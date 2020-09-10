package me.singleneuron.hook

import me.singleneuron.base.adapter.BaseDelayableHookAdapter

object CopyCardMsg : BaseDelayableHookAdapter("copyCardMsg") {
    override fun doInit(): Boolean {
        return true
    }
}