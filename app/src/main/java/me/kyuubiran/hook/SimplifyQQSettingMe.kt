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
package me.kyuubiran.hook

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.core.view.size
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import ltd.nextalone.util.get
import me.kyuubiran.util.setViewZeroSize
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.step.Step
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils
import java.util.*

//侧滑栏精简
@FunctionEntry
object SimplifyQQSettingMe : BaseMultiConfigDelayableHook() {
    var isInit = false

    //Form 8.4.1
    //Body = [0,1,0,0,0,1,4] || [0,1,0,0,0,1,4,0]
    private const val HIDE_KAI_BO_LA_E =
        "hide_kai_bo_la_e"           //开播啦鹅 [0,1,0,0,0,1,4,0,1] || [0,1,0,0,0,1,4,0,1,1,1]
    private const val HIDE_XIAO_SHI_JIE =
        "hide_xiao_shi_jie"         //我小世界 [0,1,0,0,0,1,4,0,2] || [0,1,0,0,0,1,4,0,1,2,1]
    private const val HIDE_HUI_YUAN =
        "hide_hui_yuan"                 //开通会员 [0,1,0,0,0,1,4,0,3] || [0,1,0,0,0,1,4,0,1,3,1]
    private const val HIDE_QIAN_BAO =
        "hide_qian_bao"                 //我的钱包 [0,1,0,0,0,1,4,0,4] || [0,1,0,0,0,1,4,0,1,4,1]
    private const val HIDE_ZHUANG_BAN =
        "hide_zhuang_ban"             //个性装扮 [0,1,0,0,0,1,4,0,5] || [0,1,0,0,0,1,4,0,1,5,1]
    private const val HIDE_QING_LV =
        "hide_qing_lv"                   //情侣空间 [0,1,0,0,0,1,4,0,6] || [0,1,0,0,0,1,4,0,1,6,1]
    private const val HIDE_SHOU_CANG =
        "hide_shou_cang"               //我的收藏 [0,1,0,0,0,1,4,0,7] || [0,1,0,0,0,1,4,0,1,7,1]
    private const val HIDE_XIANG_CE =
        "hide_xiang_ce"                 //我的相册 [0,1,0,0,0,1,4,0,8] || [0,1,0,0,0,1,4,0,1,8,1]
    private const val HIDE_WEN_JIAN =
        "hide_wen_jian"                 //我的文件 [0,1,0,0,0,1,4,0,9] || [0,1,0,0,0,1,4,0,1,9,1]
    private const val HIDE_RI_CHENG =
        "hide_ri_cheng"                 //我的日程 [0,1,0,0,0,1,4,0,10] || [0,1,0,0,0,1,4,0,1,10,1]
    private const val HIDE_SHI_PIN =
        "hide_shi_pin"                   //我的视频 [0,1,0,0,0,1,4,0,11] || [0,1,0,0,0,1,4,0,1,11,1]
    private const val HIDE_XIAO_YOU_XI =
        "hide_xiao_you_xi"           //我小游戏 [0,1,0,0,0,1,4,0,12] || [0,1,0,0,0,1,4,0,1,12,1]
    private const val HIDE_WEN_DANG =
        "hide_wen_dang"                 //腾讯文档 [0,1,0,0,0,1,4,0,13] || [0,1,0,0,0,1,4,0,1,13,1]
    private const val HIDE_DA_KA =
        "hide_da_ka"                       //每日打卡 [0,1,0,0,0,1,4,0,14] || [0,1,0,0,0,1,4,0,1,14,1]
    private const val HIDE_WANG_KA =
        "hide_wang_ka"                   //开通王卡 [0,1,0,0,0,1,4,0,15] || [0,1,0,0,0,1,4,0,1,15,1]

    //Body = [0,1,0,0,0,1,6]
    private const val HIDE_YE_JIAN = "hide_ye_jian"                   //夜间模式 [0,1,0,0,0,1,6,1]
    private const val HIDE_DA_REN = "hide_da_ren"                     //登录达人 [0,1,0,0,0,1,6,2]
    private const val HIDE_WEN_DU = "hide_wen_du"                     //当前温度 [0,1,0,0,0,1,6,3]


    override fun getPreconditions(): Array<Step?> {
        return arrayOfNulls(0)
    }

    override fun init(): Boolean {
        if (isInited) return true
        return try {
            val clz = Initiator.load("com.tencent.mobileqq.activity.QQSettingMe")
            XposedBridge.hookAllConstructors(clz, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    if (LicenseStatus.sDisableCommonHooks) return
                    if (!isEnabled) return
                    try {
                        //中间部分(QQ会员 我的钱包等)
                        val midcontentListLayout = if (requireMinQQVersion(QQVersion.QQ_8_6_5)) {
                            param.thisObject.get("c", LinearLayout::class.java)
                        } else {
                            val midcontentName = if (requireMinQQVersion(QQVersion.QQ_8_6_0)) "n" else "k"
                            param.thisObject.get(midcontentName, View::class.java) as LinearLayout
                        }
                        //底端部分 设置 夜间模式 达人 等
                        val underSettingsName = if (requireMinQQVersion(QQVersion.QQ_8_6_0)) "l" else "h"
                        val underSettingsLayout = if (requireMinQQVersion(QQVersion.QQ_8_6_5)) {
                            val parent  = midcontentListLayout?.parent?.parent as ViewGroup
                            var ret: LinearLayout? = null
                            parent.forEach {
                                if (it is LinearLayout && it[0] is LinearLayout) {
                                    ret = it
                                }
                            }
                            ret
                        } else {
                            param.thisObject.get(underSettingsName, View::class.java) as LinearLayout
                        }
                        underSettingsLayout?.forEachIndexed { i, v ->
                            val tv = (v as LinearLayout)[1] as TextView
                            val text = tv.text
                            when {
                                text.contains("间") && getBooleanConfig(HIDE_YE_JIAN) -> {
                                    v.setViewZeroSize()
                                }
                                (text.contains("达") || text.contains("天")) && getBooleanConfig(
                                    HIDE_DA_REN
                                ) -> {
                                    v.setViewZeroSize()
                                }
                                i == 3 && getBooleanConfig(HIDE_WEN_DU) -> {
                                    v.setViewZeroSize()
                                }
                            }
                        }
                        midcontentListLayout?.forEach {
                            val child = it as LinearLayout
                            val tv = if (child.size == 1) {
                                (child[0] as LinearLayout)[1]
                            } else {
                                child[1]
                            } as TextView
                            val text = tv.text.toString()
                            when {
                                text.contains("播") && getBooleanConfig(HIDE_KAI_BO_LA_E) -> {
                                    child.setViewZeroSize()
                                }
                                text.contains("世界") && getBooleanConfig(HIDE_XIAO_SHI_JIE) -> {
                                    child.setViewZeroSize()
                                }
                                (text.contains("会员") || text.toLowerCase(Locale.ROOT)
                                    .contains("vip")) && getBooleanConfig(HIDE_HUI_YUAN) -> {
                                    child.setViewZeroSize()
                                }
                                text.contains("钱包") && getBooleanConfig(HIDE_QIAN_BAO) -> {
                                    child.setViewZeroSize()
                                }
                                text.contains("装扮") && getBooleanConfig(HIDE_ZHUANG_BAN) -> {
                                    child.setViewZeroSize()
                                }
                                text.contains("情侣") && getBooleanConfig(HIDE_QING_LV) -> {
                                    child.setViewZeroSize()
                                }
                                text.contains("相册") && getBooleanConfig(HIDE_XIANG_CE) -> {
                                    child.setViewZeroSize()
                                }
                                text.contains("收藏") && getBooleanConfig(HIDE_SHOU_CANG) -> {
                                    child.setViewZeroSize()
                                }
                                text.contains("文件") && getBooleanConfig(HIDE_WEN_JIAN) -> {
                                    child.setViewZeroSize()
                                }
                                text.contains("日程") && getBooleanConfig(HIDE_RI_CHENG) -> {
                                    child.setViewZeroSize()
                                }
                                text.contains("视频") && getBooleanConfig(HIDE_SHI_PIN) -> {
                                    child.setViewZeroSize()
                                }
                                text.contains("游戏") && getBooleanConfig(HIDE_XIAO_YOU_XI) -> {
                                    child.setViewZeroSize()
                                }
                                text.contains("文档") && getBooleanConfig(HIDE_WEN_DANG) -> {
                                    child.setViewZeroSize()
                                }
                                text.contains("打卡") && getBooleanConfig(HIDE_DA_KA) -> {
                                    child.setViewZeroSize()
                                }
                                (text.contains("王卡") || text.contains("流量") || text.contains("送12个月")) && getBooleanConfig(
                                    HIDE_WANG_KA
                                ) -> {
                                    child.setViewZeroSize()
                                }
                            }
                        }
                    } catch (t: Throwable) {
                        Utils.log(t)
                    }
                }
            })
            isInit = true
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }

    override fun isInited(): Boolean {
        return isInit
    }

    override fun isEnabled(): Boolean {
        return getBooleanConfig(HIDE_KAI_BO_LA_E) || getBooleanConfig(HIDE_HUI_YUAN) || getBooleanConfig(
            HIDE_XIAO_SHI_JIE
        )
            || getBooleanConfig(HIDE_QIAN_BAO) || getBooleanConfig(HIDE_ZHUANG_BAN) || getBooleanConfig(
            HIDE_QING_LV
        ) || getBooleanConfig(HIDE_SHOU_CANG)
            || getBooleanConfig(HIDE_XIANG_CE) || getBooleanConfig(HIDE_WEN_JIAN) || getBooleanConfig(
            HIDE_RI_CHENG
        ) || getBooleanConfig(HIDE_SHI_PIN)
            || getBooleanConfig(HIDE_XIAO_YOU_XI) || getBooleanConfig(HIDE_WEN_DANG) || getBooleanConfig(
            HIDE_DA_KA
        ) || getBooleanConfig(HIDE_WANG_KA)
            || getBooleanConfig(HIDE_YE_JIAN) || getBooleanConfig(HIDE_DA_REN) || getBooleanConfig(
            HIDE_WEN_DU
        )
    }

    override fun getEffectiveProc(): Int {
        return SyncUtils.PROC_MAIN
    }

    override fun setEnabled(enabled: Boolean) {
        //not supported
    }
}
