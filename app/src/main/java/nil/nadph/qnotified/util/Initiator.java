package nil.nadph.qnotified.util;

@SuppressWarnings("rawtypes")
public class Initiator {

/*    private Context mContext;
    private static PopupWindow window;
    private float progress;*/

    private static ClassLoader qqClassLoader;

    public static void init(ClassLoader classLoader) {
        qqClassLoader = classLoader;
    }

    public static ClassLoader getClassLoader() {
        return qqClassLoader;
    }

    @Nullable
    public static Class<?> load(String className) {
        if (qqClassLoader == null || className == null || className.isEmpty()) {
            return null;
        }
        className = className.replace('/', '.');
        if (className.endsWith(";")) className = className.substring(0, className.length() - 1);
        if (className.charAt(0) == 'L' && className.charAt(1) >= 'a') className = className.substring(1);
        if (className.startsWith(".")) {
            className = Utils.PACKAGE_NAME_QQ + className;
        }
        try {
            return qqClassLoader.loadClass(className);
        } catch (Throwable e) {
            return null;
        }
    }

    public static Class _PicItemBuilder() {
        Class tmp;
        Class mPicItemBuilder = load("com.tencent.mobileqq.activity.aio.item.PicItemBuilder");
        if (mPicItemBuilder == null) {
            try {
                tmp = load("com.tencent.mobileqq.activity.aio.item.PicItemBuilder$7");
                mPicItemBuilder = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        if (mPicItemBuilder == null) {
            try {
                tmp = load("com.tencent.mobileqq.activity.aio.item.PicItemBuilder$6");
                mPicItemBuilder = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        if (mPicItemBuilder == null) {
            try {
                tmp = load("com.tencent.mobileqq.activity.aio.item.PicItemBuilder$8");
                mPicItemBuilder = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        return mPicItemBuilder;
    }

    public static Class _TextItemBuilder() {
        Class tmp;
        Class mTextItemBuilder = load("com/tencent/mobileqq/activity/aio/item/TextItemBuilder");
        if (mTextItemBuilder == null) {
            try {
                tmp = load("com/tencent/mobileqq/activity/aio/item/TextItemBuilder$10");
                mTextItemBuilder = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        if (mTextItemBuilder == null) {
            try {
                tmp = load("com/tencent/mobileqq/activity/aio/item/TextItemBuilder$6");
                mTextItemBuilder = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        return mTextItemBuilder;
    }

    public static Class _UpgradeController() {
        Class tmp;
        Class clazz = load("com.tencent.mobileqq.app.upgrade.UpgradeController");
        if (clazz == null) {
            try {
                tmp = load("com.tencent.mobileqq.app.upgrade.UpgradeController$1");
                clazz = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        if (clazz == null) {
            try {
                tmp = load("com.tencent.mobileqq.app.upgrade.UpgradeController$2");
                clazz = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        return clazz;
    }

    public static Class _BannerManager() {
        Class tmp;
        Class clazz = load("com.tencent.mobileqq.activity.recent.BannerManager");
        for (int i = 38; clazz == null && i < 42; i++) {
            try {
                tmp = load("com.tencent.mobileqq.activity.recent.BannerManager$" + i);
                clazz = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        return clazz;
    }

    public static Class _PttItemBuilder() {
        Class cl_PttItemBuilder = load("com/tencent/mobileqq/activity/aio/item/PttItemBuilder");
        if (cl_PttItemBuilder == null) {
            Class cref = load("com/tencent/mobileqq/activity/aio/item/PttItemBuilder$2");
            try {
                cl_PttItemBuilder = cref.getDeclaredField("this$0").getType();
            } catch (NoSuchFieldException ignored) {
            }
        }
        return cl_PttItemBuilder;
    }

    public static Class _TroopGiftAnimationController() {
        Class cl_TroopGiftAnimationController = load("com.tencent.mobileqq.troopgift.TroopGiftAnimationController");
        if (cl_TroopGiftAnimationController == null) {
            Class cref = load("com.tencent.mobileqq.troopgift.TroopGiftAnimationController$1");
            try {
                cl_TroopGiftAnimationController = cref.getDeclaredField("this$0").getType();
            } catch (NoSuchFieldException ignored) {
            }
        }
        return cl_TroopGiftAnimationController;
    }

    public static Class _FavEmoRoamingHandler() {
        Class clz = load("com/tencent/mobileqq/app/FavEmoRoamingHandler");
        if (clz == null) {
            Class cref = load("com/tencent/mobileqq/app/FavEmoRoamingHandler$1");
            try {
                clz = cref.getDeclaredField("this$0").getType();
            } catch (NoSuchFieldException ignored) {
            }
        }
        return clz;
    }

    public static Class _QQMessageFacade() {
        return load("com/tencent/mobileqq/app/message/QQMessageFacade");
    }

    public static Class _SessionInfo() {
        return load("com/tencent/mobileqq/activity/aio/SessionInfo");
    }

    public static Class _MessageRecord() {
        return load("com/tencent/mobileqq/data/MessageRecord");
    }

    @Nullable
    public static Class _EmoAddedAuthCallback() {
        try {
            Class clz = load("com/tencent/mobileqq/emosm/favroaming/EmoAddedAuthCallback");
            if (clz == null) {
                Class cref = load("com/tencent/mobileqq/emosm/favroaming/EmoAddedAuthCallback$2");
                try {
                    clz = cref.getDeclaredField("this$0").getType();
                } catch (NoSuchFieldException ignored) {
                }
            }
            if (clz == null) {
                Class cref = load("com/tencent/mobileqq/emosm/favroaming/EmoAddedAuthCallback$1");
                try {
                    clz = cref.getDeclaredField("this$0").getType();
                } catch (NoSuchFieldException ignored) {
                }
            }
            return clz;
        } catch (NullPointerException e) {
            return null;
        }
    }


    @Nullable
    public static Class _C2CMessageProcessor() {
        Class clz = load("com/tencent/mobileqq/app/message/C2CMessageProcessor");
        if (clz == null) {
            Class cref = load("com/tencent/mobileqq/app/message/C2CMessageProcessor$1");
            try {
                clz = cref.getDeclaredField("this$0").getType();
            } catch (NoSuchFieldException ignored) {
            }
        }
        if (clz == null) {
            Class cref = load("com/tencent/mobileqq/app/message/C2CMessageProcessor$5");
            try {
                clz = cref.getDeclaredField("this$0").getType();
            } catch (NoSuchFieldException ignored) {
            }
        }
        if (clz == null) {
            Class cref = load("com/tencent/mobileqq/app/message/C2CMessageProcessor$7");
            try {
                clz = cref.getDeclaredField("this$0").getType();
            } catch (NoSuchFieldException ignored) {
            }
        }
        return clz;
    }
	

	/*
	public static void showPopup(View root){
		if(mContext==null)
			mContext=Utils.getContext(root);
		if(window==null)window=new PopupWindow();
		window.setOutsideTouchable(false);
		int width=(int)(0.5f+Utils.dip2px(mContext,2.0f));
		SimpleBgDrawable bg_up=new SimpleBgDrawable(0x10000000,0xFFFFFFFF,width);
		SimpleBgDrawable bg_down=new SimpleBgDrawable(0xC0AAAAAA,0xFFFFFFFF,width);
		StateListDrawable bg=new StateListDrawable();
		bg.addState(new int[]{android.R.attr.state_pressed},bg_down);
		bg.addState(new int[]{android.R.attr.state_checked},bg_down);
		bg.addState(new int[]{},bg_up);
		window.setTouchable(true);
		window.setBackgroundDrawable(null);
		window.setWidth(WRAP_CONTENT);
		window.setHeight(WRAP_CONTENT);
		LinearLayout vroot=new LinearLayout(mContext);
		vroot.setBackgroundColor(0xC0000000);
		ViewGroup.LayoutParams lp_ww=new ViewGroup.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
		vroot.setLayoutParams(lp_ww);
		vroot.setPadding(20,20,20,20);
		vroot.setOrientation(LinearLayout.VERTICAL);
		window.setContentView(vroot);
		
		LinearLayout.LayoutParams llp_ww=new LinearLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
		llp_ww.setMargins(10,10,10,10);
		
		LinearLayout.LayoutParams llp_mw=new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
		llp_mw.setMargins(20,20,20,20);
		
		
		TextView tv=new TextView(mContext);
		tv.setTextSize(20);
		tv.setText("请稍候...");
		tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.LEFT);
		tv.setTextColor(0xFFFFFFFF);
		vroot.addView(tv,llp_ww);
		
		tv=new TextView(mContext);
		tv.setTextSize(15);
		tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.LEFT);
		tv.setText("QNotified正在初始化, 这可能会花费几分钟时间");
		tv.setTextColor(0xFFFFFFFF);
		vroot.addView(tv,llp_ww);
		
		tv=new TextView(mContext);
		tv.setTextSize(12);
		tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.LEFT);
		tv.setText("当第一次激活QNotified,QQ升级或QQ数据被清除时,QNotified会进行初始化");
		tv.setTextColor(0xFFFFFFFF);
		vroot.addView(tv,llp_ww);
		
		LinearLayout _ll=new LinearLayout(mContext);
		_ll.setBackgroundDrawable(new SimpleBgDrawable(0,0xFFFFFFFF,(int)(0.5f+Utils.dip2px(mContext,1.0f))));
		LinearLayout.LayoutParams _tmp_lllp=new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
		_tmp_lllp.setMargins(20,20,20,0);
		vroot.addView(_ll,_tmp_lllp);
		
		final View _v=new View(mContext);
		final ProportionDrawable pd=new ProportionDrawable(0xFFFFFFFF,0,Gravity.LEFT,0f);
		_v.setBackgroundDrawable(pd);
		_tmp_lllp=new LinearLayout.LayoutParams(MATCH_PARENT,20);
		_tmp_lllp.setMargins(10,10,10,10);
		_ll.addView(_v,_tmp_lllp);
		
		RelativeLayout _rl=new RelativeLayout(mContext);
		_tmp_lllp=new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
		_tmp_lllp.setMargins(20,0,20,0);
		vroot.addView(_rl,_tmp_lllp);
		
		final TextView tv_l=new TextView(mContext);
		tv_l.setTextSize(13);
		tv_l.setGravity(Gravity.LEFT);
		tv_l.setText("0/NaN");
		tv_l.setTextColor(0xFFFFFFFF);
		RelativeLayout.LayoutParams _rllp=new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
		_rllp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		_rl.addView(tv_l,_rllp);
		
		final TextView tv_r=new TextView(mContext);
		tv_r.setTextSize(13);
		tv_r.setGravity(Gravity.RIGHT);
		tv_r.setText("0%");
		tv_r.setTextColor(0xFFFFFFFF);
		_rllp=new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
		_rllp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		_rl.addView(tv_r,_rllp);
		
		tv=new TextView(mContext);
		tv.setBackgroundDrawable(bg);
		tv.setTextSize(20);
		tv.setGravity(Gravity.CENTER);
		tv.setText("隐藏");
		tv.setTextColor(0xFFFFFFFF);
		tv.setClickable(true);
		tv.setPadding(20,10,20,10);
		tv.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					window.dismiss();
				}
			});
		vroot.addView(tv,llp_mw);
		
		window.showAtLocation(root,Gravity.CENTER,0,0);
		
		new Thread(new Runnable(){
			int s1,s2,a,i;
				@Override
				public void run(){
					a=(int)(10000f*Math.random());
					for(i=0;i<a;){
						s1=(int)(30d+70d*Math.random());
						s2=(int)(10d+100d*Math.random());
						try{
							Thread.sleep(s1);
						}catch(InterruptedException e){}
						i+=s2;
						if(i>a)i=a;
						((Activity)mContext).runOnUiThread(new Runnable(){
								@Override
								public void run(){
									tv_l.setText(i+"/"+a);
									tv_r.setText(Math.round(((float)i)/((float)a)*1000f)/10f+"%");
									pd.setProportion(((float)i)/((float)a));
									_v.invalidate();
								}
							});
					}
				}
			}).start();
	}*/

}
