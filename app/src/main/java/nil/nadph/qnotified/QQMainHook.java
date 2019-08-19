package nil.nadph.qnotified;

import android.app.*;
import android.os.*;
import android.content.*;
import android.view.*;
import android.widget.*;
import java.lang.reflect.*;
import android.view.View.*;
import android.util.*;
import java.io.*;
import android.graphics.*;
import android.content.res.*;
import android.graphics.drawable.*;
import android.text.*;
import nil.nadph.qnotified.pk.*;
import java.util.*;
import java.lang.ref.*;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage.*;
import de.robv.android.xposed.callbacks.*;

import static nil.nadph.qnotified.Utils.log;
import static nil.nadph.qnotified.Utils.invoke_static;
import static nil.nadph.qnotified.Utils.*;
import static nil.nadph.qnotified.Utils.iget_object;
import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static nil.nadph.qnotified.Initiator.load;
import static nil.nadph.qnotified.QQViewBuilder.*;
import de.robv.android.xposed.XC_MethodHook.*;
import dalvik.system.*;

/*TitleKit:Lcom/tencent/mobileqq/widget/navbar/NavBarCommon*/


public class QQMainHook <SlideDetectListView extends ViewGroup,ContactsFPSPinnedHeaderExpandableListView extends View> implements IXposedHookLoadPackage{

	public static final int VIEW_ID_DELETED_FRIEND=0x00EE77AA;
	public static final String ACTIVITY_PROXY_ID_TAG="qn_act_proxy_id";
	public static final String ACTIVITY_PROXY_ACTION="qn_act_proxy_action";
	public static final int ACTION_EXFRIEND_LIST=1;
	public static final int ACTION_ADV_SETTINGS=2;
	/*public static final int ACTION_ABOUT=3;*/
	public static final int ACTION_SHELL=4;
	public static final int ACTION_SHUTUP_AT_ALL=5;
	public static final int ACTION_SHUTUP_RED_PACKET=6;

	public static final String QN_FULL_TAG="qn_full_tag";
	public HashSet addedListView=new HashSet();
	private boolean __state_mini_app_hidden=false;


	XC_LoadPackage.LoadPackageParam lpparam;

	public static WeakReference<Activity> splashActivityRef;

	TextView exfriend;
	public static WeakReference<TextView> redDotRef;

