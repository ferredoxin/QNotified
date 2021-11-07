/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
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
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import java.lang.StringBuilder

@KotlinPoetKspPreview
class Dex2CProcessor(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) :
    SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols =
            resolver.getSymbolsWithAnnotation("cn.lliiooll.annotations.Dex2C")
                .filter { it is KSClassDeclaration }
                .map { it as KSClassDeclaration }
                .toList()
        if (symbols.isEmpty()) {
            logger.warn(">>>> Dex2C skipped because empty <<<<")
            return emptyList()
        }

        logger.warn(">>>> Dex2C Processing <<<<")
        val mGetApi = FunSpec.builder("getDex2CClasses").run {
            addCode(CodeBlock.Builder().run {
                add("return mutableListOf(«")
                symbols.forEachIndexed { index, ksClassDeclaration ->
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
        logger.warn(">>>> Dex2C Processed(total: " + symbols.size + ") <<<<")
        val dependencies = Dependencies(true, *(symbols.map {
            it.containingFile!!
        }.toTypedArray()))
        FileSpec.builder("cn.lliiooll.utils", "GeneratedClass")
            .addFunction(mGetApi)
            .build()
            .writeTo(codeGenerator, dependencies)
        return emptyList()
    }
}

@KotlinPoetKspPreview
class Dex2CProvider : SymbolProcessorProvider {
    override fun create(
        environment: SymbolProcessorEnvironment
    ): SymbolProcessor {
        return Dex2CProcessor(environment.codeGenerator, environment.logger)
    }
}
