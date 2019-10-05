package nil.nadph.qnotified;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import nil.nadph.qnotified.pk.FriendChunk;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.ui.DebugDrawable;
import nil.nadph.qnotified.util.*;

import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashSet;

import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static nil.nadph.qnotified.ActProxyMgr.*;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

/*TitleKit:Lcom/tencent/mobileqq/widget/navbar/NavBarCommon*/


public class QQMainHook<SlideDetectListView extends ViewGroup> implements IXposedHookLoadPackage {

    public static final int VIEW_ID_DELETED_FRIEND = 0x00EE77AA;

    public static final String QN_FULL_TAG = "qn_full_tag";
    public HashSet addedListView = new HashSet();
    private boolean __state_mini_app_hidden = false;

    public static final int R_ID_PTT_FORWARD = 0x00EE77CB;

    XC_LoadPackage.LoadPackageParam lpparam;

    public static WeakReference<Activity> splashActivityRef;

    TextView exfriend;
    public static WeakReference<TextView> redDotRef;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam _lpparam) throws Throwable {
        try {
            this.lpparam = _lpparam;
            XC_MethodHook startup = new XC_MethodHook(51) {
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        FileInputStream fin = new FileInputStream("/proc/" + android.os.Process.myPid() + "/cmdline");
                        byte[] b = new byte[64];
                        int len = fin.read(b, 0, b.length);
                        fin.close();
                        String procName = new String(b, 0, len).trim();
                        //XposedBridge.log(procName);
                        if (procName.endsWith(":peak")) return;
                        if (procName.endsWith(":qzone")) return;
                        if (procName.endsWith(":tool")) return;
                        if (procName.endsWith(":MSF")) return;
                        Utils.checkLogFlag();
                        Context ctx = null;
                        Class clz = param.thisObject.getClass().getClassLoader().loadClass("com.tencent.common.app.BaseApplicationImpl");
                        Field f = hasField(clz, "sApplication");
                        if (f == null) ctx = (Context) sget_object(clz, "a", clz);
                        else ctx = (Context) f.get(null);
                        ClassLoader classLoader = ctx.getClassLoader();
                        Initiator.init(classLoader);
                        //log("Clases init done");
                        if (classLoader == null) throw new AssertionError("ERROR:classLoader==null");
                        injectStartupHook();
                        Class director = load("com/tencent/mobileqq/startup/director/StartupDirector");
                        if (director == null)
                            director = load("com/tencent/mobileqq/startup/director/StartupDirector$1").getDeclaredField("this$0").getType();
                        Class loadData = load("com/tencent/mobileqq/startup/step/LoadData");
                        Method doStep = null;
                        for (Method method : loadData.getDeclaredMethods()) {
                            if (method.getReturnType().equals(boolean.class) && method.getParameterTypes().length == 0) {
                                doStep = method;
                                break;
                            }
                        }
                        Class __director = director;
                        XposedBridge.hookMethod(doStep, new XC_MethodHook(51) {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                Object dir = iget_object_or_null(param.thisObject, "mDirector", __director);
                                if (dir == null) dir = iget_object_or_null(param.thisObject, "a", __director);
                                InjectDelayableHooks.step(dir);
                            }
                        });
                    } catch (Throwable e) {
                        log(e);
                        throw e;
                    }
                }
            };
            Class loadDex = lpparam.classLoader.loadClass("com.tencent.mobileqq.startup.step.LoadDex");
            Method[] ms = loadDex.getDeclaredMethods();
            Method m = null;
            for (Method method : ms) {
                if (method.getReturnType().equals(boolean.class) && method.getParameterTypes().length == 0) {
                    m = method;
                    break;
                }
            }
            XposedBridge.hookMethod(m, startup);
            findAndHookMethodIfExists("com.tencent.common.app.QFixApplicationImpl", lpparam.classLoader, "isAndroidNPatchEnable", XC_MethodReplacement.returnConstant(500, false));
        } catch (Throwable e) {
            if ((e + "").contains("com.bug.zqq")) return;
            log(e);
            throw e;
        }
    }

    private void injectStartupHook() {
        if (Utils.DEBUG) {
            if ("true".equals(System.getProperty(QN_FULL_TAG))) {
                log("Err:QNotified reloaded??");
                //return;
                System.exit(-1);
                //QNotified updated(in HookLoader mode),kill QQ to make user restart it.
            }
            System.setProperty(QN_FULL_TAG, "true");
        }

        Class clazz = load(ActProxyMgr.STUB_ACTIVITY);
        if (clazz != null) {
            ActProxyMgr mgr = ActProxyMgr.getInstance();
            findAndHookMethod(clazz, "onCreate", Bundle.class, mgr);
            findAndHookMethodIfExists(clazz, "doOnDestroy", mgr);
            findAndHookMethodIfExists(clazz, "onActivityResult", int.class, int.class, Intent.class, mgr);
            findAndHookMethodIfExists(clazz, "doOnPause", mgr);
            findAndHookMethodIfExists(clazz, "doOnResume", mgr);
            findAndHookMethodIfExists(clazz, "isWrapContent", mgr);
        }

		/*
		 try {
		 Method m = null;
		 Method[] methods = load("com.tencent.mobileqq.activity.BaseChatPie").getDeclaredMethods();
		 for (int i = 0; i < methods.length; i++) {
		 Method method = methods[i];
		 if (method.getName().equals("e") && method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
		 m = methods[i];
		 break;
		 }
		 }
		 assert m != null;
		 XposedBridge.hookMethod(m, new XC_MethodHook(51) {
		 @Override
		 public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
		 if (ConfigManager.get().getBooleanOrFalse(qn_send_card_msg)) {
		 final Object qqi = iget_object(methodHookParam.thisObject, "a", load("com.tencent.mobileqq.app.QQAppInterface"));
		 final Object session = iget_object(methodHookParam.thisObject, "a", load("com.tencent.mobileqq.activity.aio.SessionInfo"));
		 final ViewGroup viewGroup = (ViewGroup) iget_object(methodHookParam.thisObject, "d", Class.forName("android.view.ViewGroup"));
		 Resources res = viewGroup.getContext().getResources();
		 int id_btn = res.getIdentifier("fun_btn", "id", null);
		 final int id_et = res.getIdentifier("input", "id", null);
		 if (viewGroup != null)
		 ((Button) viewGroup.findViewById(id_btn).setOnLongClickListener(new View.OnLongClickListener() {
		 @Override
		 public boolean onLongClick(View view) {
		 EditText edit = (EditText) viewGroup.findViewById(id_et);
		 String input = edit.getText().toString();
		 boolean success = false;
		 Class cl_msgMgr = load((String) Hook.config.get("MessageManager"));
		 try {
		 Object msg = invoke_static(load((String) Hook.config.get("TestStructMsg")), "a", input, load("com.tencent.mobileqq.structmsg.AbsStructMsg"));
		 if (msg != null) {
		 invoke_static(cl_msgMgr, "a", qqi, session, msg);
		 success = true;
		 }
		 } catch (Throwable th) {
		 Toast.makeText(view.getContext(), th.toString(), Toast.LENGTH_SHORT).show();
		 XposedBridge.log(th);
		 }
		 try {
		 Object arkMsg = new_instance(load("com.tencent.mobileqq.data.ArkAppMessage"));
		 if ((Boolean) invoke_virtual(arkMsg, "fromAppXml", input)) {
		 invoke_static(cl_msgMgr, "a", qqi, session, arkMsg);
		 success = true;
		 }
		 } catch (Throwable th2) {
		 XposedBridge.log(th2);
		 }
		 if (success) edit.setText("");
		 return false;
		 }
		 }));

		 }
		 }
		 });
		 } catch (Throwable e) {
		 log(e);
		 }

		 /*
		 findAndHookMethod(load("friendlist/DelFriendReq"),"writeTo",load("com/qq/taf/jce/JceOutputStream"),new XC_MethodHook(70){
		 @Override
		 protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
		 Field f=param.thisObject.getClass().getDeclaredField("delType");
		 f.setAccessible(true);
		 f.set(param.thisObject,(byte)2);
		 }
		 });

		 //findAndHookMethod(load("friendlist/AddFriendReq"),"writeTo",load("com/qq/taf/jce/JceOutputStream"),invokeRecord);
		 /*findAndHookMethod(load("friendlist/AddFriendReq"),"writeTo",load("com/qq/taf/jce/JceOutputStream"),new XC_MethodHook(10){
		 @Override
		 protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
		 Field f=param.thisObject.getClass().getDeclaredField("sourceSubID");
		 f.setAccessible(true);
		 f.set(param.thisObject,1);
		 f=param.thisObject.getClass().getDeclaredField("sourceID");
		 f.setAccessible(true);
		 f.set(param.thisObject,3071);
		 f=param.thisObject.getClass().getDeclaredField("myfriendgroupid");
		 f.setAccessible(true);
		 f.set(param.thisObject,(byte)0);
		 /*f=param.thisObject.getClass().getDeclaredField("adduinsetting");
		 f.setAccessible(true);
		 f.set(param.thisObject,4);*

		 }
		 });//*/

        initSettingsEntry();
        asyncStartFindClass();
        initMuteAtAllAndRedPacket();
        initDelDetector();
        hideMiniAppEntry();
        initCardMsg();
        initPttForward();
    }

    private void initMuteAtAllAndRedPacket() {
        try {
            Class cl_MessageInfo = load("com/tencent/mobileqq/troop/data/MessageInfo");
            if (cl_MessageInfo == null) {
                Class c = load("com/tencent/mobileqq/data/MessageRecord");
                cl_MessageInfo = c.getDeclaredField("mMessageInfo").getType();
            }
            /* @author qiwu */
            final int at_all_type = (Utils.getHostInfo(getApplication()).versionName.compareTo("7.8.0") >= 0) ? 13 : 12;
            findAndHookMethod(cl_MessageInfo, "a", load("com/tencent/mobileqq/app/QQAppInterface"), boolean.class, String.class, new XC_MethodHook(60) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    int ret = (int) param.getResult();
                    String troopuin = (String) param.args[2];
                    if (ret != at_all_type) return;
                    String muted = "," + ConfigManager.getDefault().getString(qn_muted_at_all) + ",";
                    if (muted.contains("," + troopuin + ",")) {
                        param.setResult(0);
                    }
                }
            });
        } catch (Exception e) {
            log(e);
        }
        try {
            findAndHookMethod(load("com.tencent.mobileqq.data.MessageForQQWalletMsg"), "doParse", new XC_MethodHook(200) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    boolean mute = false;
                    int istroop = (Integer) iget_object_or_null(param.thisObject, "istroop");
                    if (istroop != 1) return;
                    String troopuin = (String) iget_object_or_null(param.thisObject, "frienduin");
                    String muted = "," + ConfigManager.getDefault().getString(qn_muted_red_packet) + ",";
                    if (muted.contains("," + troopuin + ",")) mute = true;
                    if (mute) XposedHelpers.setObjectField(param.thisObject, "isread", true);
                }
            });
        } catch (Exception e) {
            log(e);
        }
    }

    private void initDelDetector() {
        findAndHookMethod(load("com/tencent/widget/PinnedHeaderExpandableListView"), "setAdapter", ExpandableListAdapter.class, exfriendEntryHook);
        XposedHelpers.findAndHookMethod(load("com/tencent/mobileqq/activity/SplashActivity"), "doOnResume", new XC_MethodHook(700) {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    if (Utils.getLongAccountUin() > 10000) {
                        ExfriendManager ex = ExfriendManager.getCurrent();
                        ex.timeToUpdateFl();
                    }
                } catch (Throwable e) {
                    log(e);
                    throw e;
                }
            }
        });

		/*
		 findAndHookMethod(load("friendlist/DelFriendReq"),"readFrom",load("com/qq/taf/jce/JceInputStream"),invokeRecord);
		 *
		 findAndHookMethod(load("friendlist/DelFriendReq"),"writeTo",load("com/qq/taf/jce/JceOutputStream"),new XC_MethodHook(200){
		 @Override
		 protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
		 splashActivity.runOnUiThread(new Runnable(){
		 @Override
		 public void run(){
		 try{
		 Utils.showToast(Utils.getApplication(),Utils.TOAST_TYPE_ERROR,"拒绝访问: 非法操作",0);
		 }catch(Throwable e){
		 log(e);
		 }
		 }
		 });
		 param.setThrowable(new IOException("Permission denied"));
		 }
		 });

		 /*findAndHookMethod(load("friendlist/DelFriendResp"),"readFrom",load("com/qq/taf/jce/JceInputStream"),invokeRecord);
		 findAndHookMethod(load("friendlist/DelFriendResp"),"writeTo",load("com/qq/taf/jce/JceOutputStream"),invokeRecord);
		 *
		 findAndHookMethod(load("friendlist/GetFriendListReq"),"writeTo",load("com/qq/taf/jce/JceOutputStream"),invokeRecord);

		 findAndHookMethod(load("com/tencent/mobileqq/service/friendlist/FriendListService"),"n",load("com/tencent/qphone/base/remote/ToServiceMsg"),load("com/qq/jce/wup/UniPacket"),invokeRecord);
		*/
        findAndHookMethod(load("friendlist/GetFriendListResp"), "readFrom", load("com/qq/taf/jce/JceInputStream"), new XC_MethodHook(200) {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    FriendChunk fc = new FriendChunk(param.thisObject);
                    ExfriendManager.onGetFriendListResp(fc);
						/*String ret="dump object:"+param.thisObject.getClass().getCanonicalName()+"\n";
						 Field[] fs=param.thisObject.getClass().getDeclaredFields();
						 for(int i=0;i<fs.length;i++){
						 fs[i].setAccessible(true);
						 ret+=(i<fs.length-1?"├":"└")+fs[i].getName()+"="+ClazzExplorer.en_toStr(fs[i].get(param.thisObject))+"\n";
						 }
						 log(ret);*/
                } catch (Throwable e) {
                    log(e);
                    throw e;
                }
            }
        });

        findAndHookMethod(load("friendlist/DelFriendResp"), "readFrom", load("com/qq/taf/jce/JceInputStream"), new XC_MethodHook(200) {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    long uin = (Long) iget_object_or_null(param.thisObject, "uin");
                    long deluin = (Long) iget_object_or_null(param.thisObject, "deluin");
                    int result = (Integer) iget_object_or_null(param.thisObject, "result");
                    short errorCode = (Short) iget_object_or_null(param.thisObject, "errorCode");
                    if (result == 0 && errorCode == 0) ExfriendManager.get(uin).markActiveDelete(deluin);
						/*String ret="dump object:"+param.thisObject.getClass().getCanonicalName()+"\n";
						 Field[] fs=param.thisObject.getClass().getDeclaredFields();
						 for(int i=0;i<fs.length;i++){
						 fs[i].setAccessible(true);
						 ret+=(i<fs.length-1?"├":"└")+fs[i].getName()+"="+ClazzExplorer.en_toStr(fs[i].get(param.thisObject))+"\n";
						 }
						 log(ret);*/
                } catch (Throwable e) {
                    log(e);
                    throw e;
                }
            }
        });
    }

    private void hideMiniAppEntry() {
        try {
            if (Utils.isTim(getApplication())) return;
        } catch (Exception ignored) {
        }
        try {
            ConfigManager cfg = ConfigManager.getDefault();
            if (cfg.getBooleanOrFalse(qn_hide_msg_list_miniapp)) {
                int lastVersion = cfg.getIntOrDefault("qn_hide_msg_list_miniapp_version_code", 0);
                if (getHostInfo(getApplication()).versionCode == lastVersion) {
                    String methodName = cfg.getString("qn_hide_msg_list_miniapp_method_name");
                    findAndHookMethod(load("com/tencent/mobileqq/activity/Conversation"), methodName, XC_MethodReplacement.returnConstant(null));
                } else {
                    Class con = load("com/tencent/mobileqq/activity/Conversation");
                    for (Method m : con.getDeclaredMethods()) {
                        Class[] ps = m.getParameterTypes();
                        if (ps != null && ps.length > 0) continue;
                        if (!m.getReturnType().equals(void.class)) continue;
                        String name = m.getName();
                        if (!Modifier.isPrivate(m.getModifiers())) continue;
                        if (name.length() > 1) continue;
                        char c = name.charAt(0);
                        if ('F' <= c && c < 'a')
                            XposedBridge.hookMethod(m, new XC_MethodReplacement(30) {
                                @Override
                                protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                                    try {
                                        Method m = (Method) param.method;
                                        m.setAccessible(true);
                                        XposedBridge.invokeOriginalMethod(m, param.thisObject, param.args);
                                    } catch (InvocationTargetException e) {
                                        if (!(e.getCause() instanceof UnsupportedOperationException)) {
                                            log(e);
                                        }
                                    } catch (Throwable t) {
                                        log(t);
                                    }
                                    return null;
                                }
                            });
                    }
					/*try {
					 findAndHookMethod(load("com.tencent.mobileqq.app.FrameFragment"), "createTabContent", String.class, new XC_MethodReplacement(39) {
					 @Override
					 protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
					 try {
					 Method m = (Method) param.method;
					 m.setAccessible(true);
					 XposedBridge.invokeOriginalMethod(m, param.thisObject, param.args);
					 } catch (UnsupportedOperationException e) {
					 } catch (Throwable t) {
					 log(t);
					 }
					 return null;
					 }
					 });
					 } catch (Exception e) {}*/
                    Class miniapp = load("com/tencent/mobileqq/mini/entry/MiniAppEntryAdapter");
                    if (miniapp == null)
                        miniapp = load("com/tencent/mobileqq/mini/entry/MiniAppEntryAdapter$1").getDeclaredField("this$0").getType();
                    XposedBridge.hookAllConstructors(miniapp, new XC_MethodHook(60) {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            String methodName = null;
                            StackTraceElement[] stacks = new Throwable().getStackTrace();
                            for (int i = 0; i < stacks.length; i++) {
                                if (stacks[i].getClassName().indexOf("Conversation") != -1) {
                                    methodName = stacks[i].getMethodName();
                                    break;
                                }
                            }
                            if (methodName == null)
                                throw new NullPointerException("Failed to get Conversation.?() to hide MiniApp!");
                            ConfigManager cfg = ConfigManager.getDefault();
                            cfg.putString("qn_hide_msg_list_miniapp_method_name", methodName);
                            cfg.getAllConfig().put("qn_hide_msg_list_miniapp_version_code", getHostInfo(getApplication()).versionCode);
                            cfg.save();
                            param.setThrowable(new UnsupportedOperationException("MiniAppEntry disabled"));
                        }
                    });
                }
            }
        } catch (Exception e) {
            log(e);
        }
    }

    private void initCardMsg() {
        try {
            Class cl_BaseBubbleBuilder = load("com.tencent.mobileqq.activity.aio.BaseBubbleBuilder");
            Class cl_ChatMessage = load("com.tencent.mobileqq.data.ChatMessage");
            Class cl_BaseChatItemLayout = load("com.tencent.mobileqq.activity.aio.BaseChatItemLayout");
            assert cl_BaseBubbleBuilder != null;
            assert cl_ChatMessage != null;
            assert cl_BaseChatItemLayout != null;
            Method[] ms = cl_BaseBubbleBuilder.getDeclaredMethods();
            Method m = null;
            Class[] argt;
            for (int i = 0; i < ms.length; i++) {
                argt = ms[i].getParameterTypes();
                if (argt.length != 6) continue;
                if (argt[0].equals(cl_ChatMessage) && argt[1].equals(Context.class)
                        && argt[2].equals(cl_BaseChatItemLayout) && argt[4].equals(int.class)
                        && argt[5].equals(int.class)) {
                    m = ms[i];
                }
            }
            XposedBridge.hookMethod(m, new XC_MethodHook(51) {
                private static final int R_ID_BB_LAYOUT = 0x300AFF41;
                private static final int R_ID_BB_TEXTVIEW = 0x300AFF42;

                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    if (ConfigManager.getDefault().getBooleanOrFalse(qn_send_card_msg)) {
                        final Object msgObj = methodHookParam.args[0];
                        ViewGroup viewGroup = (ViewGroup) methodHookParam.args[2];
                        if (!load("com.tencent.mobileqq.data.MessageForStructing").isAssignableFrom(msgObj.getClass())
                                && !load("com.tencent.mobileqq.data.MessageForArkApp").isAssignableFrom(msgObj.getClass()))
                            return;
                        if (viewGroup.findViewById(R_ID_BB_LAYOUT) == null) {
                            Context context = viewGroup.getContext();
                            LinearLayout linearLayout = new LinearLayout(context);
                            linearLayout.setId(R_ID_BB_LAYOUT);
                            //linearLayout.setBackground(new DebugDrawable(context));//SimpleBgDrawable(0x00000000, Color.BLUE, dip2px(context, 1)));
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                            lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                            TextView textView = new TextView(context);
                            textView.setId(R_ID_BB_TEXTVIEW);
                            textView.setGravity(Gravity.CENTER);
                            textView.setTextColor(Color.RED);
                            textView.setText("长按复制");
                            linearLayout.addView(textView, lp);
                            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
                            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            //rlp.addRule(RelativeLayout.ALIGN_PARENT_TO
                            int i = dip2px(context, 2);
                            rlp.setMargins(i, i, i, i);
                            //linearLayout.hashCode();
                            viewGroup.addView(linearLayout, rlp);
                            //iput_object(viewGroup,"DEBUG_DRAW",true);
                        }
                        ((TextView) viewGroup.findViewById(R_ID_BB_TEXTVIEW)).setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                try {
                                    ClipboardManager clipboardManager = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                    if (load("com.tencent.mobileqq.data.MessageForStructing").isAssignableFrom(msgObj.getClass())) {
                                        clipboardManager.setText((String) invoke_virtual(iget_object_or_null(msgObj, "structingMsg"), "getXml", new Object[0]));
                                        showToast(view.getContext(), TOAST_TYPE_INFO, "复制成功", Toast.LENGTH_SHORT);
                                    } else if (load("com.tencent.mobileqq.data.MessageForArkApp").isAssignableFrom(msgObj.getClass())) {
                                        clipboardManager.setText((String) invoke_virtual(iget_object_or_null(msgObj, "ark_app_message"), "toAppXml", new Object[0]));
                                        showToast(view.getContext(), TOAST_TYPE_INFO, "复制成功", Toast.LENGTH_SHORT);
                                    }
                                } catch (Throwable ignored) {
                                }
                                return true;
                            }
                        });
                        viewGroup.setBackgroundDrawable(new DebugDrawable(viewGroup.getContext()));
                    }
                }
            });
        } catch (Throwable e) {
            log(e);
        }
    }

    private void asyncStartFindClass() {
        if (DexKit.tryLoadOrNull(DexKit.C_DIALOG_UTIL) == null)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ignored) {
                    }
                    DexKit.doFindClass(DexKit.C_DIALOG_UTIL);
                }
            }).start();
        if (DexKit.tryLoadOrNull(DexKit.C_FACADE) == null)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ignored) {
                    }
                    DexKit.doFindClass(DexKit.C_FACADE);
                }
            }).start();
        if (DexKit.tryLoadOrNull(DexKit.C_FLASH_PIC_HELPER) == null)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(8000);
                    } catch (InterruptedException ignored) {
                    }
                    DexKit.doFindClass(DexKit.C_FLASH_PIC_HELPER);
                }
            }).start();
    }

    private void initPttForward() {
        try {
            Class clz_ForwardBaseOption = load("com/tencent/mobileqq/forward/ForwardBaseOption");
            if (clz_ForwardBaseOption == null) {
                Class clz_DirectForwardActivity = load("com/tencent/mobileqq/activity/DirectForwardActivity");
                for (Field f : clz_DirectForwardActivity.getDeclaredFields()) {
                    if (Modifier.isStatic(f.getModifiers())) continue;
                    Class clz = f.getType();
                    if (Modifier.isAbstract(clz.getModifiers()) && !clz.getName().contains("android")) {
                        clz_ForwardBaseOption = clz;
                        break;
                    }
                }
            }
            Method buildConfirmDialog = null;
            for (Method m : clz_ForwardBaseOption.getDeclaredMethods()) {
                if (!m.getReturnType().equals(void.class)) continue;
                if (!Modifier.isFinal(m.getModifiers())) continue;
                if (m.getParameterTypes().length != 0) continue;
                buildConfirmDialog = m;
                break;
            }
            XposedBridge.hookMethod(buildConfirmDialog, new XC_MethodHook(51) {
                @SuppressLint("SetTextI18n")
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Bundle data = (Bundle) Utils.iget_object_or_null(param.thisObject, "a", Bundle.class);
                    if (!data.containsKey("ptt_forward_path")) return;
                    param.setResult(null);
                    final String path = data.getString("ptt_forward_path");
                    final Activity ctx = (Activity) Utils.iget_object_or_null(param.thisObject, "a", Activity.class);
                    if (path == null || !new File(path).exists()) {
                        Utils.showToast(ctx, TOAST_TYPE_ERROR, "InternalError: Invalid ptt file!", Toast.LENGTH_SHORT);
                        return;
                    }
                    QThemeKit.initTheme(ctx);
                    boolean multi;
                    final ArrayList<Utils.ContactDescriptor> mTargets = new ArrayList<>();
                    boolean unsupport = false;
                    if (data.containsKey("forward_multi_target")) {
                        ArrayList targets = data.getParcelableArrayList("forward_multi_target");
                        if (targets.size() > 1) {
                            multi = true;
                            for (Object rr : targets) {
                                Utils.ContactDescriptor c = Utils.parseResultRec(rr);
                                mTargets.add(c);
                            }
                        } else {
                            multi = false;
                            Utils.ContactDescriptor c = Utils.parseResultRec(targets.get(0));
                            mTargets.add(c);
                        }
                    } else {
                        multi = false;
                        Utils.ContactDescriptor cd = new ContactDescriptor();
                        cd.uin = data.getString("uin");
                        cd.uinType = data.getInt("uintype", -1);
                        cd.nick = data.getString("uinname", data.getString("uin"));
                        mTargets.add(cd);
                    }
                    if (unsupport) Utils.showToastShort(ctx, "暂不支持我的设备/临时聊天/讨论组");
                    LinearLayout main = new LinearLayout(ctx);
                    main.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout heads = new LinearLayout(ctx);
                    heads.setGravity(Gravity.CENTER_VERTICAL);
                    heads.setOrientation(LinearLayout.HORIZONTAL);
                    View div = new View(ctx);
                    div.setBackgroundColor(QThemeKit.skin_gray3.getDefaultColor());
                    TextView tv = new TextView(ctx);
                    tv.setText("[语音转发]" + path);
                    tv.setTextColor(QThemeKit.skin_gray3);
                    tv.setTextSize(dip2sp(ctx, 16));
                    int pd = dip2px(ctx, 8);
                    tv.setPadding(pd, pd, pd, pd);
                    main.addView(heads, MATCH_PARENT, WRAP_CONTENT);
                    main.addView(div, MATCH_PARENT, 1);
                    main.addView(tv, MATCH_PARENT, WRAP_CONTENT);
                    int w = dip2px(ctx, 40);
                    LinearLayout.LayoutParams imglp = new LinearLayout.LayoutParams(w, w);
                    imglp.setMargins(pd, pd, pd, pd);
                    FaceImpl face = FaceImpl.getInstance();
                    if (multi) {
                        if (mTargets != null) for (Utils.ContactDescriptor cd : mTargets) {
                            ImageView imgview = new ImageView(ctx);
                            Bitmap bm = face.getBitmapFromCache(cd.uinType == 1 ? FaceImpl.TYPE_TROOP : FaceImpl.TYPE_USER, cd.uin);
                            if (bm == null) {
                                imgview.setImageDrawable(QThemeKit.loadDrawableFromAsset("face.png", ctx));
                                face.registerView(cd.uinType == 1 ? FaceImpl.TYPE_TROOP : FaceImpl.TYPE_USER, cd.uin, imgview);
                            } else {
                                imgview.setImageBitmap(bm);
                            }
                            heads.addView(imgview, imglp);
                        }
                    } else {
                        Utils.ContactDescriptor cd = mTargets.get(0);
                        ImageView imgview = new ImageView(ctx);
                        Bitmap bm = face.getBitmapFromCache(cd.uinType == 1 ? FaceImpl.TYPE_TROOP : FaceImpl.TYPE_USER, cd.uin);
                        if (bm == null) {
                            imgview.setImageDrawable(QThemeKit.loadDrawableFromAsset("face.png", ctx));
                            face.registerView(cd.uinType == 1 ? FaceImpl.TYPE_TROOP : FaceImpl.TYPE_USER, cd.uin, imgview);
                        } else {
                            imgview.setImageBitmap(bm);
                        }
                        heads.setPadding(pd / 2, pd / 2, pd / 2, pd / 2);
                        TextView ni = new TextView(ctx);
                        ni.setText(cd.nick);
                        ni.setTextColor(0xFF000000);
                        ni.setPadding(pd, 0, 0, 0);
                        ni.setTextSize(dip2sp(ctx, 18));
                        heads.addView(imgview, imglp);
                        heads.addView(ni);
                    }
                    //String ret = "" +/*ctx.getIntent().getExtras();//*/iget_object(param.thisObject, "a", Bundle.class);
                    Dialog dialog = Utils.createDialog(ctx);
                    invoke_virtual(dialog, "setPositiveButton", "确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                for (Utils.ContactDescriptor cd : mTargets) {
                                    Object sesssion = Utils.createSessionInfo(cd.uin, cd.uinType);
                                    XposedHelpers.callStaticMethod(DexKit.doFindClass(DexKit.C_FACADE), "a", Utils.getQQAppInterface(), sesssion, path);
                                }
                                Utils.showToast(ctx, TOAST_TYPE_SUCCESS, "已发送", Toast.LENGTH_SHORT);
                            } catch (Throwable e) {
                                log(e);
                                try {
                                    Utils.showToast(ctx, TOAST_TYPE_ERROR, "失败: " + e, Toast.LENGTH_SHORT);
                                } catch (Throwable ignored) {
                                    Toast.makeText(ctx, "失败: " + e, Toast.LENGTH_SHORT).show();
                                }
                            }
                            ctx.finish();
                        }
                    }, String.class, DialogInterface.OnClickListener.class);
                    invoke_virtual(dialog, "setNegativeButton", "取消", new Utils.DummyCallback(), String.class, DialogInterface.OnClickListener.class);
                    dialog.setCancelable(true);
                    invoke_virtual(dialog, "setView", main, View.class);
                    invoke_virtual(dialog, "setTitle", "发送给", String.class);
                    dialog.show();
                }
            });
            Class cl_PttItemBuilder = load("com/tencent/mobileqq/activity/aio/item/PttItemBuilder");
            if (cl_PttItemBuilder == null) {
                Class cref = load("com/tencent/mobileqq/activity/aio/item/PttItemBuilder$2");
                cl_PttItemBuilder = cref.getDeclaredField("this$0").getType();
            }
            findAndHookMethod(cl_PttItemBuilder, "a", int.class, Context.class, load("com/tencent/mobileqq/data/ChatMessage"), new XC_MethodHook(60) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    int id = (int) param.args[0];
                    Activity context = (Activity) param.args[1];
                    Object chatMessage = param.args[2];
                    if (id == R_ID_PTT_FORWARD) {
                        param.setResult(null);
                        String url = (String) invoke_virtual(chatMessage, "getLocalFilePath");
                        File file = new File(url);
                        if (!file.exists()) {
                            Utils.showToast(context, TOAST_TYPE_ERROR, "未找到语音文件", Toast.LENGTH_SHORT);
                            return;
                        }
                        Intent intent = new Intent(context, load("com/tencent/mobileqq/activity/ForwardRecentActivity"));
                        intent.putExtra("selection_mode", 0);
                        intent.putExtra("direct_send_if_dataline_forward", false);
                        intent.putExtra("forward_text", "null");
                        intent.putExtra("ptt_forward_path", file.getPath());
                        intent.putExtra("forward_type", -1);
                        intent.putExtra("caller_name", "ChatActivity");
                        intent.putExtra("k_smartdevice", false);
                        intent.putExtra("k_dataline", false);
                        intent.putExtra("k_forward_title", "语音转发");
                        context.startActivity(intent);
                    }
                }
            });
            for (Method m : cl_PttItemBuilder.getDeclaredMethods()) {
                if (!m.getReturnType().isArray()) continue;
                Class[] ps = m.getParameterTypes();
                if (ps.length == 1 && ps[0].equals(View.class))
                    XposedBridge.hookMethod(m, new XC_MethodHook(60) {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                ConfigManager cfg = ConfigManager.getDefault();
                                if (!cfg.getBooleanOrFalse(qn_enable_ptt_forward)) return;
                            } catch (Exception ignored) {
                            }
                            Object arr = param.getResult();
                            Object QQCustomMenuItem = Array.get(arr, 0).getClass().newInstance();
                            iput_object(QQCustomMenuItem, "a", int.class, R_ID_PTT_FORWARD);
                            iput_object(QQCustomMenuItem, "a", String.class, "转发");
                            Object ret = Array.newInstance(QQCustomMenuItem.getClass(), Array.getLength(arr) + 1);
                            Array.set(ret, 0, Array.get(arr, 0));
                            System.arraycopy(arr, 1, ret, 2, Array.getLength(arr) - 1);
                            Array.set(ret, 1, QQCustomMenuItem);
                            param.setResult(ret);
                        }
                    });
            }
        } catch (Exception e) {
            log(e);
        }
    }

    private void initSettingsEntry() {
        XposedHelpers.findAndHookMethod(load("com.tencent.mobileqq.activity.QQSettingSettingActivity"), "doOnCreate", Bundle.class, new XC_MethodHook(47) {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                try {
                    View itemRef = (View) Utils.iget_object_or_null(param.thisObject, "a", load("com/tencent/mobileqq/widget/FormSimpleItem"));
                    if (itemRef == null)
                        itemRef = (View) Utils.iget_object_or_null(param.thisObject, "a", load("com/tencent/mobileqq/widget/FormCommonSingleLineItem"));
                    View item = (View) new_instance(itemRef.getClass(), param.thisObject, Context.class);
                    invoke_virtual(item, "setLeftText", "QNotified", CharSequence.class);
                    invoke_virtual(item, "setRightText", Utils.QN_VERSION_NAME, CharSequence.class);
                    LinearLayout list = (LinearLayout) itemRef.getParent();
                    list.addView(item, 0, itemRef.getLayoutParams());
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startProxyActivity((Context) param.thisObject, ACTION_ADV_SETTINGS);
                        }
                    });
                } catch (Throwable e) {
                    log(e);
                    throw e;
                }
            }
        });
    }

    public XC_MethodHook invokeRecord = new XC_MethodHook(200) {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws IllegalAccessException, IllegalArgumentException {
            Member m = param.method;
            String ret = m.getDeclaringClass().getSimpleName() + "->" + ((m instanceof Method) ? m.getName() : "<init>") + "(";
            Class[] argt;
            if (m instanceof Method)
                argt = ((Method) m).getParameterTypes();
            else if (m instanceof Constructor)
                argt = ((Constructor) m).getParameterTypes();
            else argt = new Class[0];
            for (int i = 0; i < argt.length; i++) {
                if (i != 0) ret += ",\n";
                ret += param.args[i];
            }
            ret += ")=" + param.getResult();
            Utils.log(ret);
            ret = "↑dump object:" + m.getDeclaringClass().getCanonicalName() + "\n";
            Field[] fs = m.getDeclaringClass().getDeclaredFields();
            for (int i = 0; i < fs.length; i++) {
                fs[i].setAccessible(true);
                ret += (i < fs.length - 1 ? "├" : "↓") + fs[i].getName() + "=" + ClazzExplorer.en_toStr(fs[i].get(param.thisObject)) + "\n";
            }
            log(ret);
            Utils.dumpTrace();
        }
    };

    public static XC_MethodHook.Unhook findAndHookMethodIfExists(Class<?> clazz, String methodName, Object...
            parameterTypesAndCallback) {
        try {
            return findAndHookMethod(clazz, methodName, parameterTypesAndCallback);
        } catch (Throwable e) {
            log(e.toString());
            return null;
        }
    }

    public static XC_MethodHook.Unhook findAndHookMethodIfExists(String clazzName, ClassLoader cl, String
            methodName, Object... parameterTypesAndCallback) {
        try {
            return findAndHookMethod(clazzName, cl, methodName, parameterTypesAndCallback);
        } catch (Throwable e) {
            log(e.toString());
            return null;
        }
    }

    public static void startProxyActivity(Context ctx, int action) {
        Intent intent = new Intent(ctx, load(ActProxyMgr.STUB_ACTIVITY));
        int id = ActProxyMgr.next();
        intent.putExtra(ACTIVITY_PROXY_ID_TAG, id);
        intent.putExtra(ACTIVITY_PROXY_ACTION, action);
        intent.putExtra("fling_action_key", 2);
        intent.putExtra("fling_code_key", ctx.hashCode());
        ctx.startActivity(intent);
    }

    public static void openProfileCard(Context ctx, long uin) {
        try {
            Parcelable allInOne = (Parcelable) new_instance(load("com/tencent/mobileqq/activity/ProfileActivity$AllInOne"), "" + uin, 35, String.class, int.class);
            Intent intent = new Intent(ctx, load("com/tencent/mobileqq/activity/FriendProfileCardActivity"));
            intent.putExtra("AllInOne", allInOne);
            ctx.startActivity(intent);
        } catch (Exception e) {
            log(e);
        }
    }

    private XC_MethodHook exfriendEntryHook = new XC_MethodHook(1200) {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            try {
                boolean hide = false;
                try {
                    hide = ConfigManager.getDefault().getBooleanOrFalse("qn_hide_ex_entry_group");
                } catch (Throwable e) {
                    log(e);
                }
                if (hide) return;
                if (!param.thisObject.getClass().getName().contains("ContactsFPSPinnedHeaderExpandableListView"))
                    return;
                LinearLayout layout_entrance;
                //android.widget.FrameLayout frameView;
                View lv = (View) param.thisObject;
                //frameView=Utils.getObject(,View.class,"b");
                final Activity splashActivity = (Activity) Utils.getContext(lv);
                QThemeKit.initTheme(splashActivity);
                //lv=(ContactsFPSPinnedHeaderExpandableListView) iget_object(param.thisObject,"a",load("com/tencent/mobileqq/activity/contacts/view/ContactsFPSPinnedHeaderExpandableListView"));
                //log("Fuckee:"+lv.getClass());
                //TextView unusualContacts;
				/*if(frameView.getChildAt(0) instanceof LinearLayout){
				 if(frameView.getVisibility()==View.GONE){
				 /*兼容QQ净化->隐藏不常用联系人,上面的1200也是一样*
				 frameView.setVisibility(View.VISIBLE);
				 if(unusualContacts!=null)unusualContacts.setVisibility(View.GONE);
				 }
				 return;
				 }*/
                //unusualContacts=(TextView)frameView.getChildAt(0);

                layout_entrance = new LinearLayout(splashActivity);
                RelativeLayout rell = new RelativeLayout(splashActivity);
                //rell.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT));

                //Object adapter=invoke_virtual(lv,"getAdapter",ListAdapter.class);
                //invoke_virtual(lv,"setAdapter",null,BaseAdapter.class);
				/*try{
				 invoke_virtual(lv,"removeFooterView",layout,View.class);
				 }catch(Exception e){log(e);}
				 */
                if (!addedListView.contains(lv)) {
                    //log("fucking it!");
                    invoke_virtual_original(lv, "addFooterView", layout_entrance, View.class);
                    addedListView.add(lv);
                    //invoke_static(XposedBridge.class,"dumpObjectNative",lv,Object.class);
                    //lv.setVisibility(View.GONE);
                }

                //invoke_virtual(lv,"setAdapter",adapter,BaseAdapter.class);

                layout_entrance.setOrientation(LinearLayout.VERTICAL);

                //StateListDrawable background=(StateListDrawable)unusualContacts.getBackground();

                exfriend = new TextView(splashActivity);
                exfriend.setTextColor(QThemeKit.skin_blue);//unusualContacts.getTextColors());//QThemeKit.skin_red);
                //exfriend.setBackground(Utils._obj_clone(background.mutate()));//damn! mutate() not working!
                exfriend.setTextSize(dip2sp(splashActivity, 17));//TypedValue.COMPLEX_UNIT_PX,unusualContacts.getTextSize());
                exfriend.setId(VIEW_ID_DELETED_FRIEND);
                exfriend.setText("历史好友");
                exfriend.setGravity(Gravity.CENTER);
                exfriend.setClickable(true);
                //exfriend.setTranslationY(-Utils.dip2px(splashActivity,1f));
                //unusualContacts.setVisibility(frameView.getVisibility()==View.GONE?View.GONE:View.VISIBLE);
                //frameView.setVisibility(View.VISIBLE);

                TextView redDot = new TextView(splashActivity);
                redDotRef = new WeakReference<>(redDot);
                redDot.setTextColor(0xFFFF0000);

                redDot.setGravity(Gravity.CENTER);
                //redDot.setBackground(QThemeKit.skin_tips_newmessage);
                redDot.getPaint().setFakeBoldText(true);
                //redDot.setTextAppearance(android.R.style.TextAppearance_Small);
                redDot.setTextSize(Utils.dip2sp(splashActivity, 10));
                //redDot.setPadding(4,0,4,0);
                try {
                    invoke_static(load("com/tencent/widget/CustomWidgetUtil"), "a", redDot, 3, 1, 0, TextView.class, int.class, int.class, int.class, void.class);
                } catch (NullPointerException e) {
                    redDot.setTextColor(Color.RED);
                }
                ExfriendManager.get(Utils.getLongAccountUin()).setRedDot();


                //frameView.removeAllViews();
                int height = dip2px(splashActivity, 48);//unusualContacts.getLayoutParams().height;
                //layout.addView(unusualContacts);
                RelativeLayout.LayoutParams exlp = new RelativeLayout.LayoutParams(MATCH_PARENT, height);
                exlp.topMargin = 0;
                exlp.leftMargin = 0;

                rell.addView(exfriend, exlp);
                RelativeLayout.LayoutParams dotlp = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                dotlp.topMargin = 0;
                dotlp.rightMargin = Utils.dip2px(splashActivity, 24);
                dotlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                dotlp.addRule(RelativeLayout.CENTER_VERTICAL);
                rell.addView(redDot, dotlp);
                layout_entrance.addView(rell);//,unusualContacts.getLayoutParams());
                ViewGroup.LayoutParams llp = new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                layout_entrance.setPadding(0, (int) (height * 0.3f), 0, (int) (0.3f * height));
				/*frameView.addView(layout,llp);
				 ViewGroup.LayoutParams _lp=frameView.getLayoutParams();
				 _lp.height=WRAP_CONTENT;//(int)(unusual.getLayoutParams().height*());
				 final View.OnClickListener olds=Utils.getOnClickListener(frameView);
				 frameView.setOnTouchListener(null);
				 frameView.setClickable(false);
				 //unusualContacts_old.setOnTouchListener(null);*/
                exfriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(splashActivity, load(ActProxyMgr.STUB_ACTIVITY));
                        int id = ActProxyMgr.next();
                        intent.putExtra(ACTIVITY_PROXY_ID_TAG, id);
                        intent.putExtra(ACTIVITY_PROXY_ACTION, ACTION_EXFRIEND_LIST);
                        splashActivity.startActivity(intent);
                        //Toast.makeText(splashActivity,"Test",0).show();
                    }
                });
				/*unusualContacts.setOnClickListener(new View.OnClickListener(){
				 @Override
				 public void onClick(View v){
				 olds.onClick(frameView);
				 }
				 });
				 unusualContacts.invalidate();*/
                exfriend.postInvalidate();
				/*new Thread(new Runnable(){
				 @Override
				 public void run(){
				 try{
				 Thread.sleep(500);
				 }catch(InterruptedException e){}
				 exfriend.postInvalidate();
				 unusual.postInvalidate();
				 }
				 }).start();*/

                //log("[End of putting entrance]");
            } catch (Throwable e) {
                log(e);
                throw e;
            }
        }

    };

}
