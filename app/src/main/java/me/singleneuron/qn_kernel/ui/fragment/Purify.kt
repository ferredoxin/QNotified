package me.singleneuron.qn_kernel.ui.fragment

import me.singleneuron.qn_kernel.ui.gen.getAnnotatedUiItemClassList
import org.ferredoxin.ferredoxinui.common.base.ViewMap
import org.ferredoxin.ferredoxinui.common.base.loadUiInList
import org.ferredoxin.ferredoxinui.common.base.uiCategory
import org.ferredoxin.ferredoxinui.common.base.uiScreen

val Purify: ViewMap = listOf(
    uiScreen {
        name = "主页"
        contains = linkedMapOf(
            uiCategory {
                name = "主页"
            },
        )
        loadUiInList(contains, getAnnotatedUiItemClassList())
        contains
    },
    uiScreen {
        name = "侧滑"
        contains = linkedMapOf(
            uiCategory {
                name = "侧滑"
            }
        )
        loadUiInList(contains, getAnnotatedUiItemClassList())
        contains
    },
    uiScreen {
        name = "聊天"
        contains = linkedMapOf(
            uiCategory {
                name = "聊天"
            }
        )
        loadUiInList(contains, getAnnotatedUiItemClassList())
        contains
    },
    uiScreen {
        name = "群聊"
        contains = linkedMapOf(
            uiCategory {
                name = "群聊"
            }
        )
        loadUiInList(contains, getAnnotatedUiItemClassList())
        contains
    },
    uiScreen {
        name = "扩展"
        contains = linkedMapOf(
            uiCategory {
                name = "扩展"
            }
        )
        loadUiInList(contains, getAnnotatedUiItemClassList())
        contains
    },
)

