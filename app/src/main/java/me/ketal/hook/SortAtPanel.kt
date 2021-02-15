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
package me.ketal.hook

import android.text.TextUtils
import me.ketal.util.TIMVersion
import ltd.nextalone.util.hookAfter
import ltd.nextalone.util.hookBefore
import me.singleneuron.qn_kernel.data.requireMinVersion
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.util.DexKit
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.ReflexUtil
import nil.nadph.qnotified.util.Utils
import java.util.*

object SortAtPanel: CommonDelayableHook("ketal_At_Panel_Hook", DexDeobfStep(DexKit.N_AtPanel__refreshUI), DexDeobfStep(DexKit.N_AtPanel__showDialogAtView)) {
    private var isSort = false
    override fun initOnce(): Boolean {
        return try {
            DexKit.doFindMethod(DexKit.N_AtPanel__showDialogAtView)!!.hookAfter(this) {
                val key = it.args[1] as String
                isSort = TextUtils.isEmpty(key)
            }
            DexKit.doFindMethod(DexKit.N_AtPanel__refreshUI)!!.hookBefore(this) {
                if (!isSort) return@hookBefore
                val result = it.args[0]
                val sessionInfo = ReflexUtil.getFirstByType(it.thisObject, Initiator._SessionInfo())
                val troopUin = ReflexUtil.iget_object_or_null(sessionInfo, "troopUin", String::class.java)
                    ?: ReflexUtil.iget_object_or_null(sessionInfo, "a", String::class.java)
                val clzTroopInfo = Initiator.load("com.tencent.mobileqq.data.troop.TroopInfo")
                    ?: Initiator.load("com.tencent.mobileqq.data.TroopInfo")
                val troopInfo = ReflexUtil.invoke_virtual(Utils.getTroopManager(), "b", troopUin, String::class.java, clzTroopInfo)
                val ownerUin = ReflexUtil.iget_object_or_null(troopInfo, "troopowneruin", String::class.java)
                val admin = ReflexUtil.iget_object_or_null(troopInfo, "Administrator", String::class.java).split("|")
                val list = ReflexUtil.getFirstByType(result, MutableList::class.java) as MutableList<Any>
                var uin = getUin(list[0])
                val isAdmin = "0" == uin
                for (i in 1 until list.size) {
                    val member = list[i]
                    uin = getUin(member) ?: throw NullPointerException("uin == null")
                    if (uin == ownerUin) {
                        list.remove(member)
                        list.add(if (isAdmin) 1 else 0, member)
                    } else if (admin.contains(uin)) {
                        list.remove(member)
                        list.add(if (isAdmin) 2 else 1, member)
                    }
                }
            }
            true
        } catch (e: Exception) {
            Utils.log(e)
            false
        }
    }

    private fun getUin(member: Any): String? {
        val uin = ReflexUtil.iget_object_or_null(member, "uin", String::class.java)
            ?: ReflexUtil.iget_object_or_null(member, "a", String::class.java)
        try {
            uin!!.toLong()
        } catch (e: Exception) {
            return null
        }
        return uin
    }

    override fun isValid(): Boolean = requireMinVersion(QQVersion.QQ_8_1_3, TIMVersion.TIM_3_1_1)
}
