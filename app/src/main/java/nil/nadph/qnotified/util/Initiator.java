package nil.nadph.qnotified.util;

import android.content.Context;
import android.widget.PopupWindow;


public class Initiator {

    private Context mContext;
    private static PopupWindow window;
    private float progress;

    public static void init(ClassLoader classLoader) {
        qqClassLoader = classLoader;
    }


    private static ClassLoader qqClassLoader;

	/*public static void patchStub(Context ctx){
		ClassLoader ld=ctx.getClassLoader();
		DexClassLoader dcl=new DexClassLoader("/sdcard/AppProjects/QNotified/classes2.dex",ctx.getDir("dex",0).getAbsolutePath(),null,qqClassLoader);
		qqClassLoader=dcl;
	}*/

	public static ClassLoader getClassLoader(){
		return qqClassLoader;
	}
	
    public static Class<?> load(String className) {
        if (qqClassLoader == null || className.isEmpty()) {
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
            if (!className.contains("com.tencent.mobileqq.R$")) {
                Utils.log(String.format("Can't find the Class of name: %s!", className));
            }
            return null;
        }
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
