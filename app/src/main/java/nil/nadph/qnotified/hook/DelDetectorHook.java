package nil.nadph.qnotified.hook;


import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static nil.nadph.qnotified.ActProxyMgr.*;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;
import de.robv.android.xposed.*;
import nil.nadph.qnotified.pk.*;
import nil.nadph.qnotified.*;
import nil.nadph.qnotified.util.*;
import nil.nadph.qnotified.record.*;
import android.widget.*;
import android.app.*;
import android.view.*;
import java.lang.ref.*;
import android.graphics.*;
import java.util.*;
import android.content.*;

public class DelDetectorHook extends BaseDelayableHook{
	
	public HashSet addedListView = new HashSet();
   
	public static final int VIEW_ID_DELETED_FRIEND = 0x00EE77AA;
	
    TextView exfriend;
    public WeakReference<TextView> redDotRef;

	private DelDetectorHook() {
    }

    private static final DelDetectorHook self = new DelDetectorHook();

    public static DelDetectorHook get() {
        return self;
    }

    private boolean inited = false;

    @Override
    public boolean init() {
        if (inited) return true;
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
		inited = true;
		return true;
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

	@Override
	public int getEffectiveProc() {
		return SyncUtils.PROC_MAIN;
	}

    @Override
    public boolean checkPreconditions() {
        return true;
    }

    @Override
    public int[] getPreconditions() {
        return new int[]{};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
