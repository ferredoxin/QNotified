package me.singleneuron.qn_kernel.ui.fragment

import cc.ioctl.activity.ExfriendListActivity
import cc.ioctl.activity.FriendlistExportActivity
import cc.ioctl.dialog.RepeaterIconSettingDialog
import cc.ioctl.hook.AddAccount
import cc.ioctl.hook.OpenProfileCard
import me.singleneuron.qn_kernel.ui.gen.getAnnotatedUiItemClassList
import nil.nadph.qnotified.activity.BetaTestFuncActivity
import nil.nadph.qnotified.activity.OmegaTestFuncActivity
import org.ferredoxin.ferredoxinui.common.base.*

val Others = uiScreen {
    name = "其他功能"
    contains = linkedMapOf(
        uiCategory {
            name = "娱乐功能"
        },
        uiCategory {
            name = "频道功能"
        },
        uiCategory {
            name = "自定义功能"
            contains = linkedMapOf(
                uiClickableItem {
                    title = "自定义+1图标"
                    onClickListener = {
                        RepeaterIconSettingDialog.createAndShowDialog(it)
                        true
                    }
                })

        },
        uiCategory {
            name = "好友列表"
            contains = linkedMapOf(
                uiClickableItem {
                    title = "打开资料卡"
                    summary = "打开指定用户或群的资料卡"
                    onClickListener = {
                        OpenProfileCard.onClick(it)
                        true
                    }
                },
                uiClickableItem {
                    title = "历史好友"
                    onClickListener = ClickToActivity(ExfriendListActivity::class.java)
                },
                uiClickableItem {
                    title = "导出历史好友列表"
                    summary = "支持csv/json格式"
                    onClickListener = ClickToActivity(FriendlistExportActivity::class.java)
                },
                uiClickableItem {
                    title = "添加账号"
                    summary = "需要手动登录, 核心代码由 JamGmilk 提供"
                    onClickListener = {
                        AddAccount.onAddAccountClick(it)
                        true
                    }
                }
            )
        },
        uiCategory {
            name = "实验性功能"
            contains = linkedMapOf(
                uiClickableItem {
                    title = "Beta测试"
                    summary = "仅用于测试稳定性"
                    onClickListener = ClickToActivity(BetaTestFuncActivity::class.java)
                },
                uiClickableItem {
                    title = "Omega测试"
                    summary = "这是个不存在的功能"
                    onClickListener = ClickToActivity(OmegaTestFuncActivity::class.java)
                }
            )
        },
    )
    loadUiInList(contains, getAnnotatedUiItemClassList())
    contains
}.second

