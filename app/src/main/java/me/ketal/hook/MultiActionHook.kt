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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.tencent.mobileqq.app.BaseActivity
import ltd.nextalone.util.hookAfter
import me.ketal.util.BaseUtil.tryVerbosely
import nil.nadph.qnotified.R
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.bridge.QQMessageFacade
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.ui.ResUtils
import nil.nadph.qnotified.util.DexKit
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.ReflexUtil

@FunctionEntry
object MultiActionHook : CommonDelayableHook(
    "qn_multi_action",
    DexDeobfStep(DexKit.C_MessageCache),
    DexDeobfStep(DexKit.C_MSG_REC_FAC),
    DexDeobfStep(DexKit.N_BASE_CHAT_PIE__createMulti),
    DexDeobfStep(DexKit.C_MultiMsg_Manager)
) {
    private var baseChatPie: Any? = null
    private var img: Bitmap? = null
    private val recallBitmap: Bitmap?
        get() {
            if (img == null || img!!.isRecycled) img =
                BitmapFactory.decodeStream(ResUtils.openAsset("recall.png"))
            return img
        }

    public override fun initOnce() = tryVerbosely(false) {
        val m = DexKit.doFindMethod(DexKit.N_BASE_CHAT_PIE__createMulti)
        m?.hookAfter(this) {
            val rootView = findView(m.declaringClass, it.thisObject) ?: return@hookAfter
            val context = rootView.context as BaseActivity
            baseChatPie =
                ReflexUtil.getFirstByType(it.thisObject, Initiator._BaseChatPie() as Class<*>)
            val count = rootView.childCount
            val enableTalkBack = rootView.getChildAt(0).contentDescription != null
            if (rootView.findViewById<View?>(R.id.ketalRecallImageView) == null) rootView.addView(
                create(context, recallBitmap, enableTalkBack),
                count - 1
            )
            setMargin(rootView)
        }
        true
    }

    private fun recall() {
        tryVerbosely(false) {
            val clazz = DexKit.doFindClass(DexKit.C_MultiMsg_Manager)
            val manager = ReflexUtil.findMethodByTypes_1(clazz, clazz).invoke(null)
            val list = ReflexUtil.findMethodByTypes_1(clazz, MutableList::class.java)
                .invoke(manager) as List<*>
            if (list.isNotEmpty()) {
                for (msg in list) QQMessageFacade.revokeMessage(msg)
            }
            ReflexUtil.invoke_virtual_any(
                baseChatPie,
                false,
                null,
                false,
                Boolean::class.javaPrimitiveType,
                Initiator._ChatMessage(),
                Boolean::class.javaPrimitiveType
            )
            baseChatPie = null
        }
    }

    private fun setMargin(rootView: LinearLayout) {
        val width = rootView.resources.displayMetrics.widthPixels
        val count = rootView.childCount
        val rootMargin = (rootView.layoutParams as RelativeLayout.LayoutParams).leftMargin
        val w = (rootView.getChildAt(0).layoutParams as LinearLayout.LayoutParams).height
        val leftMargin = (width - rootMargin * 2 - w * count) / (count - 1)
        for (i in 1 until count) {
            val view = rootView.getChildAt(i)
            val layoutParams = LinearLayout.LayoutParams(w, w)
            layoutParams.setMargins(leftMargin, 0, 0, 0)
            layoutParams.gravity = 16
            view.layoutParams = layoutParams
        }
    }

    private fun findView(clazz: Class<*>, obj: Any): LinearLayout? {
        for (f in clazz.declaredFields) {
            if (f.type == LinearLayout::class.java) {
                f.isAccessible = true
                val view = f[obj] ?: continue
                if (check(view as LinearLayout))
                    return view
            }
        }
        return null
    }

    private fun check(rootView: LinearLayout): Boolean {
        val count = rootView.childCount
        if (count <= 1) return false
        for (i in 0 until count) {
            val view = rootView.getChildAt(i)
            if (view is TextView) return false
        }
        return true
    }

    private fun create(context: Context, bitmap: Bitmap?, enableTalkBack: Boolean): ImageView {
        val imageView = ImageView(context)
        if (enableTalkBack) {
            imageView.contentDescription = "撤回"
        }
        imageView.setOnClickListener { recall() }
        imageView.setImageBitmap(bitmap)
        imageView.id = R.id.ketalRecallImageView
        return imageView
    }
}
