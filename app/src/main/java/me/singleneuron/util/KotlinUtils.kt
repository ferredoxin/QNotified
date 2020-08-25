package me.singleneuron.util

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import me.singleneuron.base.BaseDelayableConditionalHookAdapter
import nil.nadph.qnotified.ui.ViewBuilder.newListItemHookSwitchInit
import nil.nadph.qnotified.util.Utils
import java.io.BufferedReader
import java.io.File
import java.io.IOException

class KotlinUtils {

    /*
        use in Java:
        KotlinUtils.Companion.readFile(yourFile)
     */

    companion object {
        fun ViewGroup.addViewConditionally(view: View, condition: Boolean) {
            if (condition) {
                this.addView(view)
            }
        }

        fun ViewGroup.addViewConditionally(context: Context, title: String, desc: String, hook: BaseDelayableConditionalHookAdapter) {
            addViewConditionally(newListItemHookSwitchInit(context, title, desc, hook), hook.condition)
        }

        @Throws(IOException::class)
        fun readFile(file: File) : String {
            return file.readText()
        }

        @Throws(IOException::class)
        fun readFromBufferedReader(bufferedReader: BufferedReader): String {
            return bufferedReader.readText()
        }

        fun dumpIntent(intent: Intent) {
            Utils.logd(intent.toString())
            Utils.logd(intent.extras.toString())
            Utils.logd(Log.getStackTraceString(Throwable()))
        }

    }

}