package me.singleneuron.qn_kernel.ui.fragment

import me.singleneuron.qn_kernel.data.hostInfo
import nil.nadph.qnotified.util.Utils
import org.ferredoxin.ferredoxinui.common.base.*

val QNAbout: UiScreen = uiScreen {
    name = "关于"
    contains = linkedMapOf(uiAboutItem {
        title = "Ferredoxin UI——QNotified Style demo"
        icon = {
            it.getDrawable(nil.nadph.qnotified.R.drawable.icon)!!
        }
    }, uiCategory {
        noTitle = true
        contains = linkedMapOf(uiClickableItem {
            title = "用户协议"
        }, uiClickableItem {
            title = "隐私条款"
        }, uiClickableItem {
            title = "模块版本"
            clickAble = false
            subSummary = Utils.QN_VERSION_NAME
        }, uiClickableItem {
            title = "QQ版本"
            clickAble = false
            subSummary = "${hostInfo.versionName}(${hostInfo.versionCode})"
        }, uiClickableItem {
            title = "检查更新"
        })
    })
}.second
