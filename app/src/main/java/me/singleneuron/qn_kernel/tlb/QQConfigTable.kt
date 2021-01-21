package me.singleneuron.qn_kernel.tlb

import me.kyuubiran.hook.AutoMosaicName
import me.kyuubiran.hook.RemovePlayTogether
import me.nextalone.hook.ForcedSendOriginalPhoto
import me.nextalone.hook.HideProfileBubble
import me.nextalone.hook.HideTotalNumber
import me.singleneuron.hook.ChangeDrawerWidth
import me.singleneuron.hook.ForceSystemCamera
import me.singleneuron.hook.ForceSystemFile
import me.singleneuron.hook.NewRoundHead
import me.singleneuron.util.QQVersion.*
import nil.nadph.qnotified.hook.MultiActionHook
import nil.nadph.qnotified.hook.ReplyNoAtHook
import nil.nadph.qnotified.hook.VasProfileAntiCrash
import nil.nadph.qnotified.util.Utils

object QQConfigTable {

    private val configs: Map<String?, Map<Long, Any>> = mapOf(

            //去com.tencent.mobileqq.activity.recent.DrawerFrame类里面找一个奇怪的只有一行以一个ID从Resources获取DimensionPixelSize的方法（大概率在最末尾），然后把ID填过来
            //一般是R.dimen.akx
            ChangeDrawerWidth::class.simpleName to mapOf(
                    QQ_8_4_1 to 0x7f090834,
                    QQ_8_4_5 to 0x7f090841,
                    QQ_8_4_8 to 0x7f090882,
                    QQ_8_4_10 to 0x7f90886,
                    QQ_8_4_17 to 0x7f090896,
                    QQ_8_4_18 to 0x7f090896,
                    QQ_8_5_0 to 0x7f090921,
                    QQ_8_5_5 to 0x7f0908dc,
            ),

            //特征字符串："FaceManager"
            NewRoundHead::class.simpleName to mapOf(
                    QQ_8_3_6 to "beft",
                    QQ_8_3_9 to "bfsw",
                    QQ_8_4_1 to "aocs",
                    QQ_8_4_5 to "aope",
                    QQ_8_4_8 to "anho",
                    QQ_8_4_10 to "aoke",
                    QQ_8_4_17 to "aowc",
                    QQ_8_4_18 to "aowc",
                    QQ_8_5_0 to "com.tencent.mobileqq.avatar.utils.AvatarUtil",
                    QQ_8_5_5 to "com.tencent.mobileqq.avatar.utils.AvatarUtil",
            ),

            //特征字符串："CaptureUtil"
            ForceSystemCamera::class.simpleName to mapOf(
                    QQ_8_3_6 to "aypd",
                    QQ_8_3_9 to "babg",
                    QQ_8_4_1 to "bann",
                    QQ_8_4_5 to "bbgg",
                    QQ_8_4_8 to "babd",
                    QQ_8_4_10 to "bbhm",
                    QQ_8_4_17 to "bcmd",
                    QQ_8_4_18 to "bcmd",
                    QQ_8_5_0 to "com/tencent/mobileqq/richmedia/capture/util/CaptureUtil",
                    QQ_8_5_5 to "com/tencent/mobileqq/richmedia/capture/util/CaptureUtil"
            ),

            //特征字符串:"SmartDeviceProxyMgr create"
            ForceSystemFile::class.simpleName to mapOf(
                    QQ_8_3_6 to "zyr",
                    QQ_8_3_9 to "aaxe",
                    QQ_8_4_1 to "abqn",
                    QQ_8_4_5 to "abur",
                    QQ_8_4_8 to "aara",
                    QQ_8_4_10 to "abgm",
                    QQ_8_4_17 to "abpa",
                    QQ_8_4_18 to "abpa",
                    QQ_8_5_0 to "com/tencent/device/devicemgr/SmartDeviceProxyMgr",
                    QQ_8_5_5 to "com/tencent/device/devicemgr/SmartDeviceProxyMgr",
            ),

            // 字符串关键字 updateProfileBubbleMsgView
            HideProfileBubble::class.simpleName to mapOf(
                    QQ_8_3_9 to "S",
                    QQ_8_4_1 to "V",
                    QQ_8_4_5 to "V",
                    QQ_8_4_8 to "U",
                    QQ_8_4_10 to "Y",
                    QQ_8_4_17 to "Y",
                    QQ_8_4_18 to "Y",
                    QQ_8_5_0 to "Z",
                    QQ_8_5_5 to "Z"
            ),

            VasProfileAntiCrash::class.java.simpleName to mapOf(
                    QQ_8_4_1 to "azfl",
                    QQ_8_4_5 to "azxy",
                    QQ_8_4_8 to "aymn"
            ),

            //一般是R.id.h1y
            ForcedSendOriginalPhoto::class.java.simpleName to mapOf(
                    QQ_8_4_1 to 0x7f0a3262,
                    QQ_8_4_5 to 0x7f0a32eb,
                    QQ_8_4_8 to 0x7f0a3200,
                    QQ_8_4_10 to 0x7f0a32f0,
                    QQ_8_4_17 to 0x7f0a33cc,
                    QQ_8_4_18 to 0x7f0a33cc,
                    QQ_8_5_0 to 0x7f0a347a,
                    QQ_8_5_5 to 0x7f0a3469
            ),

            //com.tencent.mobileqq.activity.aio.core.TroopChatPie中一般是包含R.id.blz的
            HideTotalNumber::class.java.simpleName to mapOf(
                    QQ_8_4_1 to "bE",
                    QQ_8_4_5 to "bE",
                    QQ_8_4_8 to "r",
                    QQ_8_4_10 to "t",
                    QQ_8_4_17 to "t",
                    QQ_8_4_18 to "t",
                    QQ_8_5_0 to "s",
                    QQ_8_5_5 to "bz"
            ),

            RemovePlayTogether::class.java.simpleName to mapOf(
                    QQ_8_4_8 to "agpr",
                    QQ_8_4_10 to "aghe",
                    QQ_8_4_17 to "agpr",
                    QQ_8_4_18 to "agpr",
                    QQ_8_5_0 to "com/tencent/mobileqq/activity/aio/helper/ClockInEntryHelper",
                    QQ_8_5_5 to "com/tencent/mobileqq/activity/aio/helper/ClockInEntryHelper",
            ),
            AutoMosaicName::class.java.simpleName to mapOf(
                    QQ_8_4_1 to "t",
                    QQ_8_4_5 to "t",
                    QQ_8_4_8 to "enableMosaicEffect",
                    QQ_8_4_10 to "enableMosaicEffect",
                    QQ_8_4_17 to "enableMosaicEffect",
                    QQ_8_4_18 to "enableMosaicEffect",
                    QQ_8_5_0 to "enableMosaicEffect",
                    QQ_8_5_5 to "r",
            ),

    )

    private val rangingConfigs: Map<String?, Map<Long, Any>> = mapOf(
            ReplyNoAtHook::class.java.simpleName to mapOf(
                    QQ_8_1_3 to "k",
                    QQ_8_1_5 to "l",
                    QQ_8_2_6 to "m",
                    QQ_8_3_6 to "n",
                    QQ_8_4_8 to "createAtMsg",
                    QQ_8_5_5 to "l"
            ),

            MultiActionHook::class.java.simpleName to mapOf(
                    QQ_8_0_0 to "a",
            )
    )

    private val cacheMap: Map<String?, Any?> by lazy {
        val map: HashMap<String?, Any?> = HashMap()
        val versionCode = Utils.getHostVersionCode()
        for (pair in rangingConfigs) {
            for (i in versionCode downTo QQ_8_0_0) {
                if (pair.value.containsKey(i)) {
                    map[pair.key] = pair.value[i]
                    break
                }
            }
        }
        for (pair in configs) {
            if (pair.value.containsKey(versionCode)) {
                map[pair.key] = pair.value[versionCode]
            }
        }
        map
    }

    fun <T> getConfig(className: String?): T {
        val config = cacheMap[className]
        return config as T
                ?: throw RuntimeException("$className :Unsupported QQ Version")
    }

}
