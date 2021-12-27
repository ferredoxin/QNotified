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
package nil.nadph.qnotified.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.URLSpan
import android.text.util.Linkify
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cc.ioctl.util.DateTimeUtil
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import me.ketal.data.ConfigData
import me.singleneuron.qn_kernel.ui.qq_item.ListItemButton
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.ui.CustomDialog
import nil.nadph.qnotified.util.Utils.DummyCallback
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class UpdateCheck : Runnable {
    private val updateChannel = ConfigData<String>("qn_update_channel")
    private val RL_DONE = 0
    private val RL_LOAD = 1
    private val RL_SHOW_RET = 2
    var currVerCode = Utils.QN_VERSION_CODE
    var currVerName = Utils.QN_VERSION_NAME
    private var context: Context? = null
    private var flow: MutableStateFlow<String?>? = null
    private var clicked = false
    private lateinit var result: JsonObject
    private var runLevel = 0
    private fun doRefreshInfo(): String? {
        var content: String? = null
        try {
            val reqURL: URL = when (currChannel) {
                "Canary" -> URL(UPDATE_INFO_GET_CI)
                "Alpha" -> URL(UPDATE_INFO_GET_WEEKLY)
                else -> URL(UPDATE_INFO_GET_WEEKLY)
            }
            val httpsConn = reqURL.openConnection() as HttpsURLConnection
            val `in` = httpsConn.inputStream
            val bais = ByteArrayOutputStream()
            val buf = ByteArray(256)
            var len: Int
            while (`in`.read(buf).also { len = it } != -1) {
                bais.write(buf, 0, len)
            }
            `in`.close()
            content = bais.toString("UTF-8")
            httpsConn.disconnect()
            val cache = ConfigManager.getCache()
            cache.putString(qn_update_info, content)
            cache.putLong(qn_update_time, System.currentTimeMillis() / 1000L)
            cache.save()
            return content
        } catch (e: IOException) {
            runLevel = RL_DONE
            if (content == null) {
                Handler(context!!.mainLooper).post {
                    Toast.makeText(context, "检查更新失败:$e", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        return null
    }

    private val cachedUpdateInfoOrNull: String?
        get() = try {
            val cache = ConfigManager.getCache()
            val str = cache.getString(qn_update_info)
            cache.getLongOrDefault(qn_update_time, 0)
            str
        } catch (e: Exception) {
            Utils.log(e)
            null
        }

    fun setVersionTip() {
        val str = cachedUpdateInfoOrNull
        if (str != null) {
            doSetVersionTip(JsonParser.parseString(str).asJsonObject)
        }
    }

    private fun doSetVersionTip(json: JsonObject) {
        try {
            val currBuildTime = Utils.getBuildTimestamp()
            val latestBuildTime: Long = json.get("version").asString.toInt() * 1000L
            val latestName: String = json.get("short_version").asString
            if (latestBuildTime - currBuildTime > 10 * 60 * 1000L) {
                //has newer
                flow?.value = latestName
                if (clicked) {
                    doShowUpdateInfo()
                }
            } else {
                flow?.value = "已是最新"
            }
        } catch (e: Exception) {
            Utils.log(e)
        }
    }

    override fun run() {
        when (runLevel) {
            RL_LOAD -> {
                val ret = doRefreshInfo() ?: return
                runLevel = RL_SHOW_RET
                result = JsonParser.parseString(ret).asJsonObject
                Handler(context!!.mainLooper).post(this)
                return
            }
            RL_SHOW_RET -> {
                doSetVersionTip(result)
                runLevel = RL_DONE
                if (clicked) {
                    doShowUpdateInfo()
                }
                return
            }
        }
    }

    private fun doShowUpdateInfo() {
        try {
            clicked = false
            val dialog = CustomDialog.create(context)
            dialog.setTitle("当前$currVerName ($currVerCode)")
            dialog.setCancelable(true)
            dialog.setNegativeButton("关闭", DummyCallback())
            val sb = SpannableStringBuilder()
            val ver = result
            val vn = ver.get("short_version").asString
            val vc: Int = ver.get("version").asString.toInt()
            val desc = ver.get("release_notes").asString
            val md5 = ver.get("fingerprint").asString
            val download_url = ver.get("download_url").asString
            val time = ver.get("version").asString.toInt() * 1000L
            val date = DateTimeUtil.getRelTimeStrSec(time / 1000L)
            var tmp = SpannableString("$vn ($vc)")
            tmp.setSpan(RelativeSizeSpan(1.8f), 0, tmp.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            sb.append(tmp)
            if (currVerName == vn) {
                sb.append("当前版本")
            } else if (time - Utils.getBuildTimestamp() > 10 * 60 * 1000) {
                sb.append("新版本")
            } else if (time - Utils.getBuildTimestamp() < -10 * 60 * 1000) {
                sb.append("旧版本")
            }
            sb.append("\n发布于").append(date)
            sb.append("\nmd5:").append(md5).append("\n")
            sb.append(desc)
            sb.append("\n下载地址:\n")
            tmp = SpannableString(download_url)
            tmp.setSpan(URLSpan(download_url), 0, tmp.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            sb.append(tmp)
            sb.append("\n")
            sb.append("\n")
            dialog.setMessage(sb)
            val tv = dialog.messageTextView
            if (tv != null) {
                tv.linksClickable = true
                tv.isEnabled = true
                tv.isFocusable = true
                try {
                    tv.isFocusableInTouchMode = true
                    tv.setTextIsSelectable(true)
                    tv.autoLinkMask = Linkify.WEB_URLS
                } catch (ignored: NoSuchMethodError) {
                }
            }
            dialog.show()
        } catch (e: Exception) {
            Utils.log(e)
        }
    }

    fun onClick(ctx: Context?, stateFlow: MutableStateFlow<String?>) {
        context = ctx
        flow = stateFlow
        clicked = true
        if (!::result.isInitialized || runLevel == RL_LOAD) {
            runLevel = RL_LOAD
            Thread(this).start()
        } else {
            doShowUpdateInfo()
        }
    }

    var currChannel: String
        get() = updateChannel.getOrDefault("Alpha")
        set(channel) {
            updateChannel.value = channel
        }

    fun listener(vg: ViewGroup): View.OnClickListener {
        return View.OnClickListener { v: View ->
            AlertDialog.Builder(v.context,
                CustomDialog.themeIdForDialog())
                .setTitle("自定义更新通道")
                .setItems(channels) { _: DialogInterface?, which: Int ->
                    val channel = channels[which]
                    if (currChannel != channel) {
                        runLevel = RL_LOAD
                        currChannel = channels[which]
                        (vg as ListItemButton).value = channels[which]
                    }
                }.show()
        }
    }

    fun showChannelDialog(context: Context, stateFlow: MutableStateFlow<String?>){
        AlertDialog.Builder(context,
            CustomDialog.themeIdForDialog())
            .setTitle("自定义更新通道")
            .setItems(channels) { _: DialogInterface?, which: Int ->
                val channel = channels[which]
                if (currChannel != channel) {
                    runLevel = RL_LOAD
                    currChannel = channels[which]
                    stateFlow.value = channels[which]
                }
            }.show()
    }

    companion object {
        const val UPDATE_INFO_GET_CI = "https://api.appcenter.ms/v0.1/public/sdk/apps/ddf4b597-1833-45dd-af28-96ca504b8123/releases/latest"
        const val UPDATE_INFO_GET_WEEKLY = "https://api.appcenter.ms/v0.1/public/sdk/apps/ddf4b597-1833-45dd-af28-96ca504b8123/distribution_groups/8a11cc3e-47da-4e3b-84e7-ac306a128aaf/releases/latest"
        const val qn_update_info = "qn_update_info"
        const val qn_update_time = "qn_update_time"
        private val channels = arrayOf("Alpha", "Canary")
    }
}
