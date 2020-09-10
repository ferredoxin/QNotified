package me.singleneuron.hook.decorator

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import me.singleneuron.base.decorator.BaseItemBuilderFactoryHookDecorator
import nil.nadph.qnotified.util.Utils

object SimpleReceiptMessage: BaseItemBuilderFactoryHookDecorator("simpleReceiptMessage") {

    override fun doDecorate(result: Int, chatMessage: Any, param: XC_MethodHook.MethodHookParam): Boolean {
        if (result==5) {
            val id = Utils.iget_object_or_null(Utils.iget_object_or_null(param.args[param.args.size - 1], "structingMsg"), "mMsgServiceID") as Int
            if (id == 107) {
                XposedHelpers.setObjectField(chatMessage,"msg","[回执消息]")
                param.result = -1
                return true
            }
        }
        return false
    }

}