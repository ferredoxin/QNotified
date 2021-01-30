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

import me.ketal.hook.LeftSwipeReplyHook
import me.ketal.hook.MultiActionHook
import me.ketal.util.TIMVersion.*
import nil.nadph.qnotified.bridge.QQMessageFacade
import nil.nadph.qnotified.hook.ReplyNoAtHook

class TIMConfigTable: ConfigTableInterface {

    override val configs: Map<String?, Map<Long, Any>> = mapOf(

    )

    override val rangingConfigs: Map<String?, Map<Long, Any>> = mapOf(

            MultiActionHook::class.java.simpleName to mapOf(
                    TIM_1_0_0 to "a",
                    TIM_3_0_0 to "kqr",
                    TIM_3_0_0_1 to "kqy",
                    TIM_3_1_1 to "hd",
            ),

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
