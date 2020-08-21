package me.singleneuron.activity

import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import me.singleneuron.base.AbstractChooseActivity
import nil.nadph.qnotified.R

class ChooseAlbumAgentActivity : AbstractChooseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.noDisplay)
        super.onCreate(savedInstanceState)
        val intent = Intent(ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(EXTRA_ALLOW_MULTIPLE,true)
        }
        if (intent.resolveActivity(packageManager)!=null) {
            startActivityForResult(createChooser(intent,"选择相册应用"),REQUEST_CODE)
        }
    }

}