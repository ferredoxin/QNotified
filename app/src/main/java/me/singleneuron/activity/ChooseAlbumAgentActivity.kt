package me.singleneuron.activity

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import nil.nadph.qnotified.R

class ChooseAlbumAgentActivity : AppCompatActivity() {

    private val REQUEST_CODE = 1
    private val REQUEST_CODE_QQ = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.noDisplay)
        super.onCreate(savedInstanceState)
        val intent = Intent(ACTION_GET_CONTENT).apply {
            type = "*/*"
            putExtra(EXTRA_MIME_TYPES,arrayOf("image/*","video/*"))
            putExtra(EXTRA_ALLOW_MULTIPLE,true)
        }
        if (intent.resolveActivity(packageManager)!=null) {
            startActivityForResult(createChooser(intent,"选择相册应用"),REQUEST_CODE)
        }
    }

    override fun onStart() {
        super.onStart()
        setVisible(true)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==REQUEST_CODE&&resultCode== Activity.RESULT_OK) {
            val uris : ArrayList<Uri> = ArrayList()
            if (data!=null) {
                val uri = data.data
                if (uri==null) {
                    val clipData = data.clipData!!
                    for (i in 0 until clipData.itemCount) {
                        val item = clipData.getItemAt(i)
                        uris.add(item.uri)
                    }
                } else {
                    uris.add(uri)
                }
            }
            val intent = Intent().apply {
                component = ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity")
                action = ACTION_SEND_MULTIPLE
                type = "*/*"
                flags = flags or FLAG_GRANT_READ_URI_PERMISSION
                putExtra(EXTRA_MIME_TYPES,arrayOf("image/*","video/*"))
                putExtra(EXTRA_ALLOW_MULTIPLE,true)
                putParcelableArrayListExtra(EXTRA_STREAM,uris)
            }
            startActivityForResult(intent,2)
        } else if (requestCode==REQUEST_CODE_QQ) {
            finish()
        }
    }

}