package me.singleneuron.hook

import android.app.Activity
import android.content.Intent
import de.robv.android.xposed.XposedBridge
import me.singleneuron.activity.ChooseAlbumAgentActivity
import me.singleneuron.base.BaseDelayableConditionalHookAdapter
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.util.Utils

object ForceSystemAlbum : BaseDelayableConditionalHookAdapter("forceSystemFile") {

    override fun doInit(): Boolean {
        //val albumClass = Class.forName("com.tencent.mobileqq.activity.photo.album.NewList")

        val photoListPanelClass = Class.forName("com.tencent.mobileqq.activity.aio.photo.PhotoListPanel")
        XposedBridge.hookAllMethods(photoListPanelClass,"e",object : XposedMethodHookAdapter() {
            override fun beforeMethod(param: MethodHookParam?) {
                val context = Utils.getApplication()
                context.startActivity(Intent(context,ChooseAlbumAgentActivity::class.java))
                param!!.result = null
            }
        })
        return true
    }

    override val condition: () -> Boolean
        get() = {Utils.getHostVersionCode()==QQVersion.QQ_8_3_9}

}