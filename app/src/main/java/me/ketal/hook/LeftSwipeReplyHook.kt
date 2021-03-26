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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import de.robv.android.xposed.XposedHelpers
import ltd.nextalone.util.hookAfter
import ltd.nextalone.util.hookBefore
import me.ketal.data.ConfigData
import me.ketal.util.BaseUtil.tryVerbosely
import me.ketal.util.PlayQQVersion
import me.ketal.util.TIMVersion
import me.singleneuron.qn_kernel.data.hostInfo
import me.singleneuron.qn_kernel.data.requireMinVersion
import me.singleneuron.qn_kernel.tlb.ConfigTable.getConfig
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.ui.ResUtils
import nil.nadph.qnotified.util.*

@FunctionEntry
object LeftSwipeReplyHook : CommonDelayableHook(
    "ketal_left_swipe_action",
    DexDeobfStep(DexKit.N_LeftSwipeReply_Helper__reply),
    DexDeobfStep(DexKit.N_BASE_CHAT_PIE__chooseMsg)
) {
    private val LEFT_SWIPE_NO_ACTION = ConfigData<Boolean>("ketal_left_swipe_noAction")
    private val LEFT_SWIPE_MULTI_CHOOSE = ConfigData<Boolean>("ketal_left_swipe_multiChoose")
    private val LEFT_SWIPE_REPLY_DISTANCE = ConfigData<Int>("ketal_left_swipe_replyDistance")
    var isNoAction: Boolean
        get() = LEFT_SWIPE_NO_ACTION.getOrDefault(false)
        set(on) {
            LEFT_SWIPE_NO_ACTION.value = on
        }
    var isMultiChose: Boolean
        get() = LEFT_SWIPE_MULTI_CHOOSE.getOrDefault(false)
        set(on) {
            LEFT_SWIPE_MULTI_CHOOSE.value = on
        }
    var replyDistance: Int
        get() = LEFT_SWIPE_REPLY_DISTANCE.getOrDefault(-1)
        set(replyDistance) {
            LEFT_SWIPE_REPLY_DISTANCE.value = replyDistance
        }
    private var img: Bitmap? = null
    private val multiBitmap: Bitmap?
        get() {
            if (img == null || img!!.isRecycled) img =
                BitmapFactory.decodeStream(ResUtils.openAsset("list_checkbox_selected_nopress.png"))
            return img
        }

    override fun isValid(): Boolean = requireMinVersion(QQVersion.QQ_8_2_6, TIMVersion.TIM_3_1_1, PlayQQVersion.PlayQQ_8_2_9)

    override fun initOnce() = tryVerbosely(false) {
        val replyMethod = DexKit.doFindMethod(DexKit.N_LeftSwipeReply_Helper__reply)
        val hookClass = replyMethod!!.declaringClass
        var methodName = if (hostInfo.isTim) "L" else "a"
        XposedHelpers.findMethodBestMatch(
            hookClass,
            methodName,
            Float::class.java,
            Float::class.java
        )
            .hookBefore(this) {
                if (isNoAction) it.result = null
            }
        ReflexUtil.findMethodByTypes_1(
            hookClass,
            Void.TYPE,
            View::class.java,
            Int::class.javaPrimitiveType
        )
            .hookBefore(this) {
                if (!isMultiChose) return@hookBefore
                val iv = it.args[0] as ImageView
                if (iv.tag == null) {
                    iv.setImageBitmap(multiBitmap)
                    iv.tag = true
                }
            }
        replyMethod.hookBefore(this) {
            if (!isMultiChose) return@hookBefore
            val message = ReflexUtil.invoke_virtual_any(it.thisObject, Initiator._ChatMessage())
            val baseChatPie =
                ReflexUtil.getFirstByType(it.thisObject, Initiator._BaseChatPie() as Class<*>)
            DexKit.doFindMethod(DexKit.N_BASE_CHAT_PIE__chooseMsg)!!.invoke(baseChatPie, message)
            it.result = null
        }
        methodName =
            if (hostInfo.isTim) getConfig(LeftSwipeReplyHook::class.java.simpleName) else "a"
        ReflexUtil.hasMethod(hookClass, methodName, Int::class.java)
            .hookAfter(this) {
                if (replyDistance <= 0) {
                    replyDistance = it.result as Int
                } else {
                    it.result = replyDistance
                }
            }
        true
    }
}
