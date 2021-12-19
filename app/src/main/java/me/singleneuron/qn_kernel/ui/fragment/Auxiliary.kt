package me.singleneuron.qn_kernel.ui.fragment

import me.singleneuron.qn_kernel.ui.gen.getAnnotatedUiItemClassList
import org.ferredoxin.ferredoxinui.common.base.UiScreen
import org.ferredoxin.ferredoxinui.common.base.loadUiInList
import org.ferredoxin.ferredoxinui.common.base.uiCategory
import org.ferredoxin.ferredoxinui.common.base.uiScreen

val Auxiliary: UiScreen = uiScreen {
    name = "辅助功能"
    contains = linkedMapOf(
        uiCategory {
            name = "辅助功能"
            noTitle = true
        }
    )
    loadUiInList(contains, getAnnotatedUiItemClassList())
    contains
}.second
