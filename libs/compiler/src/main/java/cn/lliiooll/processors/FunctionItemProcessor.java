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

package cn.lliiooll.processors;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import nil.nadph.qnotified.base.annotation.FunctionEntry;

@AutoService(Processor.class)
@SupportedAnnotationTypes("nil.nadph.qnotified.base.annotation.FunctionEntry")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class FunctionItemProcessor extends BaseProcessor {

    private TypeMirror iProvider = null;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        System.out.println(">>>> FunctionItemProcessor init <<<<");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annos = roundEnv.getElementsAnnotatedWith(FunctionEntry.class);
        if (!annos.isEmpty()) {
            System.out.println(">>>> FunctionEntry Processing <<<<");
            ClassName absHook = ClassName.get("nil.nadph.qnotified.hook", "AbsDelayableHook");
            ClassName list = ClassName.get("java.util", "List");
            MethodSpec.Builder beyond = MethodSpec
                .methodBuilder("getAnnotatedFunctionItemClassList")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ParameterizedTypeName.get(list, absHook))
                .addStatement("$T result = new $T<>()", ParameterizedTypeName.get(list, absHook),
                    ArrayList.class);

            for (Element e : annos) {
//                System.out.println("Processing >>> " + e.toString());
                beyond.addStatement("result.add($T.INSTANCE)", e.asType());
            }
            beyond.addStatement("return result");
            TypeSpec util = TypeSpec.classBuilder("AnnotatedFunctionItemList")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(beyond.build())
                .build();
            JavaFile javaFile = JavaFile.builder("nil.nadph.qnotified.gen", util)
                .build();
            try {
                javaFile.writeTo(env.getFiler());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(">>>> FunctionEntry Processed(total: " + annos.size() + ") <<<<");
        } else {
            System.out.println(">>>> FunctionEntry skipped because empty <<<<");
        }
        return false;
    }
}
