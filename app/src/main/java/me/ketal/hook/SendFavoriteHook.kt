package me.ketal.hook

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.TextView
import de.robv.android.xposed.XC_MethodHook
import me.ketal.util.HookUtil.getMethod
import me.ketal.util.HookUtil.hookMethod
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.H
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.util.DexKit
import nil.nadph.qnotified.util.ReflexUtil
import nil.nadph.qnotified.util.Utils


object SendFavoriteHook: CommonDelayableHook("ketal_send_favorite", SyncUtils.PROC_ANY, DexDeobfStep(DexKit.N_PluginProxyActivity__initPlugin)) {
    var isHooked: Boolean = false

    override fun isValid(): Boolean {
        return H.isQQ() && H.getVersionCode() >= QQVersion.QQ_8_0_0
    }

    override fun initOnce(): Boolean {
        try {
            DexKit.doFindMethod(DexKit.N_PluginProxyActivity__initPlugin)
                .hookMethod(object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (!isEnabled) return
                        try {
                            val context = param.thisObject as Activity
                            val intent = context.intent
                            if (intent.check()) {
                                val classLoader = "Lcom/tencent/mobileqq/pluginsdk/PluginStatic;->getClassLoader(Ljava/lang/String;)Ljava/lang/ClassLoader;"
                                    .getMethod()
                                    ?.invoke(null, "qqfav.apk") as ClassLoader
                                startHook(classLoader)
                            }
                        } catch (e: Exception) {
                            Utils.log(e)
                        }
                    }
                })
            return true
        } catch (e: Exception) {
            Utils.log(e)
            return false
        }
    }

    private fun startHook(classLoader: ClassLoader) {
        "Lcom/qqfav/activity/FavoritesListActivity;->onCreate(Landroid/os/Bundle;)V"
            .getMethod(classLoader)
            ?.hookMethod(object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    if (!isEnabled) return
                    try {
                        val thisObj = param.thisObject as Activity
                        isHooked = thisObj.intent.getBooleanExtra("bEnterToSelect", false)
                        if (!isHooked) return
                        val tv = findCancelTV(thisObj, "com.qqfav.activity.QfavBaseActivity".findClass(classLoader))
                        val logic = ReflexUtil.new_instance("com.qqfav.activity.FavoriteGroupLogic".findClass(classLoader),
                            thisObj, tv, thisObj::class.java, View::class.java)
                        tv?.setOnClickListener {
                            try {
                                ReflexUtil.invoke_virtual(logic, "b")
                                val b = ReflexUtil.iget_object_or_null(logic, "b", View::class.java)
                                if (b.visibility != 0) {
                                    ReflexUtil.invoke_virtual(logic, "a")
                                } else {
                                    ReflexUtil.invoke_virtual(logic, "a", true, Boolean::class.java)
                                }
                            } catch (e: Exception) {
                                Utils.log(e)
                            }
                        }
                    } catch (e: Exception) {
                        Utils.log(e)
                    }
                }
            })
    }

    private fun findCancelTV(thisObject: Any, clazz: Class<*>) : TextView? {
        for (field in clazz.declaredFields) {
            field.isAccessible = true
            if (field[thisObject] is TextView) {
                val tv = field[thisObject] as TextView
                if (tv.text == "取消") {
                    tv.text = "选择分组"
                    return tv
                }
            }
        }
        return null
    }

    private fun getClassName(intent: Intent): String? {
        return when (intent.getIntExtra("nOperation", -1)) {
            0, 1, 3, 6, 7, 8, 11 -> "com.qqfav.FavoriteIpcDelegate"
            2 -> "com.qqfav.activity.FavoritesListActivity"
            9 -> "com.qqfav.group.activity.QfavGroupActivity"
            else -> {
                val component = intent.component ?: return null
                component.className
            }
        }
    }

    private fun Intent.check(): Boolean =
        "com.qqfav.activity.FavoritesListActivity" == getClassName(this)

    private fun String.findClass(classLoader: ClassLoader, init: Boolean = false): Class<*> =
        Class.forName(this, init, classLoader)
}
