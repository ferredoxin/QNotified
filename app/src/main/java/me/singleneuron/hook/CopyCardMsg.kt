/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
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
package me.singleneuron.hook

import android.content.Context
import android.view.View
import cc.ioctl.hook.InputButtonHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.DexKit
import nil.nadph.qnotified.util.Initiator
import xyz.nextalone.util.tryOrFalse

@FunctionEntry
object CopyCardMsg : CommonDelayableHook("copyCardMsg") {
    override fun initOnce() = tryOrFalse {
        //Begin: ArkApp
        val cl_ArkAppItemBuilder = DexKit.doFindClass(DexKit.C_ARK_APP_ITEM_BUBBLE_BUILDER)
        XposedHelpers.findAndHookMethod(cl_ArkAppItemBuilder, "a", Int::class.javaPrimitiveType, Context::class.java,
            Initiator.load("com/tencent/mobileqq/data/ChatMessage"), InputButtonHook.MenuItemClickCallback())
        for (m in cl_ArkAppItemBuilder!!.declaredMethods) {
            if (!m.returnType.isArray) {
                continue
            }
            val ps = m.parameterTypes
            if (ps.size == 1 && ps[0] == View::class.java) {
                XposedBridge.hookMethod(m, InputButtonHook.GetMenuItemCallBack())
                break
            }
        }
        //End: ArkApp
        //Begin: StructMsg
        val cl_StructingMsgItemBuilder = Initiator.load(
            "com/tencent/mobileqq/activity/aio/item/StructingMsgItemBuilder")
        XposedHelpers.findAndHookMethod(cl_StructingMsgItemBuilder, "a", Int::class.javaPrimitiveType, Context::class.java,
            Initiator.load("com/tencent/mobileqq/data/ChatMessage"), InputButtonHook.MenuItemClickCallback())
        for (m in cl_StructingMsgItemBuilder.declaredMethods) {
            if (!m.returnType.isArray) {
                continue
            }
            val ps = m.parameterTypes
            if (ps.size == 1 && ps[0] == View::class.java) {
                XposedBridge.hookMethod(m, InputButtonHook.GetMenuItemCallBack())
                break
            }
        }
        //End: StructMsg
//            for (Method m : load("com.tencent.mobileqq.structmsg.StructMsgForGeneralShare").getMethods()) {
//                if (m.getName().equals("getView")) {
//                    XposedBridge.hookMethod(m, new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            View v = (View) param.getResult();
//                            View.OnLongClickListener l = getBubbleLongClickListener((Activity) param.args[0]);
//                            if (v != null && l != null) {
//                                //v.setOnLongClickListener(l);
//                            }
//                        }
//                    });
//                    break;
//                }
//            }
    }
}
