import java.io.PrintStream
import java.net.URLClassLoader
import java.nio.file.Paths

plugins {
    id("com.google.devtools.ksp") version "${Version.kotlin}-${Version.ksp}"
    id("com.android.application")
    id("kotlin-android")
}

val signingFilePath = "signing.gradle"
val performSigning = file(signingFilePath).exists()
if (performSigning) {
    apply(from = signingFilePath)
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
        versionName = "0.8.23" + (Common.getGitHeadRefsSuffix(rootProject))
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
    if (performSigning) {
        signingConfigs {
            create("release") {
                storeFile = file(signingFilePath)
                storePassword = storePassword
                keyAlias = keyAlias
                keyPassword = keyPassword
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
            if (performSigning) {
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
        additionalParameters("--allow-reserved-package-id", "--package-id", "0x75")
    }
    compileOptions {
        sourceCompatibility = Version.java
        targetCompatibility = Version.java
    }
    kotlinOptions {
        jvmTarget = Version.java.toString()
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
    implementation(project(":FerredoxinUILib"))
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
    implementation("androidx.constraintlayout:constraintlayout:2.1.2")
    implementation("androidx.browser:browser:1.4.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.afollestad.material-dialogs:core:3.3.0")
    implementation("com.afollestad.material-dialogs:input:3.3.0")
}


dependencies {
    val appCenterSdkVersion = "4.3.1"
    implementation("com.microsoft.appcenter:appcenter-analytics:${appCenterSdkVersion}")
    implementation("com.microsoft.appcenter:appcenter-crashes:${appCenterSdkVersion}")
}

dependencies {
    val lifecycle_version = "2.4.0"
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycle_version")
}

fun _execDexTail(dexPath: String): Boolean {
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
        var tmp_path = "intermediates/dex/debug/out/classes.dex"
        if ("/" != File.separator) {
            tmp_path = tmp_path.replace('/', File.separatorChar)
        }
        var tmp_f = File(project.buildDir, tmp_path)
        if (tmp_f.exists()) {
            pathList.add(tmp_f)
        }
        //3.6.x, minify
        tmp_path = "intermediates/dex/debug/shrunkDex/classes.dex"
        if ("/" != File.separator) {
            tmp_path = tmp_path.replace('/', File.separatorChar)
        }
        tmp_f = File(project.buildDir, tmp_path)
        if (tmp_f.exists()) {
            pathList.add(tmp_f)
        }
        //4.0.x single
        tmp_path = "intermediates/dex/debug/mergeDexDebug/classes.dex"
        if ("/" != File.separator) {
            tmp_path = tmp_path.replace('/', File.separatorChar)
        }
        tmp_f = File(project.buildDir, tmp_path)
        if (tmp_f.exists()) {
            pathList.add(tmp_f)
        }
        //4.0.x minify
        tmp_path = "intermediates/dex/debug/minifyDebugWithR8/classes.dex"
        if ("/" != File.separator) {
            tmp_path = tmp_path.replace('/', File.separatorChar)
        }
        tmp_f = File(project.buildDir, tmp_path)
        if (tmp_f.exists()) {
            pathList.add(tmp_f)
        }
        //end
        if (pathList.size == 0) {
            throw RuntimeException("dex not found: we only support 3.6.x, 4.0.x and 4.1.x")
        }
        for (f in pathList) {
            if (!_execDexTail(f.absolutePath)) {
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
        var tmp_path = "intermediates/dex/release/out/classes.dex"
        if ("/" != File.separator) {
            tmp_path = tmp_path.replace('/', File.separatorChar)
        }
        var tmp_f = File(project.buildDir, tmp_path)
        if (tmp_f.exists()) {
            pathList.add(tmp_f)
        }
        //3.6.x, minify
        tmp_path = "intermediates/dex/release/shrunkDex/classes.dex"
        if ("/" != File.separator) {
            tmp_path = tmp_path.replace('/', File.separatorChar)
        }
        tmp_f = File(project.buildDir, tmp_path)
        if (tmp_f.exists()) {
            pathList.add(tmp_f)
        }
        //4.0.x single
        tmp_path = "intermediates/dex/release/mergeDexRelease/classes.dex"
        if ("/" != File.separator) {
            tmp_path = tmp_path.replace('/', File.separatorChar)
        }
        tmp_f = File(project.buildDir, tmp_path)
        if (tmp_f.exists()) {
            pathList.add(tmp_f)
        }
        //4.0.x minify
        tmp_path = "intermediates/dex/release/minifyReleaseWithR8/classes.dex"
        if ("/" != File.separator) {
            tmp_path = tmp_path.replace('/', File.separatorChar)
        }
        tmp_f = File(project.buildDir, tmp_path)
        if (tmp_f.exists()) {
            pathList.add(tmp_f)
        }
        //end
        if (pathList.size == 0) {
            throw RuntimeException("dex not found: we only support 3.6.x, 4.0.x and 4.1.x")
        }
        for (f in pathList) {
            if (!_execDexTail(f.absolutePath)) {
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
            var tmp_path = "$libPath/$abi/$soName"
            if ("/" != File.separator) {
                tmp_path = tmp_path.replace('/', File.separatorChar)
            }
            val f = File(rootProject.projectDir, tmp_path)
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
            var tmp_path = "$libPath/$abi/$soName"
            if ("/" != File.separator) {
                tmp_path = tmp_path.replace('/', File.separatorChar)
            }
            val f = File(rootProject.projectDir, tmp_path)
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
