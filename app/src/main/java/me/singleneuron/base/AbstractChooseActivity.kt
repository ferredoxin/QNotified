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
package me.singleneuron.base

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nil.nadph.qnotified.activity.AppCompatTransferActivity
import nil.nadph.qnotified.ui.___WindowIsTranslucent
import java.io.File

abstract class AbstractChooseActivity : AppCompatTransferActivity(), ___WindowIsTranslucent {

    val REQUEST_CODE = this.hashCode()

    lateinit var sendCacheDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sendCacheDir = File(externalCacheDir, "SendCache")
    }

    final override fun onStart() {
        super.onStart()
        setVisible(true)
    }

    var bundle: Bundle? = null

    fun initSendCacheDir() {
        if (!sendCacheDir.exists()) {
            sendCacheDir.mkdirs()
        } else {
            for (file in sendCacheDir.listFiles()) {
                file.delete()
            }
        }
    }

    suspend fun convertUriToPath(uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            contentResolver.openInputStream(uri)?.use { input ->
                val displayName: String? = contentResolver.query(uri, null, null, null, null, null)?.run {
                    if (moveToFirst()) {
                        getString(getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    } else {
                        null
                    }
                }
                val file = File(sendCacheDir, System.currentTimeMillis().toString() + displayName)
                file.createNewFile()
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
                return@withContext file.absolutePath
            }
        }
    }

}
