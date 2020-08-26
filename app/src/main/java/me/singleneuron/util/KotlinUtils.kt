package me.singleneuron.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import me.singleneuron.base.BaseDelayableConditionalHookAdapter
import me.singleneuron.data.PageFaultHighPerformanceFunctionCache
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

        fun ViewGroup.addViewConditionally(context: Context, title: String, desc:String, hook:BaseDelayableConditionalHookAdapter) {
            addViewConditionally(newListItemHookSwitchInit(context,title,desc,hook),hook.condition)
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

        internal val hostVersionCode: PageFaultHighPerformanceFunctionCache<Long> = PageFaultHighPerformanceFunctionCache label@{
            val pi = Utils.getHostInfo(Utils.getApplication())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return@label pi.longVersionCode
            } else {
                return@label pi.versionCode.toLong()
            }
        }

        fun getHostVersionCode(): Long = hostVersionCode.getValue()

        internal val hostAppName: PageFaultHighPerformanceFunctionCache<String> = PageFaultHighPerformanceFunctionCache { Utils.getHostInfo().applicationInfo.loadLabel(Utils.getPackageManager()).toString() }

        fun getHostAppName(): String = hostAppName.getValue()
    }

}