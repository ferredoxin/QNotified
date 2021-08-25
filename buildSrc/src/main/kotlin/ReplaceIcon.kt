import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.*

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

abstract class ReplaceIcon : DefaultTask() {
    @TaskAction
    fun run() {
        val projectDir: File = project.projectDir
        val iconFileDirs = listOf(
            File(projectDir, "MiStyleIcons"),
            File(projectDir, "icons"),
            //File(projectDir ,"ChineseNewYearIcons")
        )
        val fileCount = iconFileDirs.fold(0) { i: kotlin.Int, file: File ->
            i + file.listFiles()!!.size
        }
        var number = Random().nextInt(fileCount)

        //for (aNumber in 0..fileCount) {
        //var number = aNumber
        var iconFile: File? = null
        for (iconFileDir in iconFileDirs) {
            if (number < iconFileDir.listFiles()!!.size) {
                iconFile = iconFileDir.listFiles()!![number]
                break
            }
            number -= iconFileDir.listFiles()!!.size
        }
        println("Select Icon: $iconFile")
        iconFile!!.copyTo(File(projectDir, "src/main/res/drawable/icon.png"), true)
        //}
    }
}
