package me.singleneuron.hook.decorator

import de.robv.android.xposed.XC_MethodHook
import me.singleneuron.base.decorator.BaseItemBuilderFactoryHookDecorator
import nil.nadph.qnotified.util.Utils

object SimpleCheckIn: BaseItemBuilderFactoryHookDecorator("qn_sign_in_as_text") {

    override fun doDecorate(result:Int,chatMessage:Any,param: XC_MethodHook.MethodHookParam): Boolean {
        if (result == 71 || result == 84) {
            param.result = -1
            return true
        } else if (result == 47) {
            val json = Utils.invoke_virtual(Utils.iget_object_or_null(param.args[param.args.size - 1], "ark_app_message"), "toAppXml", *arrayOfNulls(0)) as String
            if (json.contains("com.tencent.qq.checkin")) {
                param.result = -1
                return true
            }
        }
        return false
    }

}