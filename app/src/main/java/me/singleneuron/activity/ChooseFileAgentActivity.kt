package me.singleneuron.activity

import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import me.singleneuron.base.AbstractChooseActivity
import nil.nadph.qnotified.R

class ChooseFileAgentActivity : AbstractChooseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.noDisplay)
        super.onCreate(savedInstanceState)
        val intent = Intent(ACTION_GET_CONTENT).apply {
            type = "*/*"
            putExtra(EXTRA_ALLOW_MULTIPLE,false)
        }
        if (intent.resolveActivity(packageManager)!=null) {
            startActivityForResult(createChooser(intent,"选择文件应用"),REQUEST_CODE)
        }
    }

}