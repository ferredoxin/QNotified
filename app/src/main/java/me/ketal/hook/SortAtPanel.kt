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
package me.ketal.hook

import android.view.View
import xyz.nextalone.data.TroopInfo
import xyz.nextalone.util.*
import me.ketal.util.BaseUtil.tryVerbosely
import me.singleneuron.qn_kernel.data.requireMinVersion
import me.singleneuron.qn_kernel.tlb.ConfigTable
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.util.*
import nil.nadph.qnotified.util.ReflexUtil.getFirstByType

@FunctionEntry
object SortAtPanel : CommonDelayableHook(
    "ketal_At_Panel_Hook",
    DexDeobfStep(DexKit.N_AtPanel__refreshUI),
    DexDeobfStep(DexKit.N_AtPanel__showDialogAtView)
) {
    const val sessionInfoTroopUin = "SortAtPanel.sessionInfoTroopUin"
    private var isSort: Boolean? = null
    override fun initOnce() = tryVerbosely(false) {
        val showDialogAtView = DexKit.doFindMethod(DexKit.N_AtPanel__showDialogAtView)
            ?: DexKit.doFindClass(DexKit.N_AtPanel__showDialogAtView)?.method {
                it.parameterTypes.contentEquals(arrayOf(View::class.java, String::class.java, Boolean::class.java))
            }
        showDialogAtView?.hookAfter(this) {
            isSort = (it.args[1] as String?)?.isNotEmpty()
        }
        val refreshUI = DexKit.doFindMethod(DexKit.N_AtPanel__refreshUI)
            ?: DexKit.doFindClass(DexKit.N_AtPanel__refreshUI)?.method {
                it.parameterTypes.contentEquals(arrayOf("com.tencent.mobileqq.troop.quickat.ui.SearchTask\$SearchResult".clazz))
            }
        refreshUI?.hookBefore(this) {
            if (isSort == true) return@hookBefore
            val sessionInfo = getFirstByType(it.thisObject, Initiator._SessionInfo())
            val troopInfo = TroopInfo(getTroopUin(sessionInfo))
            val list = getFirstByType(it.args[0], MutableList::class.java) as MutableList<Any>
            val isAdmin = "0" == getMemberUin(list[0])
            val admin = mutableListOf<Any>()
            for (i in list.indices) {
                val member = list[i]
                when (getMemberUin(member)) {
                    "0" -> continue
                    troopInfo.troopOwnerUin -> admin.add(0, member)
                    in troopInfo.troopAdmin!! -> admin.add(member)
                }
            }
            list.removeAll(admin)
            list.addAll(if (isAdmin) 1 else 0, admin)
        }
        true
    }

    private fun getTroopUin(sessionInfo: Any?): String? =
        sessionInfo.get("troopUin", String::class.java)
            ?: sessionInfo.get(ConfigTable.getConfig(sessionInfoTroopUin), String::class.java)

    private fun getMemberUin(member: Any?): String? =
        member.get("uin", String::class.java)
            ?: member.get("a", String::class.java)


    override fun isValid(): Boolean = requireMinVersion(QQVersion.QQ_8_1_3, TIMVersion.TIM_3_1_1, PlayQQVersion.PlayQQ_8_2_9)
}
