/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
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

@file:JvmName("HostInfo")

package me.singleneuron.qn_kernel.data

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.pm.PackageInfoCompat
import me.singleneuron.qn_kernel.data.HostSpecies.*
import nil.nadph.qnotified.util.Utils

data class HostInformationProvider(
    val application: Application,
    val packageName: String,
    val hostName: String,
    val versionCode: Long,
    val versionCode32: Int,
    val versionName: String,
    val hostSpecies: HostSpecies
)


lateinit var hostInfo: HostInformationProvider

fun init(applicationContext: Application) {
    if (::hostInfo.isInitialized) throw IllegalStateException("Host Information Provider has been already initialized")
    val packageInfo = getHostInfo(applicationContext)
    val packageName = applicationContext.packageName
    hostInfo = HostInformationProvider(
        applicationContext,
        packageName,
        applicationContext.applicationInfo.loadLabel(applicationContext.packageManager).toString(),
        PackageInfoCompat.getLongVersionCode(packageInfo),
        PackageInfoCompat.getLongVersionCode(packageInfo).toInt(),
        packageInfo.versionName,
        when (packageName) {
            Utils.PACKAGE_NAME_QQ -> {
                if ("GoogleMarket" in (packageInfo.applicationInfo.metaData["AppSetting_params"]
                        ?: "") as String) {
                    QQ_Play
                } else QQ
            }
            Utils.PACKAGE_NAME_TIM -> TIM
            Utils.PACKAGE_NAME_QQ_LITE -> QQ_Lite
            Utils.PACKAGE_NAME_QQ_INTERNATIONAL -> QQ_International
            Utils.PACKAGE_NAME_SELF -> QNotified
            else -> Unknown
        },

        )
}

private fun getHostInfo(context: Context): PackageInfo {
    return try {
        context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_META_DATA)
    } catch (e: Throwable) {
        Log.e("Utils", "Can not get PackageInfo!")
        throw AssertionError("Can not get PackageInfo!")
    }
}

fun isTim(): Boolean {
    return hostInfo.hostSpecies == TIM
}

fun isPlayQQ(): Boolean {
    return hostInfo.hostSpecies == QQ_Play
}

fun requireMinQQVersion(versionCode: Long): Boolean {
    return requireMinVersion(versionCode, QQ)
}

fun requireMinPlayQQVersion(versionCode: Long): Boolean {
    return requireMinVersion(versionCode, QQ_Play)
}

fun requireMinTimVersion(versionCode: Long): Boolean {
    return requireMinVersion(versionCode, TIM)
}

fun requireMinVersion(versionCode: Long, hostSpecies: HostSpecies): Boolean {
    return hostInfo.hostSpecies == hostSpecies && hostInfo.versionCode >= versionCode
}

fun requireMinVersion(
    QQVersionCode: Long = Long.MAX_VALUE,
    TimVersionCode: Long = Long.MAX_VALUE,
    PlayQQVersionCode: Long = Long.MAX_VALUE
): Boolean {
    return requireMinQQVersion(QQVersionCode) || requireMinTimVersion(TimVersionCode) || requireMinPlayQQVersion(PlayQQVersionCode)
}
