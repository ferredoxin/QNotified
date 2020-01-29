package nil.nadph.qnotified.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.*;
import nil.nadph.qnotified.hook.FakeBatteryHook;
import nil.nadph.qnotified.ui.ResUtils;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.subtitle;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class FakeBatCfgActivity implements ActivityAdapter {
    private Activity self;

    public FakeBatCfgActivity(Activity activity) {
        self = activity;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void doOnPostCreate(Bundle savedInstanceState) throws Throwable {
        LinearLayout ll = new LinearLayout(self);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(self);
        __ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup bounceScrollView = (ViewGroup) new_instance(load("com/tencent/mobileqq/widget/BounceScrollView"), self, null, Context.class, AttributeSet.class);
        //invoke_virtual(bounceScrollView,"a",true,500,500,boolean.class,int.class,int.class);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        bounceScrollView.setBackgroundDrawable(ResUtils.qq_setting_item_bg_nor);
        //invoke_virtual(bounceScrollView,"setNeedHorizontalGesture",true,boolean.class);
        LinearLayout.LayoutParams fixlp = new LinearLayout.LayoutParams(MATCH_PARENT, dip2px(self, 48));
        RelativeLayout.LayoutParams __lp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int mar = (int) (dip2px(self, 12) + 0.5f);
        __lp_l.setMargins(mar, 0, mar, 0);
        __lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        __lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams __lp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        __lp_r.setMargins(mar, 0, mar, 0);
        __lp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        __lp_r.addRule(RelativeLayout.CENTER_VERTICAL);

        ll.addView(subtitle(self, "!!! 此功能仅在 QQ>=6.2.8 且在线状态为 我的电量 时生效"));
        ll.addView(subtitle(self, "自定义电量百分比:"));
        EditText pct = new EditText(self);
        pct.setInputType(TYPE_CLASS_NUMBER);
        pct.setTextColor(ResUtils.skin_black);
        pct.setTextSize(dip2sp(self, 18));
        pct.setBackgroundDrawable(null);
        pct.setText(FakeBatteryHook.get().getFakeBatteryCapacity() + "");
        ll.addView(pct, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        CheckBox charging = new CheckBox(self);
        charging.setText("正在充电");
        charging.setTextSize(dip2sp(self, 16));
        charging.setTextColor(ResUtils.skin_black);
        charging.setButtonDrawable(ResUtils.getCheckBoxBackground());
        charging.setChecked(FakeBatteryHook.get().isFakeBatteryCharging());
        ll.addView(charging, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        TextView apply = new TextView(self);
        apply.setGravity(Gravity.CENTER);
        apply.setText("应用");
        apply.setTextColor(ResUtils.skin_blue);
        apply.setBackgroundDrawable(null);
        apply.setTextSize(dip2sp(self, 20));
        ll.addView(apply, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        //bounceScrollView.setFocusable(true);
        //bounceScrollView.setFocusableInTouchMode(true);
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        self.setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;
        //__ll.addView(bounceScrollView,_lp_fat);
        ActProxyMgr.setContentBackgroundDrawable(self, ResUtils.skin_background);
        invoke_virtual(self, "setTitle", "自定义电量", CharSequence.class);
        invoke_virtual(self, "setImmersiveStatus");
        invoke_virtual(self, "enableLeftBtn", true, boolean.class);
        //TextView rightBtn=(TextView)invoke_virtual(self,"getRightTextView");
        //log("Title:"+invoke_virtual(self,"getTextTitle"));
    }

    @Override
    public void doOnPostResume() throws Throwable {
    }

    @Override
    public void doOnPostPause() throws Throwable {
    }

    @Override
    public void doOnPostDestory() throws Throwable {
    }

    @Override
    public void doOnPostActivityResult(int requestCode, int resultCode, Intent data) throws Throwable {
    }

    @Override
    public boolean isWrapContent() throws Throwable {
        return true;
    }
}