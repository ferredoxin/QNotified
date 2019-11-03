package nil.nadph.qnotified;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import nil.nadph.qnotified.util.Utils;

import java.io.IOException;
import java.io.InputStream;

public class ConfigActivity extends Activity implements Runnable {

    private boolean isVisible = false;
    private boolean needRun = false;
    ;
    private TextView statusTv;
    private TextView statusTvB;
    private Looper mainLooper;


    int color;
    int step;//(0-255)
    int stage;//0-5

    /**
     * 没良心的method
     */
    @Override
    public void run() {
        if (Looper.myLooper() == mainLooper) {
            statusTv.setTextColor(color);
            return;
        }
        while (isVisible && needRun) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            step += 30;
            stage = (stage + step / 256) % 6;
            step = step % 256;
            switch (stage) {
                case 0:
                    color = Color.argb(255, 255, step, 0);//R-- RG-
                    break;
                case 1:
                    color = Color.argb(255, 255 - step, 255, 0);//RG- -G-
                    break;
                case 2:
                    color = Color.argb(255, 0, 255, step);//-G- -GB
                    break;
                case 3:
                    color = Color.argb(255, 0, 255 - step, 255);//-GB --B
                    break;
                case 4:
                    color = Color.argb(255, step, 0, 255);//--B R-B
                    break;
                case 5:
                    color = Color.argb(255, 255, 0, 255 - step);//R-B R--
                    break;
            }
            runOnUiThread(this);
        }
    }


    //ClazzExplorer ce;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.log("OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        String str = "";
        mainLooper = Looper.getMainLooper();
        try {
            str += "SystemClassLoader:" + ClassLoader.getSystemClassLoader() +
                    "\nActiveModuleVersion:" + Utils.getActiveModuleVersion()
                    + "\nThisVersion:" + Utils.QN_VERSION_NAME;
        } catch (Throwable r) {
            str += r;
        }
        ((TextView) findViewById(R.id.mainTextView)).setText(str);
        statusTv = (TextView) findViewById(R.id.mainTextViewStatusA);
        statusTvB = (TextView) findViewById(R.id.mainTextViewStatusB);

        if (Utils.getActiveModuleVersion() == null) {
            statusTv.setText("!!! 错误:本模块没有激活 !!!");
            statusTvB.setText("请在正确安装Xposed框架后,在Xposed Installer中(重新)勾选QNotified以激活本模块");
            needRun = true;
        } else {
            statusTv.setText("模块已激活");
            statusTv.setTextColor(0xB000FF00);
            statusTvB.setText("更新模块后需要重启手机方可生效\n当前生效版本号见下方ActiveModuleVersion");
        }
        TextView tv = (TextView) findViewById(R.id.mainTextViewQqNum);
        try {
            //tv.setMovementMethod(LinkMovementMethod.getInstance());
            SpannableString ss = new SpannableString("QQ: 1041703712");
            ss.setSpan(new URLSpan("http://wpa.qq.com/msgrd?v=3&uin=1041703712&site=qq&menu=yes"), 4, 14,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(ss);
            tv.setClickable(true);
        } catch (Throwable e) {
            tv.setText("" + e);
        }

        InputStream in = ConfigActivity.class.getClassLoader().getResourceAsStream("assets/xposed_init");
        byte buf[] = new byte[64];
        String start = "";
        try {
            int len = in.read(buf);
            in.close();
            start = new String(buf, 0, len).replace("\n", "").replace("\r", "").replace(" ", "");
        } catch (IOException e) {
            start = e.toString();
        }
        TextView vtv = (TextView) findViewById(R.id.mainTextViewVersion);
        if (start.equals("nil.nadph.qnotified.HookLoader")) {
            vtv.setText("动态加载");
            vtv.setTextColor(Color.BLUE);
        } else if (start.equals("nil.nadph.qnotified.HookEntry")) {
            vtv.setText("静态");
            //vtv.setTextColor(Color.BLUE);
        } else {
            vtv.setText(start);
            vtv.setTextColor(Color.RED);
        }
/*
		new Thread(new Runnable(){
				@Override
				public void run(){
					final ColorStateList color_=QThemeKit.getStateColorInXml("/tmp/hyc.xml");
					/*try{
					 FileInputStream is = new FileInputStream("/tmp/skin_black.xml");
					 /*byte[] buf = new byte[is.available()];
					 int bytesRead = is.read(buf);       
					 is.close();*
					 AXmlResourceParser axml=new AXmlResourceParser();
					 axml.open(is);
					 final ColorStateList color=ResInflater.inflateColorFromXml(getResources(),axml,null);
					 }catch(XmlPullParserException e){}catch(IOException e){}*
					runOnUiThread(new Runnable(){
							@Override
							public void run(){
								TextView t=findViewById(R.id.mainTextViewStatusB);
								t.setEnabled(true);
								t.setClickable(true);
								t.setTextColor(color_);
							}
						});
				}
			}).start();*/
    }


    public void onAddQqClick(View v) {
        Uri uri = Uri.parse("http://wpa.qq.com/msgrd?v=3&uin=1041703712&site=qq&menu=yes");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        isVisible = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        isVisible = false;
        super.onStop();
    }

    @Override
    protected void onResume() {
        isVisible = true;
        if (needRun) {
            new Thread(this).start();
        }
        super.onResume();
    }

    public void onPointerCaptureChanged(boolean hasCapture) {
    }

}
