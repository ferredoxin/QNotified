package me.singleneuron.hook

import me.singleneuron.base.BaseDelayableHookAdapter

object CopyCardMsg : BaseDelayableHookAdapter("copyCardMsg") {
    override fun doInit(): Boolean {
        return true
    }
}