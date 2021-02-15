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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.view.View
import android.widget.ImageView
import me.ketal.util.TIMVersion
import ltd.nextalone.util.hookAfter
import ltd.nextalone.util.hookBefore
import me.singleneuron.qn_kernel.data.hostInfo
import me.singleneuron.qn_kernel.data.requireMinVersion
import me.singleneuron.qn_kernel.tlb.ConfigTable.getConfig
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.ui.ResUtils
import nil.nadph.qnotified.util.*

object LeftSwipeReplyHook: CommonDelayableHook("ketal_left_swipe_action", DexDeobfStep(DexKit.N_LeftSwipeReply_Helper__reply), DexDeobfStep(DexKit.N_BASE_CHAT_PIE__chooseMsg)) {
    private const val LEFT_SWIPE_NO_ACTION = "ketal_left_swipe_noAction"
    private const val LEFT_SWIPE_MULTI_CHOOSE = "ketal_left_swipe_multiChoose"
    private const val LEFT_SWIPE_REPLY_DISTANCE = "ketal_left_swipe_replyDistance"
    private const val FLAG_REPLACE_PIC = 10001
    private var img: Bitmap? = null
    private val multiBitmap: Bitmap?
        get() {
            if (img == null || img!!.isRecycled) img = BitmapFactory.decodeStream(ResUtils.openAsset("list_checkbox_selected_nopress.png"))
            return img
        }

    override fun isValid(): Boolean = requireMinVersion(QQVersion.QQ_8_2_6,TIMVersion.TIM_3_1_1)

    override fun initOnce(): Boolean {
        return try {
            val replyMethod = DexKit.doFindMethod(DexKit.N_LeftSwipeReply_Helper__reply)
            val hookClass = replyMethod!!.declaringClass
            var methodName = if (hostInfo.isTim) "L" else "a"
            ReflexUtil.hasMethod(hookClass, methodName, Float::class.java, Float::class.java)
                .hookBefore(this) {
                    if (isNoAction) it.result = null
                }
            ReflexUtil.findMethodByTypes_1(hookClass, Void.TYPE, View::class.java, Int::class.javaPrimitiveType)
                .hookBefore(this) {
                    if (!isMultiChose) return@hookBefore
                    val iv = it.args[0] as ImageView
                    if (iv.getTag(FLAG_REPLACE_PIC) == null) {
                        iv.setImageBitmap(multiBitmap)
                        iv.setTag(FLAG_REPLACE_PIC, true)
                    }
                }
            replyMethod.hookBefore(this) {
                if (!isMultiChose) return@hookBefore
                val message = ReflexUtil.invoke_virtual_any(it.thisObject, Initiator._ChatMessage())
                val baseChatPie = ReflexUtil.getFirstByType(it.thisObject, Initiator._BaseChatPie() as Class<*>)
                DexKit.doFindMethod(DexKit.N_BASE_CHAT_PIE__chooseMsg)!!.invoke(baseChatPie, message)
                it.result = null
            }
            methodName = if (hostInfo.isTim) getConfig(LeftSwipeReplyHook::class.java.simpleName) else "a"
            ReflexUtil.hasMethod(hookClass, methodName, Int::class.java)
                .hookAfter(this) {
                    if (replyDistance <= 0) {
                        replyDistance = it.result as Int
                    } else {
                        it.result = replyDistance
                    }
                }
             true
        } catch (e: Exception) {
            Utils.log(e)
            false
        }
    }

    var isNoAction: Boolean
        get() = ConfigManager.getDefaultConfig().getBooleanOrDefault(LEFT_SWIPE_NO_ACTION, false)
        set(on) {
            putValue(LEFT_SWIPE_NO_ACTION, on)
        }
    var isMultiChose: Boolean
        get() = ConfigManager.getDefaultConfig().getBooleanOrDefault(LEFT_SWIPE_MULTI_CHOOSE, false)
        set(on) {
            putValue(LEFT_SWIPE_MULTI_CHOOSE, on)
        }
    var replyDistance: Int
        get() = ConfigManager.getDefaultConfig().getOrDefault(LEFT_SWIPE_REPLY_DISTANCE, -1) as Int
        set(replyDistance) {
            putValue(LEFT_SWIPE_REPLY_DISTANCE, replyDistance)
        }

    private fun putValue(keyName: String, obj: Any) {
        try {
            val mgr = ConfigManager.getDefaultConfig()
            mgr.allConfig[keyName] = obj
            mgr.save()
        } catch (e: Exception) {
            Utils.log(e)
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Toasts.error(hostInfo.application, e.toString() + "")
            } else {
                SyncUtils.post { Toasts.error(hostInfo.application, e.toString() + "") }
            }
        }
    }
}
