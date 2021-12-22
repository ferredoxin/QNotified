package me.singleneuron.qn_kernel.ui.fragment

import android.content.Intent
import cc.ioctl.activity.JefsRulesActivity
import me.singleneuron.qn_kernel.ui.gen.getAnnotatedUiItemClassList
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.Toasts
import org.ferredoxin.ferredoxinui.common.base.*

val Config: UiScreen = uiScreen {
    name = "参数设定"
    contains = linkedMapOf(
        uiCategory {
            noTitle = true
            name = "参数设定"
            contains = linkedMapOf(
                uiClickableItem {
                    title = "跳转控制"
                    summary = "跳转自身及第三方Activity控制"
                    onClickListener = {
                        if (Initiator.load("com.tencent.mobileqq.haoliyou.JefsClass") != null) {
                            it.startActivity(Intent(it, JefsRulesActivity::class.java))
                        } else {
                            Toasts.error(it, "当前版本客户端版本不支持")
                        }
                        true
                    }
                }
            )
        }
    )
    loadUiInList(contains, getAnnotatedUiItemClassList())
    contains
}.second
