/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;

import me.singleneuron.util.HookStatue;
import nil.nadph.qnotified.HookEntry;
import nil.nadph.qnotified.MainHook;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.util.Natives;
import nil.nadph.qnotified.util.Utils;

public class ConfigActivity extends Activity implements Runnable {

    int color;
    int step;//(0-255)
    int stage;//0-5
    private boolean isVisible = false;
    private boolean needRun = false;
    private TextView statusTv;
    private TextView statusTvB;
    private Looper mainLooper;

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
            } catch (InterruptedException ignored) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (R.string.res_inject_success >>> 24 == 0x7f) {
            throw new RuntimeException("package id must NOT be 0x7f");
        }
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
        statusTv = findViewById(R.id.mainTextViewStatusA);
        statusTvB = findViewById(R.id.mainTextViewStatusB);

        HookStatue.Statue statue = HookStatue.INSTANCE.getStatue(this, false);

        if (!HookStatue.INSTANCE.isActive(statue)) {
            statusTv.setText("免费软件-请勿倒卖");
            statusTvB.setText(getString(HookStatue.INSTANCE.getStatueName(statue))
                    + "，请在正确安装Xposed框架后,在Xposed Installer中(重新)勾选QNotified以激活本模块(太极/无极请无视提示)");
            needRun = true;
        } else {
            statusTv.setText(HookStatue.INSTANCE.getStatueName(statue));
            statusTv.setTextColor(0xB000FF00);
            statusTvB.setText("更新模块后需要重启手机方可生效\n当前生效版本号见下方ActiveModuleVersion");
        }

        InputStream in = ConfigActivity.class.getClassLoader().getResourceAsStream("assets/xposed_init");
        byte[] buf = new byte[64];
        String start;
        try {
            int len = in.read(buf);
            in.close();
            start = new String(buf, 0, len).replace("\n", "").replace("\r", "").replace(" ", "");
        } catch (IOException e) {
            start = e.toString();
        }
        TextView vtv = findViewById(R.id.mainTextViewVersion);
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
        String text;
        try {
            long delta = System.currentTimeMillis();
            Natives.load(this);
            long ts = Utils.getBuildTimestamp();
            delta = System.currentTimeMillis() - delta;
            text = "Build Time: " + (ts > 0 ? new Date(ts).toString() : "unknown") + ", delta=" + delta + "ms\n" +
                    "SUPPORTED_ABIS=" + Arrays.toString(Build.SUPPORTED_ABIS) + "\npageSize=" + Natives.getpagesize();
        } catch (Throwable e) {
            text = e.toString();
        }
        TextView tvbt = findViewById(R.id.mainTextViewBuildTime);
        tvbt.setText(text);
/*
		new Thread(new Runnable(){
				@Override
				public void run(){
					final ColorStateList color_=ResUtils.getStateColorInXml("/tmp/hyc.xml");
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
//        Molecule mol = null;
//        try {
//            String molstr = new String(ResUtils.readAll(ResUtils.openAsset("9280425.mol")));
//            mol = MdlMolParser.parseString(molstr);
//        } catch (Exception e) {
//            Utils.log(e);
//        }
//        MoleculeView moleculeView = new MoleculeView(this);
//        moleculeView.setTextColor(0xFFFFFFFF);
//        moleculeView.setGravity(Gravity.CENTER);
//        moleculeView.setMolecule(mol);
//        try {
//            long begin = System.currentTimeMillis();
//            HashSet<Integer> chiral = ChiralCarbonHelper.getMoleculeChiralCarbons(mol);
//            long delta = System.currentTimeMillis() - begin;
//            Log.i("QNdump", "getMoleculeChiralCarbons took " + delta + "ms");
//            moleculeView.setSelectedChiral(Utils.integerSetToArray(chiral));
//        } catch (Exception e) {
//            Log.e("QNdump", "onCreate: getMoleculeChiralCarbons", e);
//        }
//        LinearLayout ll = findViewById(R.id.mainLinearLayout2);
//        ll.removeAllViews();
//        ll.addView(moleculeView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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

    public void openModuleSettingForHost(View view) {
        String pkg = null;
        switch (view.getId()) {
            case R.id.mainRelativeLayoutButtonOpenQQ:
                pkg = HookEntry.PACKAGE_NAME_QQ;
                break;
            case R.id.mainRelativeLayoutButtonOpenTIM:
                pkg = HookEntry.PACKAGE_NAME_TIM;
                break;
        }
        if (pkg != null) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(pkg, "com.tencent.mobileqq.activity.JumpActivity"));
            intent.setAction(Intent.ACTION_VIEW);
            intent.putExtra(MainHook.JUMP_ACTION_CMD, MainHook.JUMP_ACTION_SETTING_ACTIVITY);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                new AlertDialog.Builder(this).setTitle("出错啦")
                        .setMessage("拉起模块设置失败, 请确认 " + pkg + " 已安装并启用(没有被关冰箱或被冻结停用)\n" + e.toString())
                        .setCancelable(true).setPositiveButton(android.R.string.ok, null).show();
            }
        }
    }
}
