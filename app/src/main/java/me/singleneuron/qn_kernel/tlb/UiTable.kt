package me.singleneuron.qn_kernel.tlb

import cc.ioctl.dialog.RikkaDialog
import me.singleneuron.qn_kernel.ui.fragment.*
import nil.nadph.qnotified.activity.TroubleshootActivity
import org.ferredoxin.ferredoxinui.common.base.*

val UiTable = uiScreen {
    name = "QNotified"
    summary = null
    contains = linkedMapOf(
        uiCategory {
            name = "模块设置"
            contains = linkedMapOf(
                uiClickableItem {
                    title = "QQ净化"
                    onClickListener = ClickToActivity(me.zpp0196.qqpurify.activity.MainActivity::class.java)
                },
                uiClickableItem {
                    title = "花Q"
                    summary = "若无另行说明, 所有功能开关都即时生效"
                    onClickListener = {
                        RikkaDialog.showRikkaFuncDialog(it)
                        true
                    }
                },
                uiClickableItem {
                    title = "净化功能"
                    onClickListener = ClickToNewPages(QNPurify)
                },
                uiClickableItem {
                    title = "增强功能"
                    onClickListener = ClickToNewSetting(QNEnhance)
                },
                uiClickableItem {
                    title = "辅助功能"
                    onClickListener = ClickToNewSetting(QNAssist)
                },
                uiClickableItem {
                    title = "其他功能"
                    onClickListener = ClickToNewSetting(QNOthers)
                }
            )
        },
        uiCategory {
            name = "其他设置"
            contains = linkedMapOf(
                uiClickableItem {
                    title = "参数设定"
                    onClickListener = ClickToNewSetting(QNConfig)
                },
                uiClickableItem {
                    title = "故障排查"
                    onClickListener = ClickToActivity(TroubleshootActivity::class.java)
                }
            )
        },
        uiCategory {
            name = "更多"
            contains = linkedMapOf(
                uiClickableItem {
                    title = "鸽子画饼"
                    onClickListener = ClickToNewSetting(QNForesee)
                },
                uiClickableItem {
                    title = "关于"
                    onClickListener = ClickToNewSetting(QNAbout)
                }
            )
        }
    )
}
