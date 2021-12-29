/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
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

package me.singleneuron.qn_kernel.ui.fragment

import me.singleneuron.qn_kernel.ui.gen.getAnnotatedUiItemClassList
import org.ferredoxin.ferredoxinui.common.base.*

val Foresee: UiScreen = uiScreen {
    name = "鸽子画饼"
    contains = linkedMapOf(
        uiCategory {
            noTitle = true
            contains = linkedMapOf(
                uiClickableItem {
                    title = "禁用特别关心长震动"
                    summary = "他女朋友都没了他也没开发这个功能"
                    valid = false
                },
                uiSwitchItem {
                    title = "无视QQ电话与语音冲突"
                    summary = "允许在QQ电话时播放语音和短视频"
                    value.value = false
                    valid = false
                },
                uiSwitchItem {
                    title = "QQ电话关麦时解除占用"
                    summary = "再开麦时如麦被其他程序占用可能崩溃"
                    value.value = false
                    valid = false
                },
                uiSwitchItem {
                    title = "QQ视频通话旋转锁定"
                    summary = "可在通话界面设置旋转方向"
                    value.value = false
                    valid = false
                },
                uiSwitchItem {
                    title = "隐藏联系人"
                    summary = "和自带的\"隐藏会话\"有所不同"
                    value.value = false
                    valid = false
                },
                uiSwitchItem {
                    title = "自定义本地头像"
                    summary = "仅本机生效"
                    value.value = false
                    valid = false
                },
                uiSwitchItem {
                    title = "高级通知设置"
                    summary = "通知展开, channel等"
                    value.value = false
                    valid = false
                },
                uiSwitchItem {
                    title = "QQ电话睡眠模式"
                    summary = "仅保持连麦, 暂停消息接收, 减少电量消耗"
                    value.value = false
                    valid = false
                },
                uiSwitchItem {
                    title = "禁用QQ公交卡"
                    summary = "如果QQ在后台会干扰NFC的话"
                    value.value = false
                    valid = false
                },
                uiSwitchItem {
                    title = "AddFriendReq.sourceID"
                    summary = "自定义加好友来源"
                    value.value = false
                    valid = false
                },
                uiSwitchItem {
                    title = "DelFriendReq.delType"
                    summary = "只能为1或2"
                    value.value = false
                    valid = false
                },
                uiSwitchItem {
                    title = "隐藏聊天界面右侧滑条"
                    summary = "强迫症专用"
                    value.value = false
                    valid = false
                },
                uiSwitchItem {
                    title = "复制群公告"
                    summary = "希望能在关键时刻帮到你"
                    value.value = false
                    valid = false
                },
                uiSwitchItem {
                    title = "一键已读/去除批量已读动画"
                    value.value = false
                    valid = false
                },
                uiSwitchItem {
                    title = "取消聊天中开通会员提示"
                    summary = "如果我们能触发关键词的话"
                    value.value = false
                    valid = false
                },
                uiSwitchItem {
                    title = "空间说说自动回赞"
                    summary = "真正的友谊应该手动点"
                    value.value = false
                    valid = false
                },
                uiSwitchItem {
                    title = "一键退出已封禁群聊"
                    value.value = false
                    valid = false
                },
            )
        }
    )
    loadUiInList(contains, getAnnotatedUiItemClassList())
    contains
}.second
