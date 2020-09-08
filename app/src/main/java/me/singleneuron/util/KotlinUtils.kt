package me.singleneuron.util

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import me.singleneuron.base.CardMsgCheckResult
import me.singleneuron.base.Conditional
import me.singleneuron.base.bridge.CardMsgList
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

fun <T> ViewGroup.addViewConditionally(context: Context, title: String, desc: String, hook: T) where T:BaseDelayableHook, T:Conditional{
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
    val blackList = CardMsgList.getInstance().blackList
    for (black in blackList) {
        var hit = true
        for (rule in black.value) {
            if (!string.contains(rule, true)) {
                hit = false
                break
            }
        }
        if (hit) {
            return CardMsgCheckResult(false,black.key)
        }
    }
    return CardMsgCheckResult(true)
}
