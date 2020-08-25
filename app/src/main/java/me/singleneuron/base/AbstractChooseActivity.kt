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
        if (requestCode== REQUEST_CODE &&resultCode== Activity.RESULT_OK) {
            val contentResolver = contentResolver
            val intent = Intent().apply {
                component = ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity")
                flags = flags or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            val uris : ArrayList<Uri> = ArrayList()
            val uri : Uri
            var isImage = true
            if (data!=null) {
                if (data.clipData!=null) {
                    val clipData = data.clipData!!
                    for (i in 0 until clipData.itemCount) {
                        val item = clipData.getItemAt(i)
                        uris.add(item.uri)
                        val mime = contentResolver.getType(item.uri)
                        if (mime!=null) {
                            isImage = isImage and mime.startsWith("image",true)
                        }
                    }
                    if (!isImage) {
                        Toast.makeText(this,"多张选择必须全是图片",Toast.LENGTH_LONG).show()
                        finish()
                        return
                    }
                    intent.apply {
                        action = Intent.ACTION_SEND_MULTIPLE
                        type = "image/*"
                        putParcelableArrayListExtra(Intent.EXTRA_STREAM,uris)
                    }
                } else {
                    uri = data.data!!
                    intent.apply {
                        //Utils.logd("NoApplet getType: "+contentResolver.getType(uri))
                        action = Intent.ACTION_SEND
                        type = contentResolver.getType(uri)
                        putExtra(Intent.EXTRA_STREAM,uri)
                    }
                }
            }
            startActivity(intent)
        }
        finish()
    }

}