package me.singleneuron.qn_kernel.data

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.pm.PackageInfoCompat
import nil.nadph.qnotified.util.Utils


data class HostInformationProvider(
    val application: Application,
    val packageName: String,
    val hostName: String,
    val versionCode: Long,
    val versionCode32: Int,
    val versionName: String,
    val isTim: Boolean,
    val isPlayQQ: Boolean
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
        Utils.PACKAGE_NAME_TIM == packageName,
        "GoogleMarket" in (packageInfo.applicationInfo.metaData["AppSetting_params"]
            ?: "") as String
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

fun requireMinQQVersion(versionCode: Long): Boolean {
    return !hostInfo.isTim && !hostInfo.isPlayQQ && hostInfo.versionCode >= versionCode
}

fun requireMinPlayQQVersion(versionCode: Long): Boolean {
    return hostInfo.isPlayQQ && hostInfo.versionCode >= versionCode
}

fun requireMinTimVersion(versionCode: Long): Boolean {
    return hostInfo.isTim && hostInfo.versionCode >= versionCode
}

fun requireMinVersion(
    QQVersionCode: Long = Long.MAX_VALUE,
    TimVersionCode: Long = Long.MAX_VALUE,
    PlayQQVersionCode: Long = Long.MAX_VALUE
): Boolean {
    return requireMinQQVersion(QQVersionCode) or requireMinTimVersion(TimVersionCode) or requireMinPlayQQVersion(PlayQQVersionCode)
}
