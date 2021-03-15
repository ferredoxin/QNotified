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
import me.ketal.hook.LeftSwipeReplyHook
import me.ketal.util.TIMVersion.*
import nil.nadph.qnotified.bridge.QQMessageFacade

class TIMConfigTable : ConfigTableInterface {

    override val configs: Map<String?, Map<Long, Any>> = mapOf(

    )

    override val rangingConfigs: Map<String?, Map<Long, Any>> = mapOf(

        //key:public \S* \(boolean
        QQMessageFacade::class.java.simpleName to mapOf(
            TIM_1_0_0 to "b",
            TIM_3_0_0 to "wa",
            TIM_3_1_1 to "PK",
            TIM_3_3_0 to "PO",
        ),

        ReplyNoAtHook::class.java.simpleName to mapOf(
            TIM_3_1_1 to "wg",
            TIM_3_3_0 to "wk",
        ),

        LeftSwipeReplyHook::class.java.simpleName to mapOf(
            TIM_3_1_1 to "Cg",
            TIM_3_3_0 to "Cn"
        ),
    )

}
