package me.singleneuron.util

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.singleneuron.base.Conditional
import me.singleneuron.base.bridge.CardMsgList
import me.singleneuron.data.CardMsgCheckResult
import nil.nadph.qnotified.hook.BaseDelayableHook
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
