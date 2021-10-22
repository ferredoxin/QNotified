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

package me.singleneuron.hook

import android.app.Notification
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.MessagingStyle.Message
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import xyz.nextalone.util.clazz
import xyz.nextalone.util.tryOrFalse
import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.base.CommonDelayAbleHookBridge
import me.singleneuron.qn_kernel.data.hostInfo
import me.singleneuron.qn_kernel.ui.base.增强功能
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.base.annotation.FunctionEntry

@FunctionEntry
@UiItem
object MessageStyleNotification : CommonDelayAbleHookBridge(SyncUtils.PROC_ANY) {

    override val preference = uiSwitchPreference {
        title = "MessageStyle通知"
        summary = "致敬QQ Helper"
    }

    override val preferenceLocate = 增强功能

    private val numRegex = Regex("""\((\d+)\S{1,3}新消息\)?$""")
    private val senderName = Regex("""^.*?: """)

    private val historyMessage: HashMap<Int, MutableList<Message>> = HashMap()
    private val personCache: HashMap<Int, Person> = HashMap()

    override fun initOnce(): Boolean {
        return tryOrFalse {
            XposedHelpers.findAndHookMethod("com.tencent.mobileqq.service.MobileQQServiceExtend".clazz,
                "a",
                Intent::class.java,
                Bitmap::class.java,
                String::class.java,
                String::class.java,
                String::class.java,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        tryOrFalse {
                            val intent = param.args[0] as Intent
                            val isTroop = if (intent.getIntExtra("uintype", -1) == -1)
                                intent.getIntExtra("param_uinType", -1)
                            else
                                intent.getIntExtra("uintype", -1)
                            if (isTroop == 0 || isTroop == 1 || isTroop == 3000) {
                                var bitmap = param.args[1] as Bitmap?
                                var title = param.args[3] as String
                                var text = param.args[4] as String
                                val oldNotification = param.result as Notification
                                val notificationId =
                                    intent.getIntExtra("KEY_NOTIFY_ID_FROM_PROCESSOR", -113)
                                val messageStyle = NotificationCompat.MessagingStyle("我")
                                historyMessage[notificationId]?.forEach { it ->
                                    messageStyle.addMessage(it)
                                }

                                title = numRegex.replace(title, "")

                                val person: Person

                                if (isTroop == 1) {
                                    val sender = senderName.find(text)?.value?.replace(": ", "")
                                    text = senderName.replace(text, "")
                                    /*tryOrFalse {
                                        val senderUin = intent.getStringExtra("param_fromuin")
                                        bitmap = face.getBitmapFromCache(TYPE_USER,senderUin)
                                    }*/
                                    person = Person.Builder()
                                        .setName(sender)
                                        //.setIcon(IconCompat.createWithBitmap(bitmap))
                                        .build()
                                    messageStyle.conversationTitle = title
                                } else {
                                    val personInCache = personCache[notificationId]
                                    if (personInCache == null) {
                                        val builder = Person.Builder()
                                            .setName(title)
                                            .setIcon(IconCompat.createWithBitmap(bitmap))
                                        if (title.contains("[特别关心]")) {
                                            builder.setImportant(true)
                                        }
                                        person = builder.build()
                                        personCache[notificationId] = person
                                    } else {
                                        person = personInCache
                                    }
                                }

                                val message = Message(text, oldNotification.`when`, person)
                                messageStyle.addMessage(message)
                                if (historyMessage[notificationId] == null) {
                                    historyMessage[notificationId] = ArrayList()
                                }
                                historyMessage[notificationId]?.add(message)

                                //Utils.logd(historyMessage.toString())

                                param.result = NotificationCompat.Builder(
                                    hostInfo.application,
                                    oldNotification
                                )
                                    .setContentTitle(null)
                                    .setContentText(null)
                                    .setLargeIcon(null)
                                    .setStyle(messageStyle).build()
                            }
                        }
                    }
                }
            )
            XposedHelpers.findAndHookMethod(
                "com.tencent.commonsdk.util.notification.QQNotificationManager".clazz,
                "cancel", String::class.java, Int::class.javaPrimitiveType,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        if (param.args[0] as String != "MobileQQServiceWrapper.showMsgNotification") {
                            historyMessage.remove(param.args[1] as Int)
                            personCache.remove(param.args[1] as Int)
                        }
                    }
                }
            )
            XposedHelpers.findAndHookMethod(
                "com.tencent.commonsdk.util.notification.QQNotificationManager".clazz,
                "cancelAll",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        historyMessage.clear()
                        personCache.clear()
                    }
                }
            )
        }
    }

}
