/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */

package cn.lliiooll.processors

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

@KotlinPoetKspPreview
class FunctionItemProcessor(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) :
    SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols =
            resolver.getSymbolsWithAnnotation("nil.nadph.qnotified.base.annotation.FunctionEntry")
                .filter { it is KSClassDeclaration }
                .map { it as KSClassDeclaration }
                .toList()
        if (symbols.isEmpty()) {
            logger.warn(">>>> FunctionEntry skipped because empty <<<<")
            return emptyList()
        }

        logger.warn(">>>> FunctionEntry Processing <<<<")
        val simpleNameMap = HashMap<String, String>(symbols.size)
        val mGetApi = FunSpec.builder("getAnnotatedFunctionItemClassList").run {
            addCode(CodeBlock.Builder().run {
                add("return arrayOf(«")
                symbols.forEachIndexed { index, ksClassDeclaration ->
                    if (simpleNameMap.contains(ksClassDeclaration.simpleName.asString())) {
                        logger.error("Duplicate name in FunctionEntry's simpleName: ${ksClassDeclaration.qualifiedName?.asString()?:"null"}, ${simpleNameMap[ksClassDeclaration.simpleName.asString()]}")
                    } else {
                        simpleNameMap[ksClassDeclaration.simpleName.asString()] = ksClassDeclaration.qualifiedName?.asString()
                            ?: "null"
                    }
                    val isJava = ksClassDeclaration.containingFile?.filePath?.endsWith(".java") == true
                    // logger.warn("Processing >>> $ksClassDeclaration,isJava = $isJava")
                    val typeName = ksClassDeclaration.asStarProjectedType().toTypeName()
                    val format = StringBuilder("\n%T").run {
                        if (isJava) append(".INSTANCE")
                        if (index == symbols.lastIndex) append("\n") else append(",")
                        toString()
                    }
                    add(format, typeName)
                }
                add("»)")
                build()
            })
            build()
        }
        logger.warn(">>>> FunctionEntry Processed(total: " + symbols.size + ") <<<<")
        // @file:JvmName("AnnotatedFunctionItemList")
        val annotationSpec = AnnotationSpec.builder(JvmName::class).run {
            addMember("%S", "AnnotatedFunctionItemList")
            build()
        }
        val dependencies = Dependencies(true, *(symbols.map {
            it.containingFile!!
        }.toTypedArray()))
        FileSpec.builder("nil.nadph.qnotified.gen", "AnnotatedFunctionItemList")
            .addAnnotation(annotationSpec)
            .addFunction(mGetApi)
            .build()
            .writeTo(codeGenerator, dependencies)
        return emptyList()
    }
}

@KotlinPoetKspPreview
class FunctionItemProvider : SymbolProcessorProvider {
    override fun create(
        environment: SymbolProcessorEnvironment
    ): SymbolProcessor {
        return FunctionItemProcessor(environment.codeGenerator, environment.logger)
    }
}
