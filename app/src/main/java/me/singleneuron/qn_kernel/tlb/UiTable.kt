package me.singleneuron.qn_kernel.tlb

import me.singleneuron.qn_kernel.ui.fragment.*
import nil.nadph.qnotified.activity.TroubleshootActivity
import org.ferredoxin.ferredoxinui.common.base.*

val UiTable = uiScreen {
    name = "QNotified"
    summary = null
    contains = linkedMapOf(
        ActivityRouter.title to ActivityRouter,
        uiCategory {
            name = "模块设置"
            contains = linkedMapOf(
                uiClickableItem {
                    title = "QQ净化"
                    onClickListener = ClickToActivity(me.zpp0196.qqpurify.activity.MainActivity::class.java)
                },
                uiClickableItem {
                    title = "花Q"
                    onClickListener = ClickToNewSetting(RikkaQ)
                },
                uiClickableItem {
                    title = "净化功能"
                    onClickListener = ClickToNewPages(Purify)
                },
                uiClickableItem {
                    title = "增强功能"
                    onClickListener = ClickToNewSetting(Enhance)
                },
                uiClickableItem {
                    title = "辅助功能"
                    onClickListener = ClickToNewSetting(Auxiliary)
                },
                uiClickableItem {
                    title = "其他功能"
                    onClickListener = ClickToNewSetting(Others)
                }
            )
        },
        uiCategory {
            name = "其他设置"
            contains = linkedMapOf(
                uiClickableItem {
                    title = "参数设定"
                    onClickListener = ClickToNewSetting(Config)
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
                    onClickListener = ClickToNewSetting(Foresee)
                },
                uiClickableItem {
                    title = "关于"
                    onClickListener = ClickToNewSetting(About)
                }
            )
        }
    )
}
