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

import java.io.PrintStream
import java.net.URLClassLoader
import java.nio.file.Paths

plugins {
    id("com.google.devtools.ksp") version "${Version.kotlin}-${Version.ksp}"
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 31
    buildToolsVersion = "31.0.0"
    ndkVersion = "21.4.7075529"
    defaultConfig {
        applicationId = "nil.nadph.qnotified"
        minSdk = 21
        targetSdk = 31
        versionCode = Common.getTimeStamp()
        // versionName = major.minor.accumulation.commit_id
        versionName = "1.0.1" + (Common.getGitHeadRefsSuffix(rootProject))
        multiDexEnabled = false
        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
            //drop "mips" , "armeabi",
        }
        externalNativeBuild {
            cmake {
                arguments("-DQNOTIFIED_VERSION=$versionName")
            }
        }
    }
    if (System.getenv("KEYSTORE_PATH") != null) {
        signingConfigs {
            create("release") {
                storeFile = file(System.getenv("KEYSTORE_PATH"))
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
                enableV1Signing = true
                enableV2Signing = true
            }
        }
    }
    buildTypes {
        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
            if (System.getenv("KEYSTORE_PATH") != null) {
                signingConfig = signingConfigs.getByName("release")
            }
            tasks.forEach {
                if (it.name.contains("lint")) {
                    it.enabled = false
                }
            }
            kotlinOptions.suppressWarnings = true
        }
        getByName("debug") {
            isShrinkResources = true
            isMinifyEnabled = true
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
        create("CI") {
            initWith(getByName("debug"))
            isShrinkResources = false
            isMinifyEnabled = false
            signingConfig = null
            ndk {
                abiFilters.clear()
                abiFilters.add("arm64-v8a")
            }
            matchingFallbacks += listOf("debug")
            tasks.forEach {
                if (it.name.contains("lint")) {
                    it.enabled = false
                }
            }
            kotlinOptions.suppressWarnings = true
        }
    }
    androidResources {
        additionalParameters("--allow-reserved-package-id", "--package-id", "0x37")
    }
    compileOptions {
        sourceCompatibility = Version.java
        targetCompatibility = Version.java
    }
    kotlinOptions {
        jvmTarget = Version.java.toString()
        compileOptions {
            kotlinOptions.freeCompilerArgs += "-Xmulti-platform"
        }
    }
    // Encapsulates your external native build configurations.
    externalNativeBuild {
        // Encapsulates your CMake build configurations.
        cmake {
            // Provides a relative path to your CMake build script.
            path = File(projectDir, "src/main/cpp/CMakeLists.txt")
            version = "3.10.2"
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

kotlin {
    sourceSets.debug {
        kotlin.srcDir("build/generated/ksp/debug/kotlin")
    }
    sourceSets.release {
        kotlin.srcDir("build/generated/ksp/release/kotlin")
    }
}
dependencies {
    compileOnly(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))
    compileOnly(project(":stub"))
    implementation(project(":mmkv"))
    implementation(project(":common"))
    implementation(project(":qnotified_style"))
    //add("kspAndroid", project(":compiler"))
    ksp(project(":compiler"))
    compileOnly("de.robv.android.xposed:api:82")
    implementation("com.jaredrummler:colorpicker:1.1.0")
    implementation("de.psdev.licensesdialog:licensesdialog:2.2.0")
    implementation("io.noties.markwon:core:4.6.2")
    implementation(kotlin("stdlib", Version.kotlin))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    // 脚本解析
    implementation("org.apache-extras.beanshell:bsh:2.0b6")
    // androidx
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.browser:browser:1.4.0")
    implementation("com.google.android.material:material:1.5.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.afollestad.material-dialogs:core:3.3.0")
    implementation("com.afollestad.material-dialogs:input:3.3.0")
}


dependencies {
    val appCenterSdkVersion = "4.4.2"
    implementation("com.microsoft.appcenter:appcenter-analytics:${appCenterSdkVersion}")
    implementation("com.microsoft.appcenter:appcenter-crashes:${appCenterSdkVersion}")
}

dependencies {
    val lifecycleVersion = "2.4.0"
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")
}

fun execDexTail(dexPath: String): Boolean {
    val cl = URLClassLoader(arrayOf(Paths.get(rootProject.projectDir.absolutePath, "libs", "dex-ptm", "build", "classes", "java", "main").toUri().toURL()))
    try {
        val time = cl.loadClass("cc.ioctl.dextail.HexUtils").getMethod("getTimeAsByteArray").invoke(null) as ByteArray
        val stdout: PrintStream = System.out
        return cl.loadClass("cc.ioctl.dextail.Main").getMethod("checkAndUpdateTail", String::class.java, ByteArray::class.java, Boolean::class.javaPrimitiveType, PrintStream::class.java)
            .invoke(null, dexPath, time, true, stdout) as Boolean
    } catch (ie: java.lang.reflect.InvocationTargetException) {
        throw(ie.cause!!)
    }
}
tasks.register("dexTailDebug") {
    dependsOn(":dex-ptm:assemble")
    doLast {
        println("dexTailDebug.doLast invoked")
        val pathList: ArrayList<File> = arrayListOf()
        //3.6.x, plain
        var tmpPath = "intermediates/dex/debug/out/classes.dex"
        if ("/" != File.separator) {
            tmpPath = tmpPath.replace('/', File.separatorChar)
        }
        var tmpF = File(project.buildDir, tmpPath)
        if (tmpF.exists()) {
            pathList.add(tmpF)
        }
        //3.6.x, minify
        tmpPath = "intermediates/dex/debug/shrunkDex/classes.dex"
        if ("/" != File.separator) {
            tmpPath = tmpPath.replace('/', File.separatorChar)
        }
        tmpF = File(project.buildDir, tmpPath)
        if (tmpF.exists()) {
            pathList.add(tmpF)
        }
        //4.0.x single
        tmpPath = "intermediates/dex/debug/mergeDexDebug/classes.dex"
        if ("/" != File.separator) {
            tmpPath = tmpPath.replace('/', File.separatorChar)
        }
        tmpF = File(project.buildDir, tmpPath)
        if (tmpF.exists()) {
            pathList.add(tmpF)
        }
        //4.0.x minify
        tmpPath = "intermediates/dex/debug/minifyDebugWithR8/classes.dex"
        if ("/" != File.separator) {
            tmpPath = tmpPath.replace('/', File.separatorChar)
        }
        tmpF = File(project.buildDir, tmpPath)
        if (tmpF.exists()) {
            pathList.add(tmpF)
        }
        //end
        if (pathList.size == 0) {
            throw RuntimeException("dex not found: we only support 3.6.x, 4.0.x and 4.1.x")
        }
        for (f in pathList) {
            if (!execDexTail(f.absolutePath)) {
                throw RuntimeException("DedxTail returned false")
            }
        }
    }
}

tasks.register("dexTailRelease") {
    dependsOn(":dex-ptm:assemble")
    doLast {
        println("dexTailRelease.doLast invoked")
        val pathList: ArrayList<File> = arrayListOf()
        //3.6.x single?
        var tmpPath = "intermediates/dex/release/out/classes.dex"
        if ("/" != File.separator) {
            tmpPath = tmpPath.replace('/', File.separatorChar)
        }
        var tmpF = File(project.buildDir, tmpPath)
        if (tmpF.exists()) {
            pathList.add(tmpF)
        }
        //3.6.x, minify
        tmpPath = "intermediates/dex/release/shrunkDex/classes.dex"
        if ("/" != File.separator) {
            tmpPath = tmpPath.replace('/', File.separatorChar)
        }
        tmpF = File(project.buildDir, tmpPath)
        if (tmpF.exists()) {
            pathList.add(tmpF)
        }
        //4.0.x single
        tmpPath = "intermediates/dex/release/mergeDexRelease/classes.dex"
        if ("/" != File.separator) {
            tmpPath = tmpPath.replace('/', File.separatorChar)
        }
        tmpF = File(project.buildDir, tmpPath)
        if (tmpF.exists()) {
            pathList.add(tmpF)
        }
        //4.0.x minify
        tmpPath = "intermediates/dex/release/minifyReleaseWithR8/classes.dex"
        if ("/" != File.separator) {
            tmpPath = tmpPath.replace('/', File.separatorChar)
        }
        tmpF = File(project.buildDir, tmpPath)
        if (tmpF.exists()) {
            pathList.add(tmpF)
        }
        //end
        if (pathList.size == 0) {
            throw RuntimeException("dex not found: we only support 3.6.x, 4.0.x and 4.1.x")
        }
        for (f in pathList) {
            if (!execDexTail(f.absolutePath)) {
                throw RuntimeException("DedxTail returned false")
            }
        }
    }
}


tasks.register("checkTargetNativeLibsDebug") {
    dependsOn(":app:externalNativeBuildDebug")
    doLast {
        val targetAbi = listOf("arm64-v8a", "armeabi-v7a")
        val soName = "libnatives.so"
        val libPath = "app/build/intermediates/cmake/debug/obj"
        for (abi in targetAbi) {
            var tmpPath = "$libPath/$abi/$soName"
            if ("/" != File.separator) {
                tmpPath = tmpPath.replace('/', File.separatorChar)
            }
            val f = File(rootProject.projectDir, tmpPath)
            if (!f.exists()) {
                throw IllegalStateException(" Native library missing for the target abi: $abi. Please run gradle task ':app:externalNativeBuildDebug' manually to force android gradle plugin to satisfy all required ABIs.")
            }
        }
    }
}
tasks.register("checkTargetNativeLibsRelease") {
    dependsOn(":app:externalNativeBuildRelease")
    doLast {
        val targetAbi = listOf("arm64-v8a", "armeabi-v7a")
        val soName = "libnatives.so"
        val libPath = "app/build/intermediates/cmake/release/obj"
        for (abi in targetAbi) {
            var tmpPath = "$libPath/$abi/$soName"
            if ("/" != File.separator) {
                tmpPath = tmpPath.replace('/', File.separatorChar)
            }
            val f = File(rootProject.projectDir, tmpPath)
            if (!f.exists()) {
                throw IllegalStateException("Native library missing for the target abi: $abi.\nPlease run gradle task ':app:externalNativeBuildRelease' manually to force android gradle plugin to satisfy all required ABIs.")
            }
        }
    }
}
tasks.register<ReplaceIcon>("replaceIcon")
tasks.getByName("preBuild").dependsOn(tasks.getByName("replaceIcon"))

tasks.whenTaskAdded {
    if (this.name == "assembleDebug") {
        this.dependsOn(tasks.getByName("dexTailDebug"))
    }
    if (this.name == "mergeDexDebug") {
        tasks.getByName("dexTailDebug").dependsOn(this)
    }
    if (this.name == "stripDebugDebugSymbols") {
        tasks.getByName("dexTailDebug").mustRunAfter(this)
    }
    if (this.name == "dexBuilderDebug" || this.name == "mergeExtDexDebug"
        || this.name == "mergeLibDexDebug" || this.name == "mergeProjectDexDebug"
        || this.name == "shrinkDebugRes" || this.name.startsWith("minifyDebug")) {
        tasks.getByName("dexTailDebug").mustRunAfter(this)
    }
    if (this.name == "assembleRelease") {
        this.dependsOn(tasks.getByName("dexTailRelease"))
    }
    if (this.name == "mergeDexRelease") {
        tasks.getByName("dexTailRelease").dependsOn(this)
    }
    if (this.name == "stripReleaseDebugSymbols") {
        tasks.getByName("dexTailRelease").mustRunAfter(this)
    }
    if (this.name == "dexBuilderRelease" || this.name == "mergeExtDexRelease"
        || this.name == "mergeLibDexRelease" || this.name == "mergeProjectDexRelease"
        || this.name == "shrinkReleaseRes" || this.name.startsWith("minifyRelease")) {
        tasks.getByName("dexTailRelease").mustRunAfter(this)
    }
    if (this.name == "packageDebug") {
        this.dependsOn(tasks.getByName("checkTargetNativeLibsDebug"))
    }
    if (this.name == "packageRelease") {
        this.dependsOn(tasks.getByName("checkTargetNativeLibsRelease"))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    if (name.contains("release", true)) {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-Xno-call-assertions",
                "-Xno-receiver-assertions",
                "-Xno-param-assertions",
            )
        }
    }
}
