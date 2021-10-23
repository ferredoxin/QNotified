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

import xyz.nextalone.util.hookBefore
import xyz.nextalone.util.tryOrFalse
import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.base.CommonDelayAbleHookBridge
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.util.DexKit

//聊天界面顶栏群名字/好友昵称自动打码
@FunctionEntry
@UiItem
object AutoMosaicName : CommonDelayAbleHookBridge(DexDeobfStep(DexKit.N_BaseChatPie_mosaic)) {

    override fun initOnce()=tryOrFalse {
        DexKit.getMethodFromCache(DexKit.N_BaseChatPie_mosaic)?.hookBefore(this){
            it.args[0]=true
        }
    }


    override val preference = uiSwitchPreference {
        title = "昵称/群名片打码"
    }
    override val preferenceLocate: Array<String> = arrayOf("其他功能", "娱乐功能")

}