	/*XC_MethodHook.Unhook[] unhook=new XC_MethodHook.Unhook[3];*/

	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam _lpparam) throws Throwable{
		try{
			this.lpparam=_lpparam;
			XC_MethodHook startup=new XC_MethodHook(51) {
				protected void afterHookedMethod(MethodHookParam param) throws Throwable{
					try{
						FileInputStream fin=new FileInputStream("/proc/"+android.os.Process.myPid()+"/cmdline");
						byte[]b=new byte[64];
						int len=fin.read(b,0,b.length);
						fin.close();Activity a;
						String procName=new String(b,0,len).trim();
						//XposedBridge.log(procName);
						if(procName.endsWith(":peak"))return;
						if(procName.endsWith(":qzone"))return;
						if(procName.endsWith(":tool"))return;
						if(procName.endsWith(":MSF"))return;
						Utils.checkLogFlag();
						Context ctx=null;
						Class clz=param.thisObject.getClass().getClassLoader().loadClass("com.tencent.common.app.BaseApplicationImpl");
						Field f=hasField(clz,"sApplication");
						if(f==null)ctx=(Context)sget_object(clz,"a",clz);
						else ctx=(Context)f.get(null);
						performHook(ctx.getClassLoader());
					}catch(Throwable e){
						log(e);
						throw e;
					}
				}
			};
			/*XposedHelpers.
			 /*findAndHookMethodIfExists("com.tencent.mobileqq.app.InjectUtils",lpparam.classLoader,"injectExtraDexes",Application.class,boolean.class,startup);
			 findAndHookMethodIfExists("com.tencent.mobileqq.app.InjectUtils",lpparam.classLoader,"a",Application.class,boolean.class,startup);*/
			Class loadDex=lpparam.classLoader.loadClass("com.tencent.mobileqq.startup.step.LoadDex");
			Method[] ms=loadDex.getDeclaredMethods();
			Method m=null;
			for(int i=0;i<ms.length;i++){
				//log(""+ms[i].getReturnType());
				if(ms[i].getReturnType().equals(boolean.class)&&ms[i].getParameterTypes().length==0){
					m=ms[i];
					break;
				}
			}
			XposedBridge.hookMethod(m,startup);
			findAndHookMethodIfExists("com.tencent.common.app.QFixApplicationImpl",lpparam.classLoader,"isAndroidNPatchEnable",XC_MethodReplacement.returnConstant(500,false));
		}catch(Throwable e){
			log(e);
			throw e;
		}
	}

	/*
	 private class SearchEntrance implements Runnable,TextWatcher,View.OnClickListener{

	 View.OnClickListener mOriginalOnClickListener;
	 int id;
	 @Override
	 public void onClick(View v){
	 log("onClick");
	 mOriginalOnClickListener.onClick(v);
	 EditText t=splashActivity.findViewById(id);
	 //t.addTextChangedListener(this);
	 }

	 EditText et=null;
	 EditText ptet=null;
	 ScrollView l;
	 @Override
	 public void run(){
	 if(et==null){
	 id=splashActivity.getResources().getIdentifier("et_search_keyword","id",splashActivity.getPackageName());
	 //log("id="+id);
	 for(int i=0;i<10;i++){
	 try{
	 Thread.sleep(1000);
	 if((et=splashActivity.findViewById(id))!=null){
	 splashActivity.runOnUiThread(this);
	 //log(et.toString());
	 return;
	 }
	 }catch(InterruptedException e){}
	 }
	 }else{
	 //log(et.getClass().toString());
	 mOriginalOnClickListener=Utils.getOnClickListener(et);
	 et.setOnClickListener(this);
	 }
	 }

	 @Override
	 public void beforeTextChanged(CharSequence s,int start,int count,int after){
	 // TODO: Implement this method
	 }

	 @Override
	 public void onTextChanged(CharSequence s,int start,int before,int count){
	 // TODO: Implement this method
	 }

	 @Override
	 public void afterTextChanged(Editable s){
	 log(s.toString());
	 if(s.toString().toLowerCase().equals("#ex")){
	 startProxyActivity(splashActivity,ACTION_EXFRIEND_LIST);
	 }
	 }
	 }


	 */
	private void performHook(ClassLoader classLoader){
		/*if(Utils.DEBUG){
			if("true".equals(System.getProperty(QN_FULL_TAG))){
				log("Err:Qnotified reloaded??");
				System.exit(-1);
				//QNotified updated(in HookLoader mode),kill QQ to make user restart it.
			}
			System.setProperty(QN_FULL_TAG,"true");
		}*/
		Initiator.init(classLoader);
		log("Clases init done");
		log("App:"+Utils.getApplication());
		if(classLoader==null)log("ERROR:classLoader==null");
		/*try{
		 Thread.sleep(5000);
		 }catch(InterruptedException e){}*
		 try{
		 findAndHookMethod(load("com.tencent.mobileqq.activity.SplashActivity"),"doOnCreate",Bundle.class,new XC_MethodHook(200){
		 @Override
		 protected void afterHookedMethod(MethodHookParam param){
		 splashActivity=(Activity)param.thisObject;
		 new Thread(new SearchEntrance()).start();
		 }
		 });
		 }catch(Exception e){}
		 /*findAndHookMethod(load("com.tencent.mobileqq.data.MessageForQQWalletMsg"),"doParse",new XC_MethodHook(200){
		 @Override
		 protected void afterHookedMethod(MethodHookParam param){
		 XposedHelpers.setObjectField(param.thisObject,"isread",true);
		 }
		 });*/



		/*XposedBridge.hookAllMethods(load("com/tencent/mobileqq/util/FaceDecoder"),"a",
		 });*/

		Class clazz=load(".activity.contacts.fragment.FriendFragment");//".activity.Contacts");
		/*findAndHookMethod(clazz,"i",pastEntry);
		 findAndHookMethod(clazz,"j",pastEntry);*/
		findAndHookMethod(load("com/tencent/widget/PinnedHeaderExpandableListView"),"setAdapter",ExpandableListAdapter.class,exfriendEntryHook);
		//log("Will load");
		clazz=load(ActProxyMgr.STUB_ACTIVITY);
		//log(""+clazz);
		if(clazz!=null){
			findAndHookMethod(clazz,"onCreate",Bundle.class,proxyActivity_onCreate);
			//findAndHookMethod(clazz,"doOnCreate",Bundle.class,proxyActivity_doOnCreate);
			findAndHookMethodIfExists(clazz,"doOnDestroy",proxyActivity_doOnDestroy);
			findAndHookMethodIfExists(clazz,"onActivityResult",int.class,int.class,Intent.class,proxyActivity_doOnActivityResult);
			findAndHookMethodIfExists(clazz,"doOnPause",proxyActivity_doOnPause);
			findAndHookMethodIfExists(clazz,"doOnResume",proxyActivity_doOnResume);
			findAndHookMethodIfExists(clazz,"isWrapContent",proxyActivity_isWrapContent);
		}
		XposedHelpers.findAndHookMethod(load("com/tencent/mobileqq/activity/SplashActivity"),"doOnResume",new XC_MethodHook(700){
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable{
					try{
						if(Utils.getLongAccountUin()>10000){
							ExfriendManager ex=ExfriendManager.getCurrent();
							ex.timeToUpdateFl();
						}
					}catch(Throwable e){
						log(e);
						throw e;
					}
				}
			});

		/*findAndHookMethod("com.tencent.mobileqq.activity.contact.newfriend.NewFriendActivity",classLoader,"doOnCreate",android.os.Bundle.class,new XC_MethodHook(200){
		 @Override
		 protected void afterHookedMethod(MethodHookParam param){
		 log("NewFriendActivity->doOnCreate");
		 ClazzExplorer ce=ClazzExplorer.get();
		 ce.rootEle=ce.currEle=param.thisObject;
		 ce.track.removeAllElements();
		 ce.init((Activity)param.thisObject);
		 }
		 });
		 XC_MethodHook.Unhook unh=findAndHookMethod(Classes.Contacts,"o",new XC_MethodHook(200) {
		 @Override
		 protected void afterHookedMethod(MethodHookParam param) throws Throwable{
		 RelativeLayout newFriendEntry = getObject(param.thisObject,View.class,"a");
		 //View createTroopEntry = getObject(param.thisObject,View.class,"b");
		 //View searchBox = ((LinearLayout) (newFriendEntry.getParent())).getChildAt(0);
		 // 搜索框
		 View tag=newFriendEntry.getChildAt(0);
		 Utils.ref_setText(tag,"故旧-新欢");
		 //log("Setting secondary element...");

		 }
		 });*/


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

		 // XposedBridge.hookAllConstructors
		 XposedHelpers.findAndHookConstructor(load("com/tencent/mobileqq/activity/fling/FlingGestureHandler"),Activity.class,invokeRecord);
		 findAndHookMethod(load("com/tencent/mobileqq/activity/fling/FlingGestureHandler"),"a",invokeRecord);
		 findAndHookMethod(load("com/tencent/mobileqq/activity/fling/FlingHandler"),"onStart",invokeRecord);
		 */

		/*findAndHookMethod(load("friendlist/GetFriendListResp"),"readFrom",load("com/qq/taf/jce/JceInputStream"),invokeRecord);
		 */


		findAndHookMethod(load("friendlist/GetFriendListResp"),"readFrom",load("com/qq/taf/jce/JceInputStream"),new XC_MethodHook(200){
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable{
					try{
						FriendChunk fc=new FriendChunk(param.thisObject);
						ExfriendManager.onGetFriendListResp(fc);
						/*String ret="dump object:"+param.thisObject.getClass().getCanonicalName()+"\n";
						 Field[] fs=param.thisObject.getClass().getDeclaredFields();
						 for(int i=0;i<fs.length;i++){
						 fs[i].setAccessible(true);
						 ret+=(i<fs.length-1?"├":"└")+fs[i].getName()+"="+ClazzExplorer.en_toStr(fs[i].get(param.thisObject))+"\n";
						 }
						 log(ret);*/
					}catch(Throwable e){
						log(e);
						throw e;
					}
				}
			});

		findAndHookMethod(load("friendlist/DelFriendResp"),"readFrom",load("com/qq/taf/jce/JceInputStream"),new XC_MethodHook(200){
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable{
					try{
						long uin=iget_object(param.thisObject,"uin");
						long deluin=iget_object(param.thisObject,"deluin");
						int result=iget_object(param.thisObject,"result");
						short errorCode=iget_object(param.thisObject,"errorCode");
						if(result==0&&errorCode==0)ExfriendManager.get(uin).markActiveDelete(deluin);
						/*String ret="dump object:"+param.thisObject.getClass().getCanonicalName()+"\n";
						 Field[] fs=param.thisObject.getClass().getDeclaredFields();
						 for(int i=0;i<fs.length;i++){
						 fs[i].setAccessible(true);
						 ret+=(i<fs.length-1?"├":"└")+fs[i].getName()+"="+ClazzExplorer.en_toStr(fs[i].get(param.thisObject))+"\n";
						 }
						 log(ret);*/
					}catch(Throwable e){
						log(e);
						throw e;
					}
				}
			});

		try{
			ConfigManager cfg=ConfigManager.get();
			if(cfg.getBooleanOrFalse("qn_hide_msg_list_miniapp")){
				int lastVersion=cfg.getIntOrDefault("qn_hide_msg_list_miniapp_version_code",0);
				if(getQQVersionCode(getApplication())==lastVersion){
					String methodName=cfg.getString("qn_hide_msg_list_miniapp_method_name");
					findAndHookMethod(load("com/tencent/mobileqq/activity/Conversation"),methodName,XC_MethodReplacement.returnConstant(null));
				}else{
					findAndHookMethod(load("mqq/os/MqqHandler"),"dispatchMessage",Message.class,new XC_MethodReplacement(51){
							@Override
							protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable{
								try{
									Method m=(Method)param.method;
									m.setAccessible(true);
									XposedBridge.invokeOriginalMethod(m,param.thisObject,param.args);
								}catch(UnsupportedOperationException e){}catch(Throwable t){
									log(t);
								}
								return null;
							}
						});
					Class miniapp=load("com/tencent/mobileqq/mini/entry/MiniAppEntryAdapter");
					if(miniapp==null)miniapp=load("com/tencent/mobileqq/mini/entry/MiniAppEntryAdapter$1").getDeclaredField("this$0").getType();
					XposedBridge.hookAllConstructors(miniapp,new XC_MethodHook(60){
							@Override
							protected void beforeHookedMethod(MethodHookParam param)throws Throwable{
								String methodName=null;
								StackTraceElement[] stacks=new Throwable().getStackTrace();
								for(int i=0;i<stacks.length;i++){
									if(stacks[i].getClassName().indexOf("Conversation")!=-1){
										methodName=stacks[i].getMethodName();
										break;
									}
								}
								if(methodName==null)throw new NullPointerException("Failed to get Conversation.?() to hide MiniApp!");
								ConfigManager cfg=ConfigManager.get();
								cfg.putString("qn_hide_msg_list_miniapp_method_name",methodName);
								cfg.getAllConfig().put("qn_hide_msg_list_miniapp_version_code",getQQVersionCode(getApplication()));
								cfg.save();
								param.setThrowable(new UnsupportedOperationException("MiniAppEntry disabled"));
							}
						});
				}
			}
		}catch(Exception e){
			log(e);
		}
		
		log("will fuck setting2");
		XposedHelpers.findAndHookMethod(load("com.tencent.mobileqq.activity.QQSettingSettingActivity"),"doOnCreate",Bundle.class,new XC_MethodHook(47){
				@Override
				protected void afterHookedMethod(final MethodHookParam param) throws Throwable{
					try{
						View itemRef=(View)iget_object(param.thisObject,"a",load("com/tencent/mobileqq/widget/FormSimpleItem"));
						View item=(View)new_instance(itemRef.getClass(),param.thisObject,Context.class);
						invoke_virtual(item,"setLeftText","QNotified",CharSequence.class);
						invoke_virtual(item,"setRightText",Utils.QN_VERSION_NAME,CharSequence.class);
						LinearLayout list=(LinearLayout) itemRef.getParent();
						list.addView(item,0,itemRef.getLayoutParams());
						item.setOnClickListener(new View.OnClickListener(){
								@Override
								public void onClick(View v){
									startProxyActivity((Context)param.thisObject,ACTION_ADV_SETTINGS);
								}
							});
					}catch(Throwable e){
						log(e);
						throw e;
					}
				}
			});


		/*XposedBridge.hookAllMethods(load("aydf"),"a",invokeRecord);
		 XposedBridge.hookAllMethods(load("aydf"),"b",invokeRecord);
		 XposedBridge.hookAllMethods(load("aydu"),"a",invokeRecord);/*new XC_MethodHook(60){
		 *
		 @Override
		 protected void beforeHookedMethod(MethodHookParam param){

		 }
		 });



		 /*clazz=load("com.tencent.mobileqq.activity.Conversation");
		 if(clazz!=null){
		 try{
		 findAndHookMethod(clazz,"F",new XC_MethodHook(60){
		 @Override
		 protected void beforeHookedMethod(MethodHookParam param){
		 param.setResult(null);
		 }
		 });

		 }catch(Exception e){}
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

		 /*
		 XposedBridge.hookMethod(XposedHelpers.findMethodBestMatch(load("com/tencent/mobileqq/activity/UncommonlyUsedContactsActivity"),"finish",new Class[]{}),invokeRecord);
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
	}

	public XC_MethodHook invokeRecord=new XC_MethodHook(200){
		@Override
		protected void afterHookedMethod(MethodHookParam param) throws IllegalAccessException, IllegalArgumentException{
			Member m=param.method;
			String ret=m.getDeclaringClass().getSimpleName()+"->"+((m instanceof Method)?m.getName():"<init>")+"(";
			Class[] argt;
			if(m instanceof Method)
				argt=((Method)m).getParameterTypes();
			else if(m instanceof Constructor)
				argt=((Constructor)m).getParameterTypes();
			else argt=new Class[0];
			for(int i=0;i<argt.length;i++){
				if(i!=0)ret+=",\n";
				ret+=param.args[i];
			}
			ret+=")="+param.getResult();
			Utils.log(ret);
			ret="↑dump object:"+m.getDeclaringClass().getCanonicalName()+"\n";
			Field[] fs=m.getDeclaringClass().getDeclaredFields();
			for(int i=0;i<fs.length;i++){
				fs[i].setAccessible(true);
				ret+=(i<fs.length-1?"├":"↓")+fs[i].getName()+"="+ClazzExplorer.en_toStr(fs[i].get(param.thisObject))+"\n";
			}
			log(ret);
			Utils.dumpTrace();
		}
	};

	public static XC_MethodHook.Unhook findAndHookMethodIfExists(Class<?> clazz,String methodName,Object... parameterTypesAndCallback){
		try{
			return findAndHookMethod(clazz,methodName,parameterTypesAndCallback);
		}catch(Throwable e){
			log(e.toString());
			return null;
		}
	}

	public static XC_MethodHook.Unhook findAndHookMethodIfExists(String clazzName,ClassLoader cl,String methodName,Object... parameterTypesAndCallback){
		try{
			return findAndHookMethod(clazzName,cl,methodName,parameterTypesAndCallback);
		}catch(Throwable e){
			log(e.toString());
			return null;
		}
	}

	public static void startProxyActivity(Context ctx,int action){
		Intent intent=new Intent(ctx,load(ActProxyMgr.STUB_ACTIVITY));
		int id=ActProxyMgr.next();
		intent.putExtra(ACTIVITY_PROXY_ID_TAG,id);
		intent.putExtra(ACTIVITY_PROXY_ACTION,action);
		intent.putExtra("fling_action_key",2);
		intent.putExtra("fling_code_key",ctx.hashCode());
		ctx.startActivity(intent);
	}

	public static void openProfileCard(Context ctx,long uin){
		try{
			Parcelable allInOne=(Parcelable) new_instance(load("com/tencent/mobileqq/activity/ProfileActivity$AllInOne"),""+uin,35,String.class,int.class);
			Intent intent=new Intent(ctx,load("com/tencent/mobileqq/activity/FriendProfileCardActivity"));
			intent.putExtra("AllInOne",allInOne);
            ctx.startActivity(intent);
		}catch(Exception e){
			log(e);
		}
	}


	public Object exlist_mFlingHandler=null;
	private XC_MethodHook proxyActivity_onCreate=new XC_MethodHook(200){
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
			try{
				if(ActProxyMgr.isInfiniteLoop())return;
				final Activity self=(Activity)param.thisObject;
				int id=self.getIntent().getExtras().getInt(ACTIVITY_PROXY_ID_TAG,-1);
				int action=self.getIntent().getExtras().getInt(ACTIVITY_PROXY_ACTION,-1);
				if(id<=0)return;
				if(action<=0)return;
				param.setResult(null);
				//ActProxyMgr.set(id,self);
				Method m=load("mqq/app/AppActivity").getDeclaredMethod("onCreate",Bundle.class);
				m.setAccessible(true);
				try{
					ActProxyMgr.invokeSuper(self,m,param.args);
				}catch(ActProxyMgr.BreakUnaughtException e){}


				//log("***onCreate");
				if(action==ACTION_EXFRIEND_LIST)
					try{
						exlist_mFlingHandler=new_instance(load("com/tencent/mobileqq/activity/fling/FlingGestureHandler"),self,Activity.class);
						iput_object(self,"mFlingHandler",exlist_mFlingHandler);
						QThemeKit.initTheme(self);

						SlideDetectListView sdlv=(SlideDetectListView)load("com.tencent.widget.SwipListView").getConstructor(Context.class,AttributeSet.class).newInstance(self,null);
						sdlv.setFocusable(true);
						ViewGroup.LayoutParams mmlp=new ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT);
						RelativeLayout.LayoutParams mwllp=new RelativeLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
						RelativeLayout rl=new RelativeLayout(self);//)new_instance(load("com/tencent/mobileqq/activity/fling/TopGestureLayout"),self,Context.class);
						//invoke_virtual(rl,"setInterceptScrollLRFlag",true,boolean.class);
						//invoke_virtual(rl,"setInterceptTouchFlag",true,boolean.class);
						//iput_object(rl,"
						rl.setBackgroundColor(QThemeKit.qq_setting_item_bg_nor.getDefaultColor());
						mwllp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
						mwllp.addRule(RelativeLayout.CENTER_VERTICAL);

						TextView tv=new TextView(self);
						tv.setGravity(Gravity.CENTER);
						tv.setTextColor(QThemeKit.skin_gray3);
						tv.setTextSize(Utils.dip2sp(self,14));
						rl.addView(tv,mwllp);
						rl.addView(sdlv,mmlp);
						self.setContentView(rl);
						//sdlv.setBackgroundColor(0xFFAA0000)
						invoke_virtual(self,"setTitle","历史好友",CharSequence.class);
						invoke_virtual(self,"setImmersiveStatus");
						invoke_virtual(self,"enableLeftBtn",true,boolean.class);
						TextView rightBtn=(TextView)invoke_virtual(self,"getRightTextView");
						//log("Title:"+invoke_virtual(self,"getTextTitle"));
						rightBtn.setVisibility(View.VISIBLE);
						rightBtn.setText("高级");
						rightBtn.setEnabled(true);
						rightBtn.setOnClickListener(new View.OnClickListener(){
								@Override
								public void onClick(View v){
									try{
										//self.onBackPressed();
										//ExfriendManager.getCurrent().doRequestFlRefresh();
										//Utils.showToastShort(v.getContext(),"即将开放(没啥好设置的)...");
										startProxyActivity(self,ACTION_ADV_SETTINGS);
										//Intent intent=new Intent(v.getContext(),load(ActProxyMgr.DUMMY_ACTIVITY));
										//int id=ActProxyMgr.next();
										//intent.putExtra(ACTIVITY_PROXY_ID_TAG,id);
										//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										/*v.getContext().startActivity(intent);/*
										 new Thread(new Runnable(){
										 @Override
										 public void run(){
										 /*try{
										 Thread.sleep(10000);
										 }catch(InterruptedException e){}
										 EventRecord ev=new EventRecord();
										 ev.operator=10000;
										 ev._remark=ev._nick="麻花藤";
										 *
										 ExfriendManager.getCurrent().doNotifyDelFl(new Object[]{1,"ticker","title","content"});
										 }
										 }).start();*/
									}catch(Throwable e){
										log(e);
									}
								}
							});
						//.addView(sdlv,lp);
						invoke_virtual(sdlv,"setDragEnable",true,boolean.class);
						invoke_virtual(sdlv,"setDivider",null,Drawable.class);
						long uin=Utils.getLongAccountUin();
						ExfriendManager exm=ExfriendManager.get(uin);
						exm.clearUnreadFlag();
						tv.setText("最后更新: "+Utils.getRelTimeStrSec(exm.lastUpdateTimeSec));
						QQViewBuilder.listView_setAdapter(sdlv,new ExfriendListAdapter(sdlv,exm));
						//invoke_virtual(sdlv,"setOnScrollGroupFloatingListener",true,load("com/tencent/widget/AbsListView$OnScrollListener"));
					}catch(Throwable e){
						log(e);
					}
				else if(action==ACTION_ADV_SETTINGS)
					try{
						exlist_mFlingHandler=new_instance(load("com/tencent/mobileqq/activity/fling/FlingGestureHandler"),self,Activity.class);
						iput_object(self,"mFlingHandler",exlist_mFlingHandler);
						QThemeKit.initTheme(self);
						LinearLayout ll=new LinearLayout(self);
						ll.setOrientation(LinearLayout.VERTICAL);
						ViewGroup.LayoutParams mmlp=new ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT);
						LinearLayout __ll=new LinearLayout(self);
						__ll.setOrientation(LinearLayout.VERTICAL);
						ViewGroup bounceScrollView=(ViewGroup)new_instance(load("com/tencent/mobileqq/widget/BounceScrollView"),self,null,Context.class,AttributeSet.class);
						//invoke_virtual(bounceScrollView,"a",true,500,500,boolean.class,int.class,int.class);
						bounceScrollView.setLayoutParams(mmlp);
						bounceScrollView.addView(ll,new ViewGroup.LayoutParams(MATCH_PARENT,WRAP_CONTENT));
						bounceScrollView.setBackgroundColor(QThemeKit.qq_setting_item_bg_nor.getDefaultColor());
						//invoke_virtual(bounceScrollView,"setNeedHorizontalGesture",true,boolean.class);
						LinearLayout.LayoutParams fixlp=new LinearLayout.LayoutParams(MATCH_PARENT,dip2px(self,48));
						RelativeLayout.LayoutParams __lp_l=new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
						int mar=(int)(dip2px(self,12)+0.5f);
						__lp_l.setMargins(mar,0,mar,0);
						__lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
						__lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
						RelativeLayout.LayoutParams __lp_r=new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
						__lp_r.setMargins(mar,0,mar,0);
						__lp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						__lp_r.addRule(RelativeLayout.CENTER_VERTICAL);
						ColorStateList hiColor=ColorStateList.valueOf(Color.argb(255,242,140,72));
						ll.addView(subtitle(self,"列表"));
						ll.addView(newListItemButton(self,"历史好友",null,null,clickToProxyActAction(ACTION_EXFRIEND_LIST)));
						ll.addView(newListItemButton(self,"导出历史好友列表","支持json/csv/yaml格式",null,clickTheComing()));
						ll.addView(subtitle(self,"设置"));
						ll.addView(newListItemSwitchConfigNext(self,"隐藏小程序入口","隐藏消息列表下拉出现的小程序列表","qn_hide_msg_list_miniapp",false));
						ll.addView(newListItemSwitchConfigNext(self,"隐藏分组下方入口","隐藏分组列表最下方的历史好友按钮","qn_hide_ex_entry_group",false));
						ll.addView(newListItemSwitchConfig(self,"主动删除好友时不通知","仅在测试模块时才可能需要关闭","qn_del_op_silence",true));
						ll.addView(newListItemButton(self,"重置模块","会丢失所有历史好友信息",null,clickTheComing()));
						ll.addView(subtitle(self,"消息通知设置(不影响接收消息)"));
						ll.addView(newListItemButton(self,"屏蔽指定群@全体成员通知",Html.fromHtml("<font color='"+get_RGB(hiColor.getDefaultColor())+"'>[@全体成员]</font>就这点破事"),"0个群",clickToProxyActAction(ACTION_SHUTUP_AT_ALL)));
						ll.addView(newListItemButton(self,"屏蔽指定群的红包通知",Html.fromHtml("<font color='"+get_RGB(hiColor.getDefaultColor())+"'>[QQ红包][有红包][有福袋]</font>恭喜发财"),"0个群",clickToProxyActAction(ACTION_SHUTUP_RED_PACKET)));
						ll.addView(subtitle(self,"实验性功能(不一定有效,使用者后果自负)"));
						ll.addView(newListItemSwitchConfigNext(self,"上传透明头像","开启后上传透明头像不会变黑","qn_enable_transparent",true));
						ll.addView(newListItemSwitchConfig(self,"发送卡片消息","xml,json等","qn_enable_card_msg",false));
						ll.addView(newListItemSwitchConfig(self,"语音转发",null,"qn_enable_voice_forward",false));
						ll.addView(newListItemSwitchConfig(self,"以图片方式打开表情",null,"qn_sticker_as_pic",false));
						ll.addView(newListItemButton(self,"重定向文件下载目录",new File(Environment.getExternalStorageDirectory(),"Tencent/QQfile_recv").getAbsolutePath(),"禁用",clickTheComing()));
						ll.addView(subtitle(self,"参数设定"));
						ll.addView(newListItemButton(self,"DelFriendReq.delType","只能为1或2","[不改动]",clickTheComing()));
						ll.addView(newListItemButton(self,"AddFriendReq.sourceID","改错可能导致无法添加好友","[不改动]",clickTheComing()));
						ll.addView(subtitle(self,"关于"));
						ll.addView(newListItemDummy(self,"QQ版本",null,Utils.getQQVersionName(self)));
						ll.addView(newListItemDummy(self,"模块版本",null,Utils.QN_VERSION_NAME));
						ll.addView(newListItemButton(self,"检查更新",null,"暂不开放",clickTheComing()));
						ll.addView(subtitle(self,"调试"));
						ll.addView(newListItemButton(self,"Shell.exec","正常情况下无需使用此功能",null,clickTheComing()));
						ll.addView(subtitle(self,"作者"));
						ll.addView(newListItemButton(self,"打赏","请选择扶贫方式",null,clickTheComing()));
						ll.addView(newListItemButton(self,"QQ","点击私信反馈","1041703712",clickToChat()));
						ll.addView(newListItemButton(self,"Mail",null,"xenonhydride@gmail.com",null));
						ll.addView(newListItemButton(self,"源代码","Bug -> Issue","https://github.com/cinit/QNotified",clickToUrl("https://github.com/cinit/QNotified")));
						ll.addView(newListItemButton(self,"Telegram",null,"Auride",clickToUrl("https://t.me/Auride")));
						ll.addView(subtitle(self,"SystemClassLoader\n"+ClassLoader.getSystemClassLoader()+"\nContext.getClassLoader()\n"+self.getClassLoader()+"\nThread.getContextClassLoader()\n"+Thread.currentThread().getContextClassLoader()));

						bounceScrollView.setFocusable(true);
						bounceScrollView.setFocusableInTouchMode(true);
						__ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT));
						self.setContentView(bounceScrollView);
						LinearLayout.LayoutParams _lp_fat= new LinearLayout.LayoutParams(MATCH_PARENT,0);
						_lp_fat.weight=1;
						//__ll.addView(bounceScrollView,_lp_fat);
						//sdlv.setBackgroundColor(0xFFAA0000)
						invoke_virtual(self,"setTitle","高级与设置",CharSequence.class);
						invoke_virtual(self,"setImmersiveStatus");
						invoke_virtual(self,"enableLeftBtn",true,boolean.class);
						//TextView rightBtn=(TextView)invoke_virtual(self,"getRightTextView");
						//log("Title:"+invoke_virtual(self,"getTextTitle"));

						//.addView(sdlv,lp);
						ConfigManager.get().save();
					}catch(Throwable e){
						log(e);
					}
				else if(action==ACTION_SHELL)
					try{

					}catch(Throwable e){
						log(e);
						throw e;
					}
				else if(action==ACTION_SHUTUP_AT_ALL||action==ACTION_SHUTUP_RED_PACKET)
					try{
						exlist_mFlingHandler=new_instance(load("com/tencent/mobileqq/activity/fling/FlingGestureHandler"),self,Activity.class);
						iput_object(self,"mFlingHandler",exlist_mFlingHandler);
						QThemeKit.initTheme(self);
						TroopSelectAdapter adapter=new TroopSelectAdapter(self,action);
						adapter.doOnPostCreate();
					}catch(Throwable e){
						log(e);
					}

			}catch(Throwable e){
				log(e);
				throw e;
			}
		}
	};

	/*
	 private XC_MethodHook proxyActivity_doOnCreate=new XC_MethodHook(200){
	 @Override
	 protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
	 if(ActProxyMgr.isInfiniteLoop())return;
	 Activity self=(Activity)param.thisObject;

	 int id=self.getIntent().getExtras().getInt(ACTIVITY_PROXY_ID_TAG,-1);
	 if(id<=0)return;
	 ActProxyMgr.set(id,self);
	 Method m=self.getClass().getSuperclass().getDeclaredMethod("doOnCreate",Bundle.class);
	 m.setAccessible(true);
	 try{
	 ActProxyMgr.invokeSuper(self,m,param.args);
	 }catch(ActProxyMgr.BreakUnaughtException e){}
	 param.setResult(true);
	 //log("***doOnCreate");
	 try{
	 QThemeKit.initTheme(self);
	 LinearLayout ll=
	 QQViewBuilder.initCustomCommenTitleL(self,"返回","历史好友","清空");
	 //TextView tv=new TextView(self);
	 //QThemeKit.ThemeStruct theme=QThemeKit.getCurrentTheme(splashActivity);
	 /*tv.setText("Hello,QQ!");
	 tv.setTextColor(theme.skin_text_black);
	 tv.setBackgroundColor(theme.qq_setting_item_bg_pre);*
	 //ll.setBackgroundColor(0);
	 ll.setBackgroundColor(QThemeKit.qq_setting_item_bg_nor.getDefaultColor());
	 //ll.setBackgroundTintMode(PorterDuff.Mode.DARKEN);
	 //tv.setBackgroundColor(0xFF000000);
	 //ll.addView(tv);
	 //tv.setBackgroundResource(0);
	 SlideDetectListView sdlv=(SlideDetectListView)QConst.load("com.tencent.mobileqq.widget.SlideDetectListView").getConstructor(Context.class,AttributeSet.class).newInstance(self,null);
	 invoke_virtual(sdlv,"setCanSlide",true,boolean.class);
	 ViewGroup.LayoutParams lp=new ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT);
	 //sdlv.setBackgroundColor(0xFFAA0000);
	 ll.addView(sdlv,lp);
	 //invoke_virtual(sdlv,"addHeaderView",tv,null,false,View.class,Object.class,boolean.class);
	 invoke_virtual(sdlv,"setDivider",null,Drawable.class);
	 QQViewBuilder.listView_setAdapter(sdlv,new ExfriendListAdapter(sdlv));

	 }catch(Throwable e){
	 log(e);
	 }
	 }
	 };*/

	private XC_MethodHook proxyActivity_doOnDestroy=new XC_MethodHook(200){
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
			try{
				if(ActProxyMgr.isInfiniteLoop())return;
				Activity self=(Activity)param.thisObject;
				int id=self.getIntent().getExtras().getInt(ACTIVITY_PROXY_ID_TAG,-1);
				if(id<=0)return;
				//ActProxyMgr.remove(id);
				Method m=self.getClass().getSuperclass().getSuperclass().getDeclaredMethod("doOnDestroy");
				m.setAccessible(true);
				try{
					ActProxyMgr.invokeSuper(self,m);
				}catch(ActProxyMgr.BreakUnaughtException e){}
				param.setResult(null);
				//log("***doOnDestroy");
			}catch(Throwable e){
				log(e);
				throw e;
			}
		}
	};

	private XC_MethodHook proxyActivity_doOnResume=new XC_MethodHook(200){
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
			try{
				if(ActProxyMgr.isInfiniteLoop())return;
				Activity self=(Activity)param.thisObject;
				int id=self.getIntent().getExtras().getInt(ACTIVITY_PROXY_ID_TAG,-1);
				if(id<=0)return;
				Method m=self.getClass().getSuperclass().getSuperclass().getDeclaredMethod("doOnResume");
				m.setAccessible(true);
				try{
					ActProxyMgr.invokeSuper(self,m);
				}catch(ActProxyMgr.BreakUnaughtException e){}
				param.setResult(null);
				//log("***doOnResume");
			}catch(Throwable e){
				log(e);
				throw e;
			}
		}
	};

	private XC_MethodHook proxyActivity_isWrapContent=new XC_MethodHook(200){
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
			try{
				if(ActProxyMgr.isInfiniteLoop())return;
				Activity self=(Activity)param.thisObject;
				int id=self.getIntent().getExtras().getInt(ACTIVITY_PROXY_ID_TAG,-1);
				if(id<=0)return;
				param.setResult(true);
				//log("***doOnResume");
			}catch(Throwable e){
				log(e);
				throw e;
			}
		}
	};

	private XC_MethodHook proxyActivity_doOnPause=new XC_MethodHook(200){
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
			try{
				if(ActProxyMgr.isInfiniteLoop())return;
				Activity self=(Activity)param.thisObject;
				int id=self.getIntent().getExtras().getInt(ACTIVITY_PROXY_ID_TAG,-1);
				if(id<=0)return;
				Method m=self.getClass().getSuperclass().getSuperclass().getDeclaredMethod("doOnPause");
				m.setAccessible(true);
				try{
					ActProxyMgr.invokeSuper(self,m);
				}catch(ActProxyMgr.BreakUnaughtException e){}
				param.setResult(null);
				//log("***doOnPause");
			}catch(Throwable e){
				log(e);
				throw e;
			}
		}
	};

	private XC_MethodHook proxyActivity_doOnActivityResult=new XC_MethodHook(200){
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
			try{
				if(ActProxyMgr.isInfiniteLoop())return;
				Activity self=(Activity)param.thisObject;
				int id=self.getIntent().getExtras().getInt(ACTIVITY_PROXY_ID_TAG,-1);
				if(id<=0)return;
				Method m=self.getClass().getSuperclass().getSuperclass().getDeclaredMethod("doOnActivityResult",int.class,int.class,Intent.class);
				m.setAccessible(true);
				try{
					ActProxyMgr.invokeSuper(self,m,param.args);
				}catch(ActProxyMgr.BreakUnaughtException e){}
				param.setResult(null);
				//log("***doOnActivityResult");
			}catch(Throwable e){
				log(e);
				throw e;
			}
		}
	};

	/*private XC_MethodHook pastEntry=new XC_MethodHook(1200){

	 @Override
	 protected void afterHookedMethod(MethodHookParam param) throws Throwable{
	 try{

	 */
	private XC_MethodHook exfriendEntryHook=new XC_MethodHook(1200){
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
			try{
				boolean hide=false;
				try{
					hide=ConfigManager.get().getBooleanOrFalse("qn_hide_ex_entry_group");
				}catch(Throwable e){log(e);}
				if(hide)return;
				if(!param.thisObject.getClass().getName().contains("ContactsFPSPinnedHeaderExpandableListView"))return;
				LinearLayout layout_entrance;
				android.widget.FrameLayout frameView;
				ContactsFPSPinnedHeaderExpandableListView lv=(ContactsFPSPinnedHeaderExpandableListView) param.thisObject;
				//frameView=Utils.getObject(,View.class,"b");
				final Activity splashActivity=(Activity)Utils.getContext(lv);
				QThemeKit.initTheme(splashActivity);
				//lv=(ContactsFPSPinnedHeaderExpandableListView) iget_object(param.thisObject,"a",load("com/tencent/mobileqq/activity/contacts/view/ContactsFPSPinnedHeaderExpandableListView"));
				//log("Fuckee:"+lv.getClass());
				TextView unusualContacts;
				/*if(frameView.getChildAt(0) instanceof LinearLayout){
				 if(frameView.getVisibility()==View.GONE){
				 /*兼容QQ净化->隐藏不常用联系人,上面的1200也是一样*
				 frameView.setVisibility(View.VISIBLE);
				 if(unusualContacts!=null)unusualContacts.setVisibility(View.GONE);
				 }
				 return;
				 }*/
				//unusualContacts=(TextView)frameView.getChildAt(0);

				layout_entrance=new LinearLayout(splashActivity);
				RelativeLayout rell=new RelativeLayout(splashActivity);
				//rell.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT));

				//Object adapter=invoke_virtual(lv,"getAdapter",ListAdapter.class);
				//invoke_virtual(lv,"setAdapter",null,BaseAdapter.class);
				/*try{
				 invoke_virtual(lv,"removeFooterView",layout,View.class);
				 }catch(Exception e){log(e);}
				 */
				if(!addedListView.contains(lv)){
					//log("fucking it!");
					invoke_virtual_original(lv,"addFooterView",layout_entrance,View.class);
					addedListView.add(lv);
					//invoke_static(XposedBridge.class,"dumpObjectNative",lv,Object.class);
					//lv.setVisibility(View.GONE);
				}

				//invoke_virtual(lv,"setAdapter",adapter,BaseAdapter.class);

				layout_entrance.setOrientation(LinearLayout.VERTICAL);

				//StateListDrawable background=(StateListDrawable)unusualContacts.getBackground();

				exfriend=new TextView(splashActivity);
				exfriend.setTextColor(QThemeKit.skin_blue);//unusualContacts.getTextColors());//QThemeKit.skin_red);
				//exfriend.setBackground(Utils._obj_clone(background.mutate()));//damn! mutate() not working!
				exfriend.setTextSize(dip2sp(splashActivity,17));//TypedValue.COMPLEX_UNIT_PX,unusualContacts.getTextSize());
				exfriend.setId(VIEW_ID_DELETED_FRIEND);
				exfriend.setText("历史好友");
				exfriend.setGravity(Gravity.CENTER);
				exfriend.setClickable(true);
				//exfriend.setTranslationY(-Utils.dip2px(splashActivity,1f));
				//unusualContacts.setVisibility(frameView.getVisibility()==View.GONE?View.GONE:View.VISIBLE);
				//frameView.setVisibility(View.VISIBLE);

				TextView redDot=new TextView(splashActivity);
				redDotRef=new WeakReference(redDot);
				redDot.setTextColor(0xFFFF0000);

				redDot.setGravity(Gravity.CENTER);
				//redDot.setBackground(QThemeKit.skin_tips_newmessage);
				redDot.getPaint().setFakeBoldText(true);
				//redDot.setTextAppearance(android.R.style.TextAppearance_Small);
				redDot.setTextSize(Utils.dip2sp(splashActivity,10));
				//redDot.setPadding(4,0,4,0);
				try{
					invoke_static(load("com/tencent/widget/CustomWidgetUtil"),"a",redDot,3,1,0,TextView.class,int.class,int.class,int.class,void.class);
				}catch(NullPointerException e){
					redDot.setTextColor(Color.RED);
				}
				ExfriendManager.get(Utils.getLongAccountUin()).setRedDot();


				//frameView.removeAllViews();
				int height=dip2px(splashActivity,48);//unusualContacts.getLayoutParams().height;
				//layout.addView(unusualContacts);
				RelativeLayout.LayoutParams exlp=new RelativeLayout.LayoutParams(MATCH_PARENT,height);
				exlp.topMargin=0;
				exlp.leftMargin=0;

				rell.addView(exfriend,exlp);
				RelativeLayout.LayoutParams dotlp=new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
				dotlp.topMargin=0;
				dotlp.rightMargin=Utils.dip2px(splashActivity,24);
				dotlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				dotlp.addRule(RelativeLayout.CENTER_VERTICAL);
				rell.addView(redDot,dotlp);
				layout_entrance.addView(rell);//,unusualContacts.getLayoutParams());
				ViewGroup.LayoutParams llp=new ViewGroup.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
				layout_entrance.setPadding(0,(int)(height*0.3f),0,(int)(0.3f*height));
				/*frameView.addView(layout,llp);
				 ViewGroup.LayoutParams _lp=frameView.getLayoutParams();
				 _lp.height=WRAP_CONTENT;//(int)(unusual.getLayoutParams().height*());
				 final View.OnClickListener olds=Utils.getOnClickListener(frameView);
				 frameView.setOnTouchListener(null);
				 frameView.setClickable(false);
				 //unusualContacts_old.setOnTouchListener(null);*/
				exfriend.setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View v){
							Intent intent=new Intent(splashActivity,load(ActProxyMgr.STUB_ACTIVITY));
							int id=ActProxyMgr.next();
							intent.putExtra(ACTIVITY_PROXY_ID_TAG,id);
							intent.putExtra(ACTIVITY_PROXY_ACTION,ACTION_EXFRIEND_LIST);
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
			}catch(Throwable e){
				log(e);
				throw e;
			}
		}

	};


}
