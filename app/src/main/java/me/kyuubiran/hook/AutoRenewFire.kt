package me.kyuubiran.hook

import android.content.Context
import android.content.Intent
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.kyuubiran.dialog.AutoRenewFireDialog
import me.kyuubiran.util.*
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.hook.BaseDelayableHook
import nil.nadph.qnotified.step.Step
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method

//自动续火
object AutoRenewFire : BaseDelayableHook() {
    const val kr_auto_renew_fire: String = "kr_auto_renew_fire"
    var isInit = false
    var autoRenewFireStarted = false

    override fun getPreconditions(): Array<Step?> {
        return arrayOfNulls(0)
    }

    override fun init(): Boolean {
        if (!autoRenewFireStarted) {
            AutoRenewFireMgr.doAutoSend()
            autoRenewFireStarted = true
        }
        if (isInit) return true
        return try {
            val FormSimpleItem: Class<*> = loadClass("com.tencent.mobileqq.widget.FormSwitchItem")
            for (m: Method in getMethods("com.tencent.mobileqq.activity.ChatSettingActivity")) {
                if (m.name == "doOnCreate") {
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            if (LicenseStatus.sDisableCommonHooks) return
                            if (!isEnabled) return
                            //如果未启用 不显示按钮
                            if (!getExFriendCfg().getBooleanOrFalse(kr_auto_renew_fire)) return
                            //获取 设为置顶 SwitchItem
                            val setToTopItem = getObjectOrNull(param.thisObject, "b", FormSimpleItem)
                            //如果SwitchItem不为空 说明为好友
                            if (setToTopItem != null) {
                                //创建SwitchItem对象
                                val autoRenewFireItem =
                                    Utils.new_instance(FormSimpleItem, param.thisObject, Context::class.java)
                                //拿到ViewGroup
                                val listView = (setToTopItem as View).parent as ViewGroup
                                //设置开关文本
                                Utils.invoke_virtual(autoRenewFireItem, "setText", "自动续火", CharSequence::class.java)
                                //添加View
                                listView.addView(autoRenewFireItem as View, 7)
                                //拿到好友相关信息
                                val intent =
                                    getObjectOrNull(param.thisObject, "a", Intent::class.java) as Intent
                                //QQ
                                val uin = intent.getStringExtra("uin")
                                //昵称
                                val uinName = intent.getStringExtra("uinname")
                                //设置按钮是否启用
                                Utils.invoke_virtual(
                                    autoRenewFireItem,
                                    "setChecked",
                                    AutoRenewFireMgr.hasEnabled(uin),
                                    Boolean::class.java
                                )
                                //设置监听事件
                                Utils.invoke_virtual(
                                    autoRenewFireItem,
                                    "setOnCheckedChangeListener",
                                    object : CompoundButton.OnCheckedChangeListener {
                                        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                                            if (p1) {
                                                AutoRenewFireMgr.add(uin)
                                                (param.thisObject as Context).showToastByTencent("已开启与${uinName}的自动续火")
                                            } else {
                                                AutoRenewFireMgr.remove(uin)
                                                (param.thisObject as Context).showToastByTencent("已关闭与${uinName}的自动续火")
                                            }
                                        }
                                    },
                                    CompoundButton.OnCheckedChangeListener::class.java
                                )
                                if (LicenseStatus.isInsider()) {
                                    autoRenewFireItem.setOnLongClickListener {
                                        AutoRenewFireDialog.showSetMsgDialog(param.thisObject as Context, uin)
                                        true
                                    }
                                }
                            }
                        }
                    })
                }
            }
            isInit = true
            true
        } catch (t: Throwable) {
            logdt(t)
            false
        }
    }

    override fun isEnabled(): Boolean {
        return try {
            getDefaultCfg().getBooleanOrFalse(kr_auto_renew_fire)
        } catch (e: java.lang.Exception) {
            Utils.log(e)
            false
        }
    }

    override fun getEffectiveProc(): Int {
        return SyncUtils.PROC_MAIN
    }

    override fun setEnabled(enabled: Boolean) {
        try {
            val mgr = getDefaultCfg()
            mgr.allConfig[kr_auto_renew_fire] = enabled
            mgr.save()
        } catch (e: Exception) {
            Utils.log(e)
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Utils.showToast(Utils.getApplication(), Utils.TOAST_TYPE_ERROR, e.toString() + "", Toast.LENGTH_SHORT)
            } else {
                SyncUtils.post {
                    Utils.showToast(
                        Utils.getApplication(),
                        Utils.TOAST_TYPE_ERROR,
                        e.toString() + "",
                        Toast.LENGTH_SHORT
                    )
                }
            }
        }
    }

    override fun isInited(): Boolean {
        return isInit
    }
}