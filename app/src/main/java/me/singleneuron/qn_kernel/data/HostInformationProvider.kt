package me.singleneuron.qn_kernel.data

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.util.Log
import androidx.core.content.pm.PackageInfoCompat
import com.tencent.mobileqq.app.QQAppInterface
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import nil.nadph.qnotified.util.Utils


data class HostInformationProvider(
    val applicationContext: Application,
    val packageName: String,
    val hostName: String,
    val versionCode: Long,
    val versionCode32: Int,
    val versionName: String,
    val isTim: Boolean,
    var qqAppInterface: QQAppInterface? = null
)



lateinit var hostInformationProvider: HostInformationProvider

fun init(applicationContext: Application) {
    if (::hostInformationProvider.isInitialized) throw IllegalStateException("Host Information Provider has been already initialized")
    val packageInfo = getHostInfo(applicationContext)
    val packageName = applicationContext.packageName
    hostInformationProvider = HostInformationProvider(
        applicationContext,
        packageName,
        applicationContext.applicationInfo.loadLabel(applicationContext.packageManager).toString(),
        PackageInfoCompat.getLongVersionCode(packageInfo),
        PackageInfoCompat.getLongVersionCode(packageInfo).toInt(),
        packageInfo.versionName,
        Utils.PACKAGE_NAME_TIM == packageName
    )
    XposedHelpers.findAndHookMethod(QQAppInterface::class.java,"onCreate",object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam?) {
            hostInformationProvider.qqAppInterface = param?.thisObject as QQAppInterface
        }
    })
}

private fun getHostInfo(context: Context): PackageInfo {
    return try {
        context.packageManager.getPackageInfo(context.packageName, 0)
    } catch (e: Throwable) {
        Log.e("Utils", "Can not get PackageInfo!")
        throw AssertionError("Can not get PackageInfo!")
    }
}
