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
