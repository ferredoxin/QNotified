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
package me.singleneuron.base

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import nil.nadph.qnotified.activity.AppCompatTransferActivity
import nil.nadph.qnotified.ui.___WindowIsTranslucent

abstract class AbstractChooseActivity : AppCompatTransferActivity(), ___WindowIsTranslucent {

    companion object {
        val REQUEST_CODE = 1
    }

    final override fun onStart() {
        super.onStart()
        setVisible(true)
    }


    final override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val contentResolver = contentResolver
            val intent = Intent().apply {
                component = ComponentName(
                    "com.tencent.mobileqq",
                    "com.tencent.mobileqq.activity.JumpActivity"
                )
                flags = flags or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            val uris: ArrayList<Uri> = ArrayList()
            val uri: Uri
            var isImage = true
            if (data != null) {
                if (data.clipData != null) {
                    val clipData = data.clipData!!
                    for (i in 0 until clipData.itemCount) {
                        val item = clipData.getItemAt(i)
                        uris.add(item.uri)
                        val mime = contentResolver.getType(item.uri)
                        if (mime != null) {
                            isImage = isImage and mime.startsWith("image", true)
                        }
                    }
                    if (!isImage) {
                        Toast.makeText(this, "多张选择必须全是图片", Toast.LENGTH_LONG).show()
                        finish()
                        return
                    }
                    intent.apply {
                        action = Intent.ACTION_SEND_MULTIPLE
                        type = "image/*"
                        putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                    }
                } else {
                    uri = data.data!!
                    intent.apply {
                        action = Intent.ACTION_SEND
                        type = contentResolver.getType(uri)
                        putExtra(Intent.EXTRA_STREAM, uri)
                    }
                }
            }
            startActivity(intent)
        }
        finish()
    }

}
