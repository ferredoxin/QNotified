package me.singleneuron.qn_kernel.ui.fragment


import cc.ioctl.dialog.RikkaDialog
import me.singleneuron.qn_kernel.ui.gen.getAnnotatedUiItemClassList
import org.ferredoxin.ferredoxinui.common.base.*

val RikkaQ: UiScreen = uiScreen {
    name = "花Q"
    contains = linkedMapOf(
        uiCategory {
            noTitle = true
            name = "花Q"
            contains = linkedMapOf(
                uiClickableItem {
                    title = "花Q"
                    summary = "若无另行说明, 所有功能开关都即时生效"
                    onClickListener = {
                        RikkaDialog.showRikkaFuncDialog(it)
                        true
                    }
                },
            )
        }
    )
    loadUiInList(contains, getAnnotatedUiItemClassList())
    contains
}.second
