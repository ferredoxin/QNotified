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

package ltd.nextalone.data

import ltd.nextalone.util.clazz
import ltd.nextalone.util.get
import ltd.nextalone.util.method
import nil.nadph.qnotified.util.Utils

data class TroopInfo(val troopUin: String?) {
    private val troopInfo =
        "com.tencent.mobileqq.app.TroopManager".clazz?.method {
            it.parameterTypes.contentEquals(arrayOf(String::class.java)) && it.returnType == "com.tencent.mobileqq.data.troop.TroopInfo".clazz
        }?.invoke(Utils.getTroopManager(), troopUin)

    var troopName = troopInfo.get("troopname")
    var troopOwnerUin = troopInfo.get("troopowneruin", String::class.java)
    var troopAdmin = troopInfo.get("Administrator", String::class.java)?.split("|")
    var troopGrade = troopInfo.get("grade", Int::class.java)
}
