import org.gradle.api.Project
import java.io.File

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

object Common {

    @JvmStatic
    fun getGitHeadRefsSuffix(project: Project): String {
        // .git/HEAD描述当前目录所指向的分支信息，内容示例："ref: refs/heads/master\n"
        val headFile = File(project.rootProject.projectDir, ".git" + File.separator + "HEAD")
        if (headFile.exists()) {
            val string: String = headFile.readText(Charsets.UTF_8)
            val string1 = string.replace(Regex("""ref:|\s"""), "")
            val result = if (string1.isNotBlank() && string1.contains('/')) {
                val refFilePath = ".git" + File.separator + string1
                // 根据HEAD读取当前指向的hash值，路径示例为：".git/refs/heads/master"
                val refFile = File(project.rootProject.projectDir, refFilePath)
                // 索引文件内容为hash值+"\n"，
                // 示例："90312cd9157587d11779ed7be776e3220050b308\n"
                refFile.readText(Charsets.UTF_8).replace(Regex("""\s"""), "").subSequence(0, 7)
            } else {
                string.substring(0, 7)
            }
            println("commit_id: $result")
            return ".$result"
        } else {
            println("WARN: .git/HEAD does NOT exist")
            return ""
        }
    }

    @JvmStatic
    fun getBuildIdSuffix(): String {
        return try {
            val ciBuildId = System.getenv()["APPCENTER_BUILD_ID"]
            if (ciBuildId != null) ".$ciBuildId"
            else ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    @JvmStatic
    fun getTimeStamp(): Int {
        return (System.currentTimeMillis() / 1000L).toInt()
    }

}
