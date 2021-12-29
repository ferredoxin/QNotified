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
package me.singleneuron.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.singleneuron.base.Conditional
import me.singleneuron.base.bridge.CardMsgList
import me.singleneuron.data.CardMsgCheckResult
import nil.nadph.qnotified.BuildConfig
import nil.nadph.qnotified.R
import nil.nadph.qnotified.activity.EulaActivity
import nil.nadph.qnotified.activity.OmegaTestFuncActivity
import nil.nadph.qnotified.hook.BaseDelayableHook
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.ui.ViewBuilder.newListItemHookSwitchInit
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils
import java.io.BufferedReader
import java.io.File
import java.io.IOException

fun ViewGroup.addViewConditionally(view: View, condition: Boolean) {
    if (condition) {
        this.addView(view)
    }
}

fun <T> ViewGroup.addViewConditionally(
    context: Context,
    title: String,
    desc: String,
    hook: T
) where T : BaseDelayableHook, T : Conditional {
    addViewConditionally(newListItemHookSwitchInit(context, title, desc, hook), hook.condition)
}

fun ViewGroup.addViewConditionally(
    context: Context,
    title: String,
    desc: String,
    hook: CommonDelayableHook
) {
    addViewConditionally(newListItemHookSwitchInit(context, title, desc, hook), hook.isValid)
}

@Throws(IOException::class)
fun readFile(file: File): String {
    return file.readText()
}

@Throws(IOException::class)
fun readFromBufferedReader(bufferedReader: BufferedReader): String {
    return bufferedReader.readText()
}

fun Intent.dump() {
    dumpIntent(this)
}

fun dumpIntent(intent: Intent) {
    Utils.logd(intent.toString())
    Utils.logd(intent.extras.toString())
    Utils.logd(Log.getStackTraceString(Throwable()))
}

fun checkCardMsg(originString: String): CardMsgCheckResult {
    try {
        Utils.logd("origin string: $originString")
        val string = decodePercent(originString)
        Utils.logd("decode string: $string")
        val blackListString = CardMsgList.getInstance().invoke()
        val blackList = Gson().fromJson<HashMap<String, String>>(
            blackListString,
            object : TypeToken<HashMap<String, String>>() {}.type
        )
        Utils.logd(Gson().toJson(blackList))
        for (rule in blackList) {
            if (Regex(
                    rule.value,
                    setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
                ).containsMatchIn(string)
            ) {
                return CardMsgCheckResult(false, rule.key)
            }
        }
        return CardMsgCheckResult(true)
    } catch (e: Exception) {
        Utils.log(e)
        return CardMsgCheckResult(false, "Failed: $e")
    }
}

private fun decodePercent(string: String): String {
    var produceString = string
    val regex = Regex("""%[0-9a-fA-F]{2}""", RegexOption.IGNORE_CASE)
    while (true) {
        if (!regex.containsMatchIn(produceString)) return produceString
        produceString = regex.replace(produceString) { matchResult ->
            val hex = matchResult.value.substring(1)
            try {
                val char = Integer.valueOf(hex, 16).toChar().toString()
                Utils.logd("replace $hex -> $char")
                return@replace char
            } catch (e: Exception) {
                Utils.log(e)
                return@replace hex
            }
        }
        Utils.logd("processing string: $produceString")
    }
}

fun showEulaDialog(activity: Activity) {
    if (BuildConfig.DEBUG) {
        activity.startActivity(Intent(activity, OmegaTestFuncActivity::class.java))
        return
    }
    val linearLayout = LinearLayout(activity)
    linearLayout.orientation = LinearLayout.VERTICAL
    val textView = TextView(activity)
    textView.text =
        "为避免该功能被滥用,在您每次进入该功能时将会弹出该弹窗,请勿试图用各种办法绕过 \n在您使用 发送卡片消息 及 群发文本消息 时，本模块会向服务器报告您使用此功能时发送的消息内容以及当前QQ号。\n继续使用 群发 或 卡片消息 功能代表您同意放弃自己的一切权利，并允许QNotified开发组及管理组在非匿名的前提下任意存储、分析、使用、分享您的数据。如您不同意，请立刻退出。\n请您在使用此功能时自觉遵守您所在地区的法律法规，开发者不为您使用此功能产生的后果承担任何责任，并保留在必要的时候配合执法机构调查的权利。"
    textView.setTextColor(Color.RED)
    val editText = EditText(activity)
    editText.isEnabled = false
    editText.visibility = View.INVISIBLE
    linearLayout.addView(textView)
    linearLayout.addView(editText)
    val builder = MaterialAlertDialogBuilder(activity, R.style.MaterialDialog)
        .setView(linearLayout)
        .setCancelable(false)
        .setPositiveButton("我已阅读并同意用户协议") { _: DialogInterface, _: Int ->
            activity.startActivity(Intent(activity, OmegaTestFuncActivity::class.java))
        }
        .setNeutralButton("阅读用户协议") { _: DialogInterface, _: Int ->
            activity.startActivity(Intent(activity, EulaActivity::class.java))
            activity.finish()
        }
        .setNegativeButton("取消") { _: DialogInterface, _: Int ->
        }
    val dialog = builder.create()
    dialog.show()
    val button: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
    editText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            button.isEnabled = editText.text.toString() == "我已阅读并同意用户协议"
        }
    })
    button.isEnabled = false
    Thread {
        var time = 15
        if (LicenseStatus.isInsider()) time = if (Math.random() < 0.1) 86400 else 5
        if (Math.random() < 0.01) time = -time
        do {
            Utils.runOnUiThread { button.text = "我已阅读并同意用户协议 ($time)" }
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        } while (--time != 0)
        Utils.runOnUiThread {
            button.text = "确定"
            editText.isEnabled = true
            editText.visibility = View.VISIBLE
            textView.text =
                textView.text.toString() + "\n若继续进入该功能,请在下方输入框中输入 我已阅读并同意用户协议 ,退出该页面请点取消"
            if (LicenseStatus.isInsider()) {
                editText.setText("我已阅读并同意用户协议")
            }
        }
    }.start()
}

