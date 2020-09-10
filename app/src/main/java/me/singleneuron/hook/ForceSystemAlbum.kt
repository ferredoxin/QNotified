package me.singleneuron.hook

import android.content.Intent
import de.robv.android.xposed.XposedBridge
import me.singleneuron.activity.ChooseAlbumAgentActivity
import me.singleneuron.base.adapter.BaseDelayableConditionalHookAdapter
import me.singleneuron.data.PageFaultHighPerformanceFunctionCache
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.Utils

object ForceSystemAlbum : BaseDelayableConditionalHookAdapter("forceSystemFile") {

    override fun doInit(): Boolean {
        //特征字符串:"onAlbumBtnClicked"
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

    override val conditionCache: PageFaultHighPerformanceFunctionCache<Boolean> = PageFaultHighPerformanceFunctionCache { Utils.getHostVersionCode()>=QQVersion.QQ_8_3_6 }

}