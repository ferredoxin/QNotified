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

import cc.ioctl.hook.ReplyNoAtHook
import cc.ioctl.hook.VasProfileAntiCrash
import ltd.nextalone.hook.ChatWordsCount
import me.kyuubiran.hook.AutoMosaicName
import nil.nadph.qnotified.util.QQVersion.*

class QQConfigTable : ConfigTableInterface {

    override val configs: Map<String?, Map<Long, Any>> = mapOf(
        VasProfileAntiCrash::class.java.simpleName to mapOf(
            QQ_8_4_1 to "azfl",
            QQ_8_4_5 to "azxy",
            QQ_8_4_8 to "aymn",
            QQ_8_4_10 to "Y",
            QQ_8_4_17 to "Y",
            QQ_8_4_18 to "Y",
            QQ_8_5_0 to "com.tencent.mobileqq.profile.ProfileCardTemplate",
            QQ_8_5_5 to "com.tencent.mobileqq.profile.ProfileCardTemplate",
            QQ_8_6_0 to "com.tencent.mobileqq.profilecard.vas.component.template.VasProfileTemplateComponent",
            QQ_8_6_5 to "com.tencent.mobileqq.profilecard.vas.component.template.VasProfileTemplateComponent",
            QQ_8_7_0 to "com.tencent.mobileqq.profilecard.vas.component.template.VasProfileTemplateComponent",
            QQ_8_7_5 to "com.tencent.mobileqq.profilecard.vas.component.template.VasProfileTemplateComponent",
        ),

        AutoMosaicName::class.java.simpleName to mapOf(
            QQ_8_4_1 to "t",
            QQ_8_4_5 to "t",
            QQ_8_4_8 to "enableMosaicEffect",
            QQ_8_4_10 to "enableMosaicEffect",
            QQ_8_4_17 to "enableMosaicEffect",
            QQ_8_4_18 to "enableMosaicEffect",
            QQ_8_5_0 to "enableMosaicEffect",
            QQ_8_5_5 to "r",
            QQ_8_6_0 to "k",
            QQ_8_6_5 to "k",
            QQ_8_7_0 to "k",
            QQ_8_7_5 to "k",
        ),

        )

    override val rangingConfigs: Map<String?, Map<Long, Any>> = mapOf(
        ReplyNoAtHook::class.java.simpleName to mapOf(
            QQ_8_1_3 to "k",
            QQ_8_1_5 to "l",
            QQ_8_2_6 to "m",
            QQ_8_3_6 to "n",
            QQ_8_4_8 to "createAtMsg",
            QQ_8_5_5 to "l",
            QQ_8_6_0 to "__NOT_USED__",
        ),
        ChatWordsCount::class.java.simpleName to mapOf(
            QQ_8_5_0 to "ivc",
            QQ_8_6_5 to "mvm",
            QQ_8_7_0 to "mxh",
            QQ_8_7_5 to "mxn",
        )
    )

}
