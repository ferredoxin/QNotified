package me.singleneuron.qn_kernel.ui.fragment

import me.singleneuron.qn_kernel.ui.gen.getAnnotatedUiItemClassList
import org.ferredoxin.ferredoxinui.common.base.UiScreen
import org.ferredoxin.ferredoxinui.common.base.loadUiInList
import org.ferredoxin.ferredoxinui.common.base.uiCategory
import org.ferredoxin.ferredoxinui.common.base.uiScreen

val Enhance: UiScreen = uiScreen {
    name = "增强功能"
    contains = linkedMapOf(
        uiCategory {
            noTitle = true
            name = "增强功能"
        }
    )
    loadUiInList(contains, getAnnotatedUiItemClassList())
    contains
}.second
