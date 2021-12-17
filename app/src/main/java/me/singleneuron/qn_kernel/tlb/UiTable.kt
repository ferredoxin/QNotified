package me.singleneuron.qn_kernel.tlb

import me.singleneuron.qn_kernel.ui.fragment.QNAbout
import me.singleneuron.qn_kernel.ui.fragment.QNFragment
import me.singleneuron.qn_kernel.ui.fragment.QNViewPagerFragment
import org.ferredoxin.ferredoxinui.common.base.*

val UiTable = uiScreen {
    name = "QNotified"
    summary = null
    contains = linkedMapOf(
        uiCategory {
            name = "模块设置"
            contains = linkedMapOf(
                uiClickableItem {
                    title = "净化功能"
                    onClickListener = ClickToNewPages(QNViewPagerFragment)
                },
                uiClickableItem {
                    title = "增强功能"
                    onClickListener = ClickToNewSetting(QNViewPagerFragment[0].second)
                },
                uiClickableItem {
                    title = "辅助功能"
                    onClickListener = ClickToNewSetting(QNFragment)
                },
                uiClickableItem {
                    title = "其他功能"
                    onClickListener = ClickToNewSetting(uiScreen {
                        name = "其他功能"
                    }.second)
                }
            )
        },
        uiCategory {
            name = "其他设置"
            contains = linkedMapOf(
                uiClickableItem {
                    title = "参数设定"
                },
                uiClickableItem {
                    title = "故障排查"
                }
            )
        },
        uiCategory {
            name = "更多"
            contains = linkedMapOf(
                uiClickableItem {
                    title = "鸽子画饼"
                },
                uiClickableItem {
                    title = "关于"
                    onClickListener = ClickToNewSetting(QNAbout)
                }
            )
        }
    )
}
