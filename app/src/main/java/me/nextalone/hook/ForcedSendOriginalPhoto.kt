package me.nextalone.hook

import android.view.View
import android.widget.CheckBox
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.kyuubiran.util.getMethods
import me.singleneuron.qn_kernel.tlb.ConfigTable
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method

object ForcedSendOriginalPhoto : CommonDelayableHook("na_test_forced_original") {

    override fun initOnce(): Boolean {
//        Utils.logi("TestForcedOriginal: Init hook forced original")
        return try {
            for (m: Method in getMethods("com.tencent.mobileqq.activity.aio.photo.PhotoListPanel")) {
                val argt = m.parameterTypes
                if (m.name == "a" && argt.size == 1 && argt[0] == Boolean::class.java) {
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam?) {
                            if (LicenseStatus.sDisableCommonHooks) return
                            if (!isEnabled) return
                            val ctx = param!!.thisObject as View
                            val sendOriginPhotoCheckbox: CheckBox = ctx.findViewById(ConfigTable.getConfig(ForcedSendOriginalPhoto::class.simpleName))
                            sendOriginPhotoCheckbox.isChecked = true
//                            Utils.logd("TestForcedOriginal: Return checkbox isChecked" + sendOriginPhotoCheckbox.isChecked)
//                            Utils.logd("TestForcedOriginal: Set checkbox checked to send original photo")
                        }
                    })
                }
            }//com.tencent.mobileqq.activity.photo.album.AbstractPhotoPreviewActivity
//            for (m: Method in getMethods("com.tencent.mobileqq.activity.photo.album.AbstractPhotoPreviewActivity")) {
//                val argt = m.parameterTypes
//                if (m.name == "onCreate" && argt.size == 1) {
//                    logi("TestForcedOriginal: Found AbstractPhotoPreviewActivity onCreate")
//                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
//                        @SuppressLint("ResourceType")
//                        override fun afterHookedMethod(param: MethodHookParam?) {
//                            if (LicenseStatus.sDisableCommonHooks) return
////                            if (!isEnabled) return
//                            logi("TestForcedOriginal: AbstractPhotoPreviewActivity onCreate")
//                            val ctx = param!!.thisObject as View
//                            val sendOriginPhotoCheckbox: CheckBox = ctx.findViewById(ConfigTable.getConfig(ForcedSendOriginalPhoto::class.simpleName))
////                           val sendOriginPhotoCheckbox: CheckBox = ctx.findViewById(2131375226)
////                            sendOriginPhotoCheckbox.isChecked = true
//                            Utils.logd("TestForcedOriginal: Return checkbox isChecked " + sendOriginPhotoCheckbox.isChecked)
////                            Utils.logd("TestForcedOriginal: Set checkbox checked to send original photo")
//                        }
//                    })
//                }
//            }
//            for (m: Method in getMethods("com.tencent.mobileqq.activity.photo.album.PhotoPreviewLogicDefault")) {
//                val argt = m.parameterTypes
//                if (m.name == "initUI" && argt.isEmpty()) {
//                    logi("TestForcedOriginal: Found PhotoPreviewLogicDefault")
//                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
//                        @SuppressLint("ResourceType")
//                        override fun afterHookedMethod(param: MethodHookParam?) {
//                            if (LicenseStatus.sDisableCommonHooks) return
////                            if (!isEnabled) return
//                            logi("TestForcedOriginal: PhotoPreviewLogicDefault invoked")
//                            val ctx = param!!.thisObject as View
//                            val sendOriginPhotoCheckbox: CheckBox = ctx.findViewById(ConfigTable.getConfig(ForcedSendOriginalPhoto::class.simpleName))
////                           val sendOriginPhotoCheckbox: CheckBox = ctx.findViewById(2131375226)
//                            sendOriginPhotoCheckbox.isChecked = true
//                            Utils.logd("TestForcedOriginal: Return checkbox isChecked " + sendOriginPhotoCheckbox.isChecked)
////                            Utils.logd("TestForcedOriginal: Set checkbox checked to send original photo")
//                        }
//                    })
//                }
//            }
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }
}
