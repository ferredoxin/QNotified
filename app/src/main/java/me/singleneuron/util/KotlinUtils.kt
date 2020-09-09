package me.singleneuron.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.singleneuron.base.Conditional
import me.singleneuron.base.bridge.CardMsgList
import me.singleneuron.data.CardMsgCheckResult
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.singleneuron.base.Conditional
import nil.nadph.qnotified.R
import nil.nadph.qnotified.activity.EulaActivity
import nil.nadph.qnotified.hook.BaseDelayableHook
import nil.nadph.qnotified.ui.ViewBuilder
import nil.nadph.qnotified.ui.ViewBuilder.newListItemHookSwitchInit
import nil.nadph.qnotified.util.Utils
import java.io.BufferedReader
import java.io.File
import java.io.IOException

fun ViewGroup.addViewConditionally(view: View, condition: Boolean) {
    if (condition) {
        this.addView(view)
    }
}

fun <T> ViewGroup.addViewConditionally(context: Context, title: String, desc: String, hook: T) where T : BaseDelayableHook, T : Conditional {
    addViewConditionally(newListItemHookSwitchInit(context, title, desc, hook), hook.condition)
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

fun checkCardMsg(string: String): CardMsgCheckResult {
    try {
        val blackListString = CardMsgList.getInstance().invoke()
        val blackList = Gson().fromJson<HashMap<String, String>>(blackListString, object : TypeToken<HashMap<String, String>>() {}.type)
        Utils.logd(Gson().toJson(blackList))
        for (rule in blackList) {
            if (Regex(rule.value, setOf(RegexOption.IGNORE_CASE,RegexOption.DOT_MATCHES_ALL,RegexOption.LITERAL)).containsMatchIn(string)) {
                return CardMsgCheckResult(false, rule.key)
            }
        }
        return CardMsgCheckResult(true)
    } catch (e: Exception) {
        Utils.log(e)
        return CardMsgCheckResult(false, "Failed: $e")
    }
}

fun showEulaDialog(activity: Activity) {
    val linearLayout = LinearLayout(activity)
    linearLayout.orientation = LinearLayout.VERTICAL
    val textView = TextView(activity)
    textView.text = "为避免滥用，在您使用 发送卡片消息 及 群发文本消息 时，本模块会向服务器报告您使用此功能时发送的消息内容以及当前QQ号。\n继续使用 群发 或 卡片消息 功能代表您同意放弃自己的一切权利，并允许QNotified开发组及管理组在非匿名的前提下任意存储、分析、使用、分享您的数据。如您不同意，请立刻退出。"
    val editText = EditText(activity)
    editText.isEnabled = false
    editText.visibility = View.INVISIBLE
    linearLayout.addView(textView)
    linearLayout.addView(editText)
    val builder = MaterialAlertDialogBuilder(activity, R.style.MaterialDialog)
            .setView(linearLayout)
            .setCancelable(false)
            .setPositiveButton("我已阅读并同意", null)
            .setNeutralButton("查看用户协议"){ _: DialogInterface, _: Int ->
                ViewBuilder.clickToProxyActAction(EulaActivity::class.java).onClick(linearLayout)
                activity.finish()
            }
            .setNegativeButton("取消"){ _: DialogInterface, _: Int ->
                activity.finish()
            }
    val dialog = builder.create()
    dialog.show()
    val button: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
    editText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            button.isEnabled = editText.text.toString() == "我已阅读并同意"
        }
    })
    button.isEnabled = false
    Thread {
        for (i in 30 downTo 1) {
            Utils.runOnUiThread { button.text = "我已阅读并同意 ($i)" }
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        Utils.runOnUiThread {
            button.text = "确定"
            editText.isEnabled = true
            editText.visibility = View.VISIBLE
            textView.text = textView.text.toString() + "\n请在下方输入框中输入 我已阅读并同意"
        }
    }.start()
}

