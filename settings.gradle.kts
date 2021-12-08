dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://maven.aliyun.com/nexus/content/groups/public/")
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://api.xposed.info/")
    }
}

include(":app", ":stub", ":compiler", ":dex-ptm", ":mmkv", ":common", ":qnotified_style")
val compilerLibsDir: File = File(settingsDir, "libs")
project(":stub").projectDir = File(compilerLibsDir, "stub")
project(":compiler").projectDir = File(compilerLibsDir, "compiler")
project(":dex-ptm").projectDir = File(compilerLibsDir, "dex-ptm")
project(":mmkv").projectDir = File(compilerLibsDir, "mmkv" + File.separator + "Android")
project(":common").projectDir = File(compilerLibsDir, "FerredoxinUI" + File.separator + "common")
project(":qnotified_style").projectDir = File(compilerLibsDir, "FerredoxinUI" + File.separator + "qnotified_style")
rootProject.name = "QNotified"
