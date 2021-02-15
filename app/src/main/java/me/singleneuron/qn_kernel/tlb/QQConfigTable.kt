/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package me.singleneuron.qn_kernel.tlb

import me.kyuubiran.hook.AutoMosaicName
import me.nextalone.hook.HideProfileBubble
import me.nextalone.hook.HideTotalNumber
import me.singleneuron.util.QQVersion.*
import cc.ioctl.hook.ReplyNoAtHook
import cc.ioctl.hook.VasProfileAntiCrash

class QQConfigTable: ConfigTableInterface {

    override val configs: Map<String?, Map<Long, Any>> = mapOf(

//        //特征字符串："FaceManager"/"AvatarUtil"
//        NewRoundHead::class.simpleName to mapOf(
//            QQ_8_3_6 to "beft",
//            QQ_8_3_9 to "bfsw",
//            QQ_8_4_1 to "aocs",
//            QQ_8_4_5 to "aope",
//            QQ_8_4_8 to "anho",
//            QQ_8_4_10 to "aoke",
//            QQ_8_4_17 to "aowc",
//            QQ_8_4_18 to "aowc",
//            QQ_8_5_0 to "com.tencent.mobileqq.avatar.utils.AvatarUtil",
//            QQ_8_5_5 to "com.tencent.mobileqq.avatar.utils.AvatarUtil",
//        ),

//        //特征字符串："CaptureUtil"
//        ForceSystemCamera::class.simpleName to mapOf(
//            QQ_8_3_6 to "aypd",
//            QQ_8_3_9 to "babg",
//            QQ_8_4_1 to "bann",
//            QQ_8_4_5 to "bbgg",
//            QQ_8_4_8 to "babd",
//            QQ_8_4_10 to "bbhm",
//            QQ_8_4_17 to "bcmd",
//            QQ_8_4_18 to "bcmd",
//            QQ_8_5_0 to "com/tencent/mobileqq/richmedia/capture/util/CaptureUtil",
//            QQ_8_5_5 to "com/tencent/mobileqq/richmedia/capture/util/CaptureUtil"
//        ),

//        //特征字符串:"SmartDeviceProxyMgr create"
//        ForceSystemFile::class.simpleName to mapOf(
//            QQ_8_3_6 to "zyr",
//            QQ_8_3_9 to "aaxe",
//            QQ_8_4_1 to "abqn",
//            QQ_8_4_5 to "abur",
//            QQ_8_4_8 to "aara",
//            QQ_8_4_10 to "abgm",
//            QQ_8_4_17 to "abpa",
//            QQ_8_4_18 to "abpa",
//            QQ_8_5_0 to "com/tencent/device/devicemgr/SmartDeviceProxyMgr",
//            QQ_8_5_5 to "com/tencent/device/devicemgr/SmartDeviceProxyMgr",
//        ),

        // 字符串关键字 updateProfileBubbleMsgView
        HideProfileBubble::class.simpleName to mapOf(
            QQ_8_3_9 to "S",
            QQ_8_4_1 to "V",
            QQ_8_4_5 to "V",
            QQ_8_4_8 to "U",
            QQ_8_4_10 to "Y",
            QQ_8_4_17 to "Y",
            QQ_8_4_18 to "Y",
            QQ_8_5_0 to "Z",
            QQ_8_5_5 to "Z"
        ),

        VasProfileAntiCrash::class.java.simpleName to mapOf(
            QQ_8_4_1 to "azfl",
            QQ_8_4_5 to "azxy",
            QQ_8_4_8 to "aymn",
            QQ_8_4_10 to "Y",
            QQ_8_4_17 to "Y",
            QQ_8_4_18 to "Y",
            QQ_8_5_0 to "com.tencent.mobileqq.profile.ProfileCardTemplate",
            QQ_8_5_5 to "com.tencent.mobileqq.profile.ProfileCardTemplate",
        ),

        //com.tencent.mobileqq.activity.aio.core.TroopChatPie中一般是包含R.id.blz的
        HideTotalNumber::class.java.simpleName to mapOf(
            QQ_8_4_1 to "bE",
            QQ_8_4_5 to "bE",
            QQ_8_4_8 to "r",
            QQ_8_4_10 to "t",
            QQ_8_4_17 to "t",
            QQ_8_4_18 to "t",
            QQ_8_5_0 to "s",
            QQ_8_5_5 to "bz"
        ),

//        RemovePlayTogether::class.java.simpleName to mapOf(
//            QQ_8_4_8 to "agpr",
//            QQ_8_4_10 to "aghe",
//            QQ_8_4_17 to "agpr",
//            QQ_8_4_18 to "agpr",
//            QQ_8_5_0 to "com/tencent/mobileqq/activity/aio/helper/ClockInEntryHelper",
//            QQ_8_5_5 to "com/tencent/mobileqq/activity/aio/helper/ClockInEntryHelper",
//        ),
        AutoMosaicName::class.java.simpleName to mapOf(
            QQ_8_4_1 to "t",
            QQ_8_4_5 to "t",
            QQ_8_4_8 to "enableMosaicEffect",
            QQ_8_4_10 to "enableMosaicEffect",
            QQ_8_4_17 to "enableMosaicEffect",
            QQ_8_4_18 to "enableMosaicEffect",
            QQ_8_5_0 to "enableMosaicEffect",
            QQ_8_5_5 to "r",
        ),

        )

    override val rangingConfigs: Map<String?, Map<Long, Any>> = mapOf(
        ReplyNoAtHook::class.java.simpleName to mapOf(
            QQ_8_1_3 to "k",
            QQ_8_1_5 to "l",
            QQ_8_2_6 to "m",
            QQ_8_3_6 to "n",
            QQ_8_4_8 to "createAtMsg",
            QQ_8_5_5 to "l"
        ),
    )

}
