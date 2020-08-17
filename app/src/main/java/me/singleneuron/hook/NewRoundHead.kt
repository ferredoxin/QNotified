package me.singleneuron.hook

import me.singleneuron.base.BaseDelayableHookAdapter
import nil.nadph.qnotified.util.Utils
import java.lang.Exception

object NewRoundHead : BaseDelayableHookAdapter("newroundhead") {
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
}