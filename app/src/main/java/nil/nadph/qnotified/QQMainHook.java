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
import static nil.nadph.qnotified.QConst.load;





public class QQMainHook <SlideDetectListView extends View,ContactsFPSPinnedHeaderExpandableListView extends View> implements IXposedHookLoadPackage{

	public static final int VIEW_ID_DELETED_FRIEND=0x00EE77AA;
	public static final String ACTIVITY_PROXY_ID_TAG="qn_act_proxy_id";
	public static final String QN_FULL_TAG="qn_full_tag";
	public HashSet addedListView=new HashSet();


	XC_LoadPackage.LoadPackageParam lpparam;

	public Activity splashActivity;

	
	TextView exfriend;
	public static WeakReference<TextView> redDotRef;

	XC_MethodHook.Unhook[] unhook=new XC_MethodHook.Unhook[3];

	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam _lpparam) throws Throwable{
		this.lpparam=_lpparam;
		findAndHookMethod("com.tencent.common.app.QFixApplicationImpl",lpparam.classLoader,"isAndroidNPatchEnable",XC_MethodReplacement.returnConstant(500,false));
		XposedHelpers.findAndHookMethod("com.tencent.mobileqq.app.InjectUtils",lpparam.classLoader,"injectExtraDexes",Application.class,boolean.class,new XC_MethodHook(51) {
                protected void afterHookedMethod(MethodHookParam param) throws Throwable{
                    performHook(((Context) param.args[0]).getClassLoader());
                }
            });
	}


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
				startExfriendActivity();
			}
		}
	}



	private void performHook(ClassLoader classLoader){
		if(Utils.DEBUG){
			if("true".equals(System.getProperty(QN_FULL_TAG))){
				XposedBridge.log("Err:Qnotified reloaded??");
				System.exit(-1);
				//QNotified updated(in HookLoader mode),kill QQ to make user restart it.
			}
			System.setProperty(QN_FULL_TAG,"true");
		}
		QConst.init(classLoader);
		//log("Clases init done");
		if(classLoader==null)log("ERROR:classLoader==null");
		/*try{
		 Thread.sleep(5000);
		 }catch(InterruptedException e){}*/
		findAndHookMethod(load("com.tencent.mobileqq.activity.SplashActivity"),"doOnCreate",Bundle.class,new XC_MethodHook(200){
				@Override
				protected void afterHookedMethod(MethodHookParam param){
					splashActivity=(Activity)param.thisObject;
					new Thread(new SearchEntrance()).start();
				}
			});

		/*findAndHookMethod(load("com.tencent.mobileqq.data.MessageForQQWalletMsg"),"doParse",new XC_MethodHook(200){
		 @Override
		 protected void afterHookedMethod(MethodHookParam param){
		 XposedHelpers.setObjectField(param.thisObject,"isread",true);
		 }
		 });*/



		/*XposedBridge.hookAllMethods(load("com/tencent/mobileqq/util/FaceDecoder"),"a",
		 });*/



		Class clazz=load(".activity.contacts.fragment.FriendFragment");//".activity.Contacts");
		findAndHookMethod(clazz,"i",pastEntry);
		findAndHookMethod(clazz,"j",pastEntry);
		clazz=load(ActProxyMgr.DUMMY_ACTIVITY);
		findAndHookMethod(clazz,"onCreate",Bundle.class,proxyActivity_onCreate);
		//findAndHookMethod(clazz,"doOnCreate",Bundle.class,proxyActivity_doOnCreate);
		findAndHookMethodIfExists(clazz,"doOnDestroy",proxyActivity_doOnDestroy);
		findAndHookMethodIfExists(clazz,"onActivityResult",int.class,int.class,Intent.class,proxyActivity_doOnActivityResult);
		findAndHookMethodIfExists(clazz,"doOnPause",proxyActivity_doOnPause);
		findAndHookMethodIfExists(clazz,"doOnResume",proxyActivity_doOnResume);
		findAndHookMethodIfExists(clazz,"isWrapContent",proxyActivity_isWrapContent);
		XposedHelpers.findAndHookMethod(load("com/tencent/mobileqq/activity/SplashActivity"),"doOnResume",new XC_MethodHook(700){
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable{
					if(Utils.getLongAccountUin()>1000){
						ExfriendManager ex=ExfriendManager.getCurrent();
						ex.timeToUpdateFl();
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
		 XposedBridge.log(e);
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
					FriendChunk fc=new FriendChunk(param.thisObject);
					ExfriendManager.onGetFriendListResp(fc);
					/*String ret="dump object:"+param.thisObject.getClass().getCanonicalName()+"\n";
					 Field[] fs=param.thisObject.getClass().getDeclaredFields();
					 for(int i=0;i<fs.length;i++){
					 fs[i].setAccessible(true);
					 ret+=(i<fs.length-1?"├":"└")+fs[i].getName()+"="+ClazzExplorer.en_toStr(fs[i].get(param.thisObject))+"\n";
					 }
					 XposedBridge.log(ret);*/
				}
			});

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
		}catch(Exception e){
			log(e.toString());
			return null;
		}
	}
	public void startExfriendActivity(){
		Intent intent=new Intent(splashActivity,load(ActProxyMgr.DUMMY_ACTIVITY));
		int id=ActProxyMgr.next();
		intent.putExtra(ACTIVITY_PROXY_ID_TAG,id);
		intent.putExtra("fling_action_key",2);
		intent.putExtra("fling_code_key", hashCode());
		splashActivity.startActivity(intent);
	}

	public static void openProfileCard(Context ctx,long uin){
		try{
			Parcelable allInOne=(Parcelable) new_instance(load("com/tencent/mobileqq/activity/ProfileActivity$AllInOne"),""+uin,35,String.class,int.class);
			Intent intent=new Intent(ctx,load("com/tencent/mobileqq/activity/FriendProfileCardActivity"));
			intent.putExtra("AllInOne",allInOne);
            ctx.startActivity(intent);
		}catch(Exception e){
			XposedBridge.log(e);
		}
	}


	public Object exlist_mFlingHandler=null;
	private XC_MethodHook proxyActivity_onCreate=new XC_MethodHook(200){
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
			if(ActProxyMgr.isInfiniteLoop())return;
			final Activity self=(Activity)param.thisObject;
			int id=self.getIntent().getExtras().getInt(ACTIVITY_PROXY_ID_TAG,-1);
			if(id<=0)return;
			param.setResult(null);
			//ActProxyMgr.set(id,self);
			Method m=load("mqq/app/AppActivity").getDeclaredMethod("onCreate",Bundle.class);
			m.setAccessible(true);
			try{
				ActProxyMgr.invokeSuper(self,m,param.args);
			}catch(ActProxyMgr.BreakUnaughtException e){}
			log("***onCreate");
			try{
				
				
				exlist_mFlingHandler=new_instance(load("com/tencent/mobileqq/activity/fling/FlingGestureHandler"),self,Activity.class);
				iput_object(self,"mFlingHandler",exlist_mFlingHandler);
				QThemeKit.initTheme(self);
				
				SlideDetectListView sdlv=(SlideDetectListView)QConst.load("com.tencent.mobileqq.widget.SlideDetectListView").getConstructor(Context.class,AttributeSet.class).newInstance(self,null);
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
				log("Title:"+invoke_virtual(self,"getTextTitle"));
				rightBtn.setVisibility(View.VISIBLE);
				rightBtn.setText("高级");
				rightBtn.setEnabled(true);
				rightBtn.setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View v){
							try{
								//self.onBackPressed();
								//ExfriendManager.getCurrent().doRequestFlRefresh();
								Utils.showToastShort(v.getContext(),"即将开放(没啥好设置的)...");
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
								log(e.toString());
							}
						}
					});
				//.addView(sdlv,lp);

				
				
				invoke_virtual(sdlv,"setCanSlide",true,boolean.class);
				invoke_virtual(sdlv,"setDivider",null,Drawable.class);
				long uin=Utils.getLongAccountUin();
				ExfriendManager exm=ExfriendManager.get(uin);
				exm.clearUnreadFlag();
				tv.setText("最后更新: "+Utils.getRelTimeStrSec(exm.lastUpdateTimeSec));
				QQViewBuilder.listView_setAdapter(sdlv,new ExfriendListAdapter(sdlv,exm));
				//invoke_virtual(sdlv,"setOnScrollGroupFloatingListener",true,load("com/tencent/widget/AbsListView$OnScrollListener"));
			}catch(Throwable e){
				XposedBridge.log(e);
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
	 XposedBridge.log(e);
	 }
	 }
	 };*/

	private XC_MethodHook proxyActivity_doOnDestroy=new XC_MethodHook(200){
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
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
			log("***doOnDestroy");
		}
	};

	private XC_MethodHook proxyActivity_doOnResume=new XC_MethodHook(200){
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
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
		}
	};

	private XC_MethodHook proxyActivity_isWrapContent=new XC_MethodHook(200){
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
			if(ActProxyMgr.isInfiniteLoop())return;
			Activity self=(Activity)param.thisObject;
			int id=self.getIntent().getExtras().getInt(ACTIVITY_PROXY_ID_TAG,-1);
			if(id<=0)return;
			param.setResult(true);
			//log("***doOnResume");
		}
	};
	
	private XC_MethodHook proxyActivity_doOnPause=new XC_MethodHook(200){
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
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
		}
	};

	private XC_MethodHook proxyActivity_doOnActivityResult=new XC_MethodHook(200){
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
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
		}
	};

	private XC_MethodHook pastEntry=new XC_MethodHook(1200){
		@Override
		protected void afterHookedMethod(MethodHookParam param) throws Throwable{

			final android.widget.FrameLayout frameView=Utils.getObject(param.thisObject,View.class,"b");
			splashActivity=(Activity)frameView.getContext();
			QThemeKit.initTheme(splashActivity);
			ContactsFPSPinnedHeaderExpandableListView lv=(ContactsFPSPinnedHeaderExpandableListView) iget_object(param.thisObject,"a",load("com/tencent/mobileqq/activity/contacts/view/ContactsFPSPinnedHeaderExpandableListView"));
			TextView unusualContacts;
			/*if(frameView.getChildAt(0) instanceof LinearLayout){
			 if(frameView.getVisibility()==View.GONE){
			 /*兼容QQ净化->隐藏不常用联系人,上面的1200也是一样*
			 frameView.setVisibility(View.VISIBLE);
			 if(unusualContacts!=null)unusualContacts.setVisibility(View.GONE);
			 }
			 return;
			 }*/
			unusualContacts=(TextView)frameView.getChildAt(0);
			LinearLayout layout=new LinearLayout(splashActivity);
			RelativeLayout rell=new RelativeLayout(splashActivity);
			//rell.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT));

			//Object adapter=invoke_virtual(lv,"getAdapter",ListAdapter.class);
			//invoke_virtual(lv,"setAdapter",null,Adapter.class);
			/*try{
			 invoke_virtual(lv,"removeFooterView",layout,View.class);
			 }catch(Exception e){XposedBridge.log(e);}
			 */
			if(!addedListView.contains(lv)){
				invoke_virtual(lv,"addFooterView",layout,View.class);
				addedListView.add(lv);
			}


			//invoke_virtual(lv,"setAdapter",adapter,ExpandableListAdapter.class);

			layout.setOrientation(LinearLayout.VERTICAL);

			StateListDrawable background=(StateListDrawable)unusualContacts.getBackground();

			exfriend=new TextView(splashActivity);
			exfriend.setTextColor(unusualContacts.getTextColors());//QThemeKit.skin_red);
			exfriend.setBackground(Utils._obj_clone(background.mutate()));//damn! mutate() not working!
			exfriend.setTextSize(TypedValue.COMPLEX_UNIT_PX,unusualContacts.getTextSize());
			exfriend.setId(VIEW_ID_DELETED_FRIEND);
			exfriend.setText("历史好友");
			exfriend.setGravity(Gravity.CENTER);
			exfriend.setClickable(true);
			//exfriend.setTranslationY(-Utils.dip2px(splashActivity,1f));
			//unusualContacts.setVisibility(frameView.getVisibility()==View.GONE?View.GONE:View.VISIBLE);
			//frameView.setVisibility(View.VISIBLE);

			TextView redDot=new TextView(splashActivity);
			redDotRef=new WeakReference(redDot);
			redDot.setTextColor(0xFFFFFFFF);

			redDot.setGravity(Gravity.CENTER);
			//redDot.setBackground(QThemeKit.skin_tips_newmessage);
			redDot.getPaint().setFakeBoldText(true);
			//redDot.setTextAppearance(android.R.style.TextAppearance_Small);
			redDot.setTextSize(Utils.dip2sp(splashActivity,10));
			//redDot.setPadding(4,0,4,0);
			invoke_static(load("com/tencent/widget/CustomWidgetUtil"),"a",redDot,3,1,0,TextView.class,int.class,int.class,int.class,void.class);
			ExfriendManager.get(Utils.getLongAccountUin()).setRedDot();


			//frameView.removeAllViews();
			int height=unusualContacts.getLayoutParams().height;
			//layout.addView(unusualContacts);
			RelativeLayout.LayoutParams exlp=new RelativeLayout.LayoutParams(MATCH_PARENT,unusualContacts.getLayoutParams().height);
			exlp.topMargin=0;
			exlp.leftMargin=0;

			rell.addView(exfriend,exlp);
			RelativeLayout.LayoutParams dotlp=new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
			dotlp.topMargin=0;
			dotlp.rightMargin=Utils.dip2px(splashActivity,24);
			dotlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			dotlp.addRule(RelativeLayout.CENTER_VERTICAL);
			rell.addView(redDot,dotlp);
			layout.addView(rell,unusualContacts.getLayoutParams());
			ViewGroup.LayoutParams llp=new ViewGroup.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
			layout.setPadding(0,(int)(height*0.3f),0,(int)(0.3f*height));
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
						Intent intent=new Intent(splashActivity,load(ActProxyMgr.DUMMY_ACTIVITY));
						int id=ActProxyMgr.next();
						intent.putExtra(ACTIVITY_PROXY_ID_TAG,id);
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



		}
	};


}
