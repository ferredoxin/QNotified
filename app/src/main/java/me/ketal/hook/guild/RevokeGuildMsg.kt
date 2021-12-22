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

package me.ketal.hook.guild

import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.base.CommonDelayAbleHookBridge
import me.singleneuron.qn_kernel.data.MsgRecordData
import me.singleneuron.qn_kernel.tlb.频道功能
import mqq.app.AppRuntime
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.util.Initiator._MessageRecord
import xyz.nextalone.util.*

@FunctionEntry
@UiItem
object RevokeGuildMsg : CommonDelayAbleHookBridge(SyncUtils.PROC_MAIN or SyncUtils.PROC_MSF) {
    override val preference = uiSwitchPreference {
        title = "防撤回消息"
    }

    override val preferenceLocate = 频道功能

    override fun initOnce() = tryOrFalse {
        "com/tencent/mobileqq/guild/message/eventflow/api/impl/GuildEventFlowServiceImpl"
            .clazz?.method("handleDeleteEvent")
            ?.hookBefore(this) { it ->
                val thisObject = it.thisObject
                val appRuntime = thisObject.get("appRuntime")!!
                val msgRecord = it.args[0] ?: return@hookBefore
                val event = it.args[1]
                val senderUin = MsgRecordData(msgRecord).senderUin!!

                val opInfo = event.get("op_info")
                if (opInfo.invoke("has") != true) return@hookBefore
                val operatorTinyid = opInfo.get("operator_tinyid")
                if (operatorTinyid.invoke("has") != true) return@hookBefore
                val operatorId = operatorTinyid.invoke("get").toString()
                if (operatorId == "0") return@hookBefore
                val selfTinyId = appRuntime.invoke(
                    "getRuntimeService",
                    "com.tencent.mobileqq.qqguildsdk.api.IGPSService".clazz!!,
                    Class::class.java
                ).invoke("getSelfTinyId")

                val revokerNick = getNickName(appRuntime, operatorId, msgRecord)
                val authorNick = getNickName(appRuntime, senderUin, msgRecord)
                val msg = MsgRecordData(msgRecord).msg
                val greyMsg = if (senderUin == operatorId) {
                    "\"$revokerNick\u202d\" 尝试撤回一条消息: $msg "
                } else {
                    "\"$revokerNick\u202d\"撤回了\"$authorNick\u202d\"的消息: $msg"
                }
                msgRecord.invoke(
                    "saveExtInfoToExtStr",
                    "wording", greyMsg,
                    String::class.java, String::class.java
                )
                thisObject.invoke(
                    "addGreyTipsForDeletedMsg",
                    msgRecord, event,
                    _MessageRecord(), event.javaClass
                )

                if (selfTinyId != operatorId) {
                    it.result = null
                }
            }

        "Lcom/tencent/mobileqq/guild/message/msgtype/MessageForGuildRevokeGrayTip;->init(Lcom/tencent/mobileqq/data/MessageRecord;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V"
            .method.hookBefore(this) {
                val msg = it.args[0]
                val wording = msg.invoke(
                    "getExtInfoFromExtStr",
                    "wording", String::class.java
                )
                if (wording == null || "" == wording) return@hookBefore
                it.args[1] = wording
            }
    }

    private fun getNickName(
        appRuntime: Any,
        uin: String,
        msg: Any
    ): String {
        val msgData = MsgRecordData(msg)
        val apiClass = "com.tencent.mobileqq.guild.message.api.IGuildNicknameApi".clazz!!
        val api = "Lcom/tencent/mobileqq/qroute/QRoute;->api(Ljava/lang/Class;)Lcom/tencent/mobileqq/qroute/QRouteApi;"
            .method.invoke(null, apiClass)
        val args = arrayOf(
            appRuntime, msgData.friendUin!!, uin, msg,
            AppRuntime::class.java,
            String::class.java, String::class.java, _MessageRecord()
        )
        return api.invoke("getDisplayName", *args) as String
    }
}
