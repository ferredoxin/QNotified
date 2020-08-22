package me.singleneuron.hook

import android.app.Activity
import android.content.Intent
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import me.singleneuron.activity.ChooseAlbumAgentActivity
import me.singleneuron.activity.ChooseFileAgentActivity
import me.singleneuron.base.BaseDelayableConditionalHookAdapter
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.util.Utils

object ForceSystemFile : BaseDelayableConditionalHookAdapter("forceSystemAlbum") {

    override fun doInit(): Boolean {
        val plusPanelClass = Class.forName("com.tencent.mobileqq.activity.aio.PlusPanel")
        val aaxe = Class.forName("aaxe")
        XposedHelpers.findAndHookMethod(plusPanelClass,"a",aaxe,object : XposedMethodHookAdapter() {
            override fun beforeMethod(param: MethodHookParam?) {
                val context = Utils.getApplication()
                context.startActivity(Intent(context,ChooseFileAgentActivity::class.java))
                param!!.result = null
            }
        })
        return true
    }

    override val condition: () -> Boolean
        get() = {Utils.getHostVersionCode()==QQVersion.QQ_8_3_9}

}