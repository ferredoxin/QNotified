package me.singleneuron.hook

import me.singleneuron.base.BaseDelayableConditionalHookAdapter
import me.singleneuron.base.BaseDelayableHookAdapter
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.Utils
import java.lang.Exception

object NewRoundHead : BaseDelayableConditionalHookAdapter("newroundhead") {
    override fun doInit(): Boolean {
        try {
            Utils.logd("NewRoundHead loaded")
            NewRoundHeadInternal.hook(isEnabled)
        }catch (e:Exception) {
            Utils.log(e)
            return false
        }
        return true
    }

    override val condition: () -> Boolean
        get() = {Utils.getHostVersionCode()==QQVersion.QQ_8_3_9}
}