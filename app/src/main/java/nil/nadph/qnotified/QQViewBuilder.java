package nil.nadph.qnotified;

import android.content.*;
import android.view.*;
import android.widget.*;
import android.app.*;
import java.lang.reflect.*;

import static nil.nadph.qnotified.Utils.log;
import static nil.nadph.qnotified.Utils.invoke_static;
import static nil.nadph.qnotified.Utils.invoke_virtual;
import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.QConst.load;

public class QQViewBuilder{
	/*
	//L stands for LinearLayout
	public static LinearLayout initCustomCommenTitleL(Activity ctx,String back_text,String title,String btn_text) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

		RelativeLayout root=new RelativeLayout(ctx);
		root.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT));

		LinearLayout ll1=new LinearLayout(ctx);
		ll1.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT));
		ll1.setOrientation(1);
		root.addView(ll1);
		//root.setBackgroundResource(0x7F02202D);
		//Avoid using R.id.custom_commen_title,junk!
		LayoutInflater.from(ctx).inflate(QConst.getId("custom_commen_title"),ll1);
		RelativeLayout title_rl=(RelativeLayout)ll1.getChildAt(0);
		ctx.setContentView(root);

		ctx.getWindow().setBackgroundDrawable(null);//I don't know why to set it null...

		int statusBarHeight=invoke_static(load("com/tencent/widget/immersive/ImmersiveUtils"),"a",ctx,Context.class,int.class);
		root.setPadding(0,statusBarHeight,0,0);
		
		
		 TextView title_tv=(TextView)title_rl.getChildAt(0);
		 title_tv.setText(title);

		 TextView backBtn=(TextView)title_rl.getChildAt(2);
		 backBtn.setText(back_text);
		backBtn.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					((Activity)v.getContext()).onBackPressed();
				}
			});
		 ViewGroup rl_tmp;
		 if(btn_text!=null&&!"".equals(btn_text)){
			 rl_tmp=(ViewGroup)title_rl.getChildAt(title_rl.getChildCount()-1);
			 rl_tmp=(ViewGroup)rl_tmp.getChildAt(rl_tmp.getChildCount()-1);
			 TextView rightBtn=(TextView)rl_tmp.getChildAt(0);
			 rightBtn.setText(btn_text);
			 rightBtn.setVisibility(View.VISIBLE);
		}
		LinearLayout ll2=new LinearLayout(ctx);
		ll2.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT));
		ll2.setOrientation(1);
		ll1.addView(ll2);
		 
		
		 
		 return ll2;
		 
	}*/
	
	
	@SuppressWarnings("unchecked")
	public static void listView_setAdapter(View v,ListAdapter adapter){
		try{
			Class clazz=v.getClass();
			clazz.getMethod("setAdapter",ListAdapter.class).invoke(v,adapter);
		}catch(Exception e){
			Utils.log("tencent_ListView->setAdapter: "+e.toString());
		}
	}
	
	public static View switch_new(Context ctx){
		try{
			Class clazz=load("com/tencent/widget/Switch");
			return (View)clazz.getConstructor(Context.class).newInstance(ctx);
		}catch(Exception e){
			Utils.log("Switch->new: "+e.toString());
		}
		return null;
	}

	public static boolean switch_isChecked(View v){
		try{
			Class clazz=load("com/tencent/widget/Switch");
			return clazz.getMethod("isChecked").invoke(v);
		}catch(Exception e){
			Utils.log("Switch->isChecked: "+e.toString());
		}
		return false;
	}
	@SuppressWarnings("unchecked")
	public static void switch_setChecked(View v,boolean checked){
		try{
			Class clazz=load("com/tencent/widget/Switch");
			clazz.getMethod("setChecked",boolean.class).invoke(v,checked);
		}catch(Exception e){
			Utils.log("Switch->setChecked: "+e.toString());
		}
	}
}
