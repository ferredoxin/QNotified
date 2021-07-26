/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */

package me.singleneuron.qn_kernel.tlb

import android.content.Intent
import cc.ioctl.activity.ExfriendListActivity
import cc.ioctl.activity.FakeBatCfgActivity
import cc.ioctl.activity.FriendlistExportActivity
import cc.ioctl.activity.JefsRulesActivity
import cc.ioctl.dialog.RepeaterIconSettingDialog
import cc.ioctl.dialog.RikkaDialog
import cc.ioctl.hook.AddAccount
import cc.ioctl.hook.OpenProfileCard
import me.singleneuron.qn_kernel.data.hostInfo
import me.singleneuron.qn_kernel.ui.gen.AnnotatedUiItemList
import nil.nadph.qnotified.activity.*
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.Toasts
import nil.nadph.qnotified.util.Utils
import org.ferredoxin.ferredoxin_ui.base.*

typealias UiMap = MutableMap<String, UiDescription>

object UiTable : UiScreen {
    override var name: String = "QNotified"
    override var summary: String? = null
    override var contains: UiMap
        get() {
            return containsInternal
        }
        set(value) {
            throw IllegalAccessException()
        }

    private val containsInternal: UiMap by lazy {
        val map: UiMap = linkedMapOf(
            uiClickableItem {
                title = "打开旧版设置界面"
                onClickListener = ClickToActivity(SettingsActivity::class.java)
            },
            uiCategory {
                name = "希腊字母"
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
            uiCategory {
                name = "其他模块功能集合"
                contains = linkedMapOf(
                    uiClickableItem {
                        title = "花Q"
                        summary = "若无另行说明, 所有功能开关都即时生效"
                        onClickListener = {
                            RikkaDialog.showRikkaFuncDialog(it)
                            true
                        }
                    },
                    uiClickableItem {
                        title = "QQ净化[WIP]"
                        summary = "开发中……"
                        onClickListener =
                            ClickToActivity(me.zpp0196.qqpurify.activity.MainActivity::class.java)
                    }
                )
            },
            uiCategory {
                name = "净化功能"
                contains = linkedMapOf(
                    uiScreen {
                        name = "消息通知设置"
                        summary = "(不影响接收消息),屏蔽后可能仍有[橙字],但通知栏不会有通知,赞说说不提醒仅屏蔽通知栏的通知"
                    },
                    uiScreen {
                        name = "图片相关"
                        summary = "仅限QQ"
                    }
                )
            },
            uiCategory {
                name = "增强功能"
                contains = linkedMapOf(
                    uiClickableItem {
                        title = "自定义电量"
                        summary = "[QQ>=8.2.6]在线模式为我的电量时生效"
                        onClickListener = ClickToActivity(FakeBatCfgActivity::class.java)
                    }
                )
            },
            uiCategory {
                name = "自定义功能"
                contains = linkedMapOf(
                    uiClickableItem {
                        title = "自定义+1图标"
                        onClickListener = {
                            RepeaterIconSettingDialog.OnClickListener_createDialog(it)
                            true
                        }
                    }
                )
            },
            uiCategory {
                name = "辅助功能"
                contains = linkedMapOf(
                    uiClickableItem {
                        title = "更多辅助功能"
                        onClickListener = ClickToActivity(AuxFuncActivity::class.java)
                    }
                )
            },
            uiCategory {
                name = "好友列表"
                contains = linkedMapOf(
                    uiClickableItem {
                        title = "打开资料卡"
                        summary = "打开指定用户的资料卡"
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
                    }
                )
            },
            uiCategory {
                name = "其他功能"
                contains = linkedMapOf(
                    uiClickableItem {
                        title = "娱乐功能"
                        onClickListener = ClickToActivity(AmusementActivity::class.java)
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
                name = "参数设定"
                contains = linkedMapOf(
                    uiClickableItem {
                        title = "跳转控制"
                        summary = "跳转自身及第三方Activity控制"
                        onClickListener = {
                            if (Initiator.load("com.tencent.mobileqq.haoliyou.JefsClass") != null) {
                                it.startActivity(Intent(it, JefsRulesActivity::class.java))
                            } else {
                                Toasts.error(it, "当前版本客户端版本不支持")
                            }
                            true
                        }
                    }
                )
            },
            uiClickableItem {
                title = "禁用特别关心长震动"
                summary = "他女朋友都没了他也没开发这个功能"
            },
            uiCategory {
                name = "实验性功能"
            },
            uiCategory {
                name = "关于"
                contains = linkedMapOf(
                    uiClickableItem {
                        title = hostInfo.hostName
                        summary = hostInfo.versionName + "(" + hostInfo.versionCode + ")"
                    },
                    uiClickableItem {
                        title = "模块版本"
                        summary = Utils.QN_VERSION_NAME
                    },
                    uiClickableItem {
                        title = "关于模块"
                        onClickListener = ClickToActivity(AboutActivity::class.java)
                    },
                    uiClickableItem {
                        title = "用户协议"
                        summary = "《QNotified 最终用户许可协议》与《隐私条款》"
                        onClickListener = ClickToActivity(EulaActivity::class.java)
                    },
                    uiClickableItem {
                        title = "展望未来"
                        summary = "其实都还没写"
                        onClickListener = ClickToActivity(PendingFuncActivity::class.java)
                    },
                    uiClickableItem {
                        title = "开放源代码许可"
                        summary = "感谢卖动绘制图标"
                        onClickListener = ClickToActivity(LicenseActivity::class.java)
                    }
                )
            },
            uiCategory {
                name = "高级"
                contains = linkedMapOf(
                    uiClickableItem {
                        title = "故障排查"
                        onClickListener = ClickToActivity(TroubleshootActivity::class.java)
                    }
                )
            },
            uiCategory {
                name = "本软件为免费软件,请尊重开发者劳动成果,严禁倒卖\nLife feeds on negative entropy."
            }
        )
        loadUiInList(map,AnnotatedUiItemList.getAnnotatedUiItemClassList())
        map
    }
}
