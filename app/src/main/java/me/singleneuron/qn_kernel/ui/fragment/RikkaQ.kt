package me.singleneuron.qn_kernel.ui.fragment


import me.singleneuron.qn_kernel.ui.gen.getAnnotatedUiItemClassList
import org.ferredoxin.ferredoxinui.common.base.UiScreen
import org.ferredoxin.ferredoxinui.common.base.loadUiInList
import org.ferredoxin.ferredoxinui.common.base.uiCategory
import org.ferredoxin.ferredoxinui.common.base.uiScreen

val RikkaQ: UiScreen = uiScreen {
    name = "花Q"
    contains = linkedMapOf(
        uiCategory {
            noTitle = true
            name = "花Q"
            contains = linkedMapOf(
                // todo
            )
        }
    )
    loadUiInList(contains, getAnnotatedUiItemClassList())
    contains
}.second
