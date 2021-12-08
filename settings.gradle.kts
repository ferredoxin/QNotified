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

include(":app", ":stub", ":compiler", ":dex-ptm", ":mmkv", ":FerredoxinUILib")
val compilerLibsDir: File = File(settingsDir, "libs")
project(":stub").projectDir = File(compilerLibsDir, "stub")
project(":compiler").projectDir = File(compilerLibsDir, "compiler")
project(":dex-ptm").projectDir = File(compilerLibsDir, "dex-ptm")
project(":mmkv").projectDir = File(compilerLibsDir, "mmkv" + File.separator + "Android")
project(":FerredoxinUILib").projectDir = File(compilerLibsDir, "FerredoxinUI" + File.separator + "FerredoxinUILib")
rootProject.name = "QNotified"
