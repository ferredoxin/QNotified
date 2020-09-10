package me.singleneuron.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import com.topjohnwu.superuser.Shell
import nil.nadph.qnotified.R
import nil.nadph.qnotified.util.Utils
import java.io.File

object HookStatue {

    const val TAICHI_NOT_INSTALL = 0
    const val TAICHI_NOT_ACTIVE = 1
    const val TAICHI_ACTIVE = 2

    fun isEnabled(): Boolean {
        return Utils.getActiveModuleVersion() != null
//        Log.d("singleNeuronUI", "模块未激活")
//        return false
    }

    fun Context.getStatue(useSu: Boolean): Statue {
        val isInstall = IsInstall(this)
        val getMagiskModule: BaseGetMagiskModule = if (useSu) {
            GetMagiskModule()
        } else {
            BaseGetMagiskModule()
        }
        val isExp = isExpModuleActive(this)
        return if (isEnabled()) {
            if (edxp) Statue.Edxp_Active else if (isExp == TAICHI_ACTIVE) Statue.taichi_magisk_active else if (isInstall.isEdxpManagerInstall || getMagiskModule.edxpModule) Statue.Edxp_Active else if (isInstall.isXposedInstall) Statue.xposed_active else Statue.xposed_active
        } else {
            if (isExp == TAICHI_ACTIVE) {
                if (taichi_magisk() || getMagiskModule.taichiModule) Statue.taichi_magisk_active else Statue.taichi_active
            } else if (isInstall.isEdxpManagerInstall || getMagiskModule.edxpModule) Statue.Edxp_notActive else if (isInstall.isXposedInstall) Statue.xposed_notActive else if (isExp == TAICHI_NOT_ACTIVE) {
                if (taichi_magisk() || getMagiskModule.taichiModule) Statue.taichi_magisk_notActive else Statue.taichi_notActive
            } else Statue.xposed_notActive
        }
    }

    fun Statue.isActive(): Boolean {
        return !this.name.contains("not")
    }

    @StringRes
    fun Statue.getStatueName(): Int {
        return when (this) {
            Statue.xposed_notActive -> R.string.xposed_not_activated
            Statue.xposed_active -> R.string.xposed_activated
            Statue.Edxp_notActive -> R.string.edxposed_not_activated
            Statue.Edxp_Active -> R.string.edxposed_activated
            Statue.taichi_notActive -> R.string.taichi_yin_not_activated
            Statue.taichi_active -> R.string.taichi_yin_activated
            Statue.taichi_magisk_notActive -> R.string.taichi_yang_not_activated
            Statue.taichi_magisk_active -> R.string.taichi_yang_activated
        }
    }

    private var edxp = File("/system/framework/edxp.jar").exists()

    fun taichi_magisk(): Boolean {
        var taichi_magisk = false
        try {
            taichi_magisk = "1" == System.getProperty("taichi_magisk")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return taichi_magisk
    }

    @Taichi_statue
    fun isExpModuleActive(context: Context): Int {
        var isExp = TAICHI_NOT_INSTALL
        try {
            val contentResolver = context.contentResolver
            val uri = Uri.parse("content://me.weishu.exposed.CP/")
            var result: Bundle? = null
            try {
                result = contentResolver.call(uri, "active", null, null)
            } catch (e: RuntimeException) {
                // TaiChi is killed, try invoke
                try {
                    val intent = Intent("me.weishu.exp.ACTION_ACTIVE")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } catch (e1: Throwable) {
                    return TAICHI_NOT_INSTALL
                }
            }
            if (result == null) {
                result = contentResolver.call(uri, "active", null, null)
            }
            if (result == null) {
                return TAICHI_NOT_INSTALL
            }
            isExp = if (!result.getBoolean("active", false)) TAICHI_NOT_ACTIVE
            else TAICHI_ACTIVE
        } catch (ignored: Throwable) {
        }
        return isExp
    }

    class IsInstall constructor(val context: Context){


        var isEdxpManagerInstall = false
        var isXposedInstall = false

        init {
            val packageManager = context.packageManager
            val pid = PackageInstallDetect(packageManager)
            isXposedInstall = pid.isPackageInstall(xposed_installer_packageName)
            isEdxpManagerInstall = pid.isPackageInstall(edxposed_installer_packageName) || pid.isPackageInstall(edxposed_manager_packageName)
        }

        companion object {
            const val xposed_installer_packageName = "de.robv.android.xposed.installer"
            const val edxposed_installer_packageName = "com.solohsu.android.edxp.manager"
            const val edxposed_manager_packageName = "org.meowcat.edxposed.manager"
        }

        class PackageInstallDetect constructor(private val packageManager: PackageManager) {

            fun isPackageInstall(packageName: String): Boolean {
                return try {
                    packageManager.getPackageInfo(packageName, PackageManager.GET_GIDS)
                    true
                } catch (e: Exception) {
                    //ignore
                    false
                }
            }
        }
    }

    enum class Statue {
        Edxp_notActive, Edxp_Active, taichi_notActive, taichi_magisk_notActive, taichi_active, taichi_magisk_active, xposed_active, xposed_notActive
    }

    @Target(
        AnnotationTarget.TYPE_PARAMETER,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER
    )
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(TAICHI_NOT_INSTALL, TAICHI_NOT_ACTIVE, TAICHI_ACTIVE)
    annotation class Taichi_statue

    class GetMagiskModule: BaseGetMagiskModule() {

        companion object {
            const val moduleLocate = "/data/adb/modules"
        }

        init {
            Shell.su("su")
            val result: Shell.Result =
                Shell.su("ls $moduleLocate").exec()
            val resultString: String = result.getOut().toString()
            //Log.d("getMagiskModule", resultString);
            if (resultString.contains("edxp")) edxpModule = true
            if (resultString.contains("taichi")) taichiModule = true
        }
    }

    open class BaseGetMagiskModule {
        var taichiModule = false
        var edxpModule = false
    }

}