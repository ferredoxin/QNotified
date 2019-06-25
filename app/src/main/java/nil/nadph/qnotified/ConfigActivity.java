package nil.nadph.qnotified;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.text.*;
import android.text.method.*;
import android.text.style.*;
import android.net.*;
import android.content.*;

public class ConfigActivity extends Activity implements Runnable{

	private boolean isVisible=false;
	private boolean needRun=false;;
	private TextView statusTv;
	private TextView statusTvB;
	private Looper mainLooper;


	int color;
	int step;//(0-255)
	int stage;//0-5

	/** 没良心的method */
	@Override
	public void run(){
		if(Looper.myLooper()==mainLooper){
			statusTv.setTextColor(color);
			return;
		}
		while(isVisible&&needRun){
			try{
				Thread.sleep(100);
			}catch(InterruptedException e){}
			step+=30;
			stage=(stage+step/256)%6;
			step=step%256;
			switch(stage){
				case 0:
					color=Color.argb(255,255,step,0);//R-- RG-
					break;
				case 1:
					color=Color.argb(255,255-step,255,0);//RG- -G-
					break;
				case 2:
					color=Color.argb(255,0,255,step);//-G- -GB
					break;
				case 3:
					color=Color.argb(255,0,255-step,255);//-GB --B
					break;
				case 4:
					color=Color.argb(255,step,0,255);//--B R-B
					break;
				case 5:
					color=Color.argb(255,255,0,255-step);//R-B R--
					break;
			}
			runOnUiThread(this);
		}
	}




	//ClazzExplorer ce;
    @Override
    protected void onCreate(Bundle savedInstanceState){
		Utils.log("OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		String str="";
		mainLooper=Looper.getMainLooper();
		try{
			str+="SystemClassLoader:"+ClassLoader.getSystemClassLoader()+
				"\nActiveModuleVersion:"+Utils.getActiveModuleVersion()
				+"\nThisVersion:"+Utils.CURRENT_MODULE_VERSION;
		}catch(Throwable r){
			str+=r;
		}
		((TextView)findViewById(R.id.mainTextView)).setText(str);
		statusTv=(TextView)findViewById(R.id.mainTextViewStatusA);
		statusTvB=(TextView)findViewById(R.id.mainTextViewStatusB);
		if(Utils.getActiveModuleVersion()==0){
			statusTv.setText("!!! 错误:本模块没有激活 !!!");
			statusTvB.setText("请在正确安装Xposed框架后,在Xposed Installer中(重新)勾选QNotified以激活本模块");
			needRun=true;
		}else{
			statusTv.setText("模块已激活");
			statusTv.setTextColor(0xB000FF00);
			statusTvB.setText("更新模块后需要重启手机方可生效\n当前生效版本号见下方ActiveModuleVersion");
		}
		TextView tv=(TextView)findViewById(R.id.mainTextViewQqNum);
		try{
			//tv.setMovementMethod(LinkMovementMethod.getInstance());
			SpannableString ss = new SpannableString("QQ: 1041703712");  
			ss.setSpan(new URLSpan("http://wpa.qq.com/msgrd?v=3&uin=1041703712&site=qq&menu=yes"), 4, 14,  
					   Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  
			tv.setText(ss);
			tv.setClickable(true);
		}catch(Throwable e){
			tv.setText(""+e);
		}
		
		/*new Thread(new Runnable(){
				@Override
				public void run(){
					try{
						Thread.sleep(1000);
					}catch(InterruptedException e){}
					runOnUiThread(new Runnable(){
							@Override
							public void run(){
								Initiator it=new Initiator();
								it.showPopup(getWindow().getDecorView());
							}
						});
				}
			}).start();*/
    }
	
	
	public void onAddQqClick(View v){
		Uri uri = Uri.parse("http://wpa.qq.com/msgrd?v=3&uin=1041703712&site=qq&menu=yes");
		Intent intent = new Intent(Intent.ACTION_VIEW,uri);
		startActivity(intent);
	}

	@Override
	protected void onPause(){
		isVisible=false;
		super.onPause();
	}

	@Override
	protected void onStop(){
		isVisible=false;
		super.onStop();
	}

	@Override
	protected void onResume(){
		isVisible=true;
		if(needRun){
			new Thread(this).start();
		}
		super.onResume();
	}



	@Override
	public void onPointerCaptureChanged(boolean hasCapture){
		// TODO: Implement this method
	}

}
