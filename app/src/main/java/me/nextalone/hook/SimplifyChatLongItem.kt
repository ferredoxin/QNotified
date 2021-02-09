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
package me.nextalone.hook

import de.robv.android.xposed.XC_MethodHook
import me.nextalone.util.Utils.hook
import me.nextalone.util.Utils.method
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Utils

object SimplifyChatLongItem : CommonDelayableHook("na_simplify_chat_long_item_kt", SyncUtils.PROC_MAIN, false) {

    override fun initOnce(): Boolean {
        return try {
            val method = ("Lcom/tencent/mobileqq/utils/dialogutils/QQCustomMenu;->a(ILjava/lang/String;II)V").method
            method.hook(object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val string = param.args[1] as String
                    if (string == "\u4e00\u8d77\u5199" || string == "\u79c1\u804a" || string == "\u76f8\u5173\u8868\u60c5" || string == "\u5f85\u529e") {
                        param.result = null
                    }
                }
            })
            val method2 = ("Lcom/tencent/mobileqq/utils/dialogutils/QQCustomMenu;->a(ILjava/lang/String;I)V").method
            method2.hook(object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val string = param.args[1] as String
                    if (string == "\u4e00\u8d77\u5199" || string == "\u79c1\u804a" || string == "\u76f8\u5173\u8868\u60c5" || string == "\u5f85\u529e") {
                        param.result = null
                    }
                }
            })
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }
}
