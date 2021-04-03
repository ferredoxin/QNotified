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
package ltd.nextalone.hook

import ltd.nextalone.util.invoke
import ltd.nextalone.util.method
import ltd.nextalone.util.replace
import ltd.nextalone.util.tryOrFalse
import me.singleneuron.qn_kernel.data.hostInfo
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.util.DexKit

@FunctionEntry
object AutoReceiveOriginalPhoto : CommonDelayableHook(
    "na_auto_receive_origin_photo",
    SyncUtils.PROC_PEAK,
    false,
    DexDeobfStep(DexKit.C_AIOPictureView)
) {

    override fun initOnce() = tryOrFalse {
        val method: String = when {
            requireMinQQVersion(QQVersion.QQ_8_6_0) -> {
                "j"
            }
            requireMinQQVersion(QQVersion.QQ_8_5_0) -> {
                "h"
            }
            else -> {
                "I"
            }
        }
        val clz = DexKit.doFindClass(DexKit.C_AIOPictureView)
        val m: String = if (hostInfo.versionCode >= QQVersion.QQ_8_6_0) {
            "g"
        } else {
            "f"
        }
        "L${clz?.name};->$m(Z)V".method.replace(this) {
            if (it.args[0] as Boolean) {
                it.thisObject.invoke(method)
            }
        }
    }

    override fun isValid() = requireMinQQVersion(QQVersion.QQ_8_3_5)
}
