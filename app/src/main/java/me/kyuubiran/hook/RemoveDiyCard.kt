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
package me.kyuubiran.hook

import android.app.Activity
import xyz.nextalone.util.*
import me.singleneuron.qn_kernel.data.isTim
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.util.DexKit
import nil.nadph.qnotified.util.QQVersion

@FunctionEntry
object RemoveDiyCard : CommonDelayableHook(
    "kr_remove_diy_card",
    DexDeobfStep(DexKit.N_VasProfileTemplateController_onCardUpdate)) {

    override fun initOnce() = tryOrFalse {
        DexKit.doFindMethod(DexKit.N_VasProfileTemplateController_onCardUpdate)!!
            .hookBefore(this) {
                when (it.thisObject) {
                    is Activity -> {
                        val card = it.args[0]
                        copeCard(card)
                    }
                    else -> {
                        if (requireMinQQVersion(QQVersion.QQ_8_6_0)) {
                            val card = it.args[1].get("card")
                            copeCard(card!!)
                            return@hookBefore
                        }
                        it.result = null
                    }
                }
            }
    }

    private fun copeCard(card: Any) {
        val id = card.get("lCurrentStyleId", Long::class.java)
        if ((21L == id) or (22L == id))
            card.set("lCurrentStyleId", 0)
    }

    override fun isValid() = !isTim()
}
