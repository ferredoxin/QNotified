package me.singleneuron.qn_kernel.ui.fragment

import me.singleneuron.qn_kernel.data.hostInfo
import nil.nadph.qnotified.activity.EulaActivity
import nil.nadph.qnotified.activity.LicenseActivity
import nil.nadph.qnotified.util.Utils
import org.ferredoxin.ferredoxinui.common.base.*
import java.text.SimpleDateFormat
import java.util.*

val About: UiScreen = uiScreen {
    name = "关于"
    contains = linkedMapOf(uiAboutItem {
        title = "QNotified"
        icon = {
            it.getDrawable(nil.nadph.qnotified.R.drawable.icon)!!
        }
    }, uiCategory {
        noTitle = true
        contains = linkedMapOf(uiClickableItem {
            title = "用户协议与隐私条款"
            clickAble = true
            onClickListener = ClickToActivity(EulaActivity::class.java)
        }, uiClickableItem {
            title = "开源许可"
            clickAble = true
            onClickListener = ClickToActivity(LicenseActivity::class.java)
        }, uiClickableItem {
            title = "模块版本"
            clickAble = false
            subSummary = Utils.QN_VERSION_NAME
        }, uiClickableItem {
            title = "构建时间"
            clickAble = false
            subSummary = SimpleDateFormat("yyyy.MM.dd HH:mm:ss E", Locale.CHINA).format(Utils.getBuildTimestamp())
        }, uiClickableItem {
            title = "QQ版本"
            clickAble = false
            subSummary = "${hostInfo.versionName}(${hostInfo.versionCode})"
        }, uiClickableItem {
            title = "检查更新"
            clickAble = true
        })
    })
}.second
