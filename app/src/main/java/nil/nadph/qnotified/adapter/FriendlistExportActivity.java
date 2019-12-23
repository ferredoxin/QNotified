package nil.nadph.qnotified.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import nil.nadph.qnotified.util.QQViewBuilder;
import nil.nadph.qnotified.util.QThemeKit;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.QQViewBuilder.subtitle;
import static nil.nadph.qnotified.util.Utils.*;

public class FriendlistExportActivity implements ActivityAdapter {

    private Activity self;

    private static final int R_ID_CHECKBOX_CSV = 0x300AFF61;
    private static final int R_ID_CHECKBOX_JSON = 0x300AFF62;

    public FriendlistExportActivity(Activity activity) {
        self = activity;
    }

    @Override
    public void doOnPostCreate(Bundle savedInstanceState) throws Throwable {
        LinearLayout ll = new LinearLayout(self);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(self);
        __ll.setOrientation(LinearLayout.VERTICAL);
        final ViewGroup bounceScrollView = (ViewGroup) new_instance(load("com/tencent/mobileqq/widget/BounceScrollView"), self, null, Context.class, AttributeSet.class);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        bounceScrollView.setBackgroundColor(QThemeKit.qq_setting_item_bg_nor.getDefaultColor());
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


        ll.addView(subtitle(self, "导出格式"));

        final CheckBox cbCsv = new CheckBox(self);
        cbCsv.setButtonDrawable(QThemeKit.getCheckBoxBackground());
        cbCsv.setText("CSV");
        cbCsv.setId(R_ID_CHECKBOX_CSV);
        final CheckBox cbJson = new CheckBox(self);
        cbJson.setButtonDrawable(QThemeKit.getCheckBoxBackground());
        cbJson.setText("Json");
        cbJson.setId(R_ID_CHECKBOX_JSON);

        LinearLayout llcsvopt = new LinearLayout(self);
        llcsvopt.setOrientation(LinearLayout.VERTICAL);
        llcsvopt.addView(QQViewBuilder.subtitle(self, "CSV设定"));
        //llcsvopt.

        View.OnClickListener formatListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R_ID_CHECKBOX_CSV:
                        cbCsv.setChecked(true);
                        cbJson.setChecked(false);
                        break;
                    case R_ID_CHECKBOX_JSON:
                        cbCsv.setChecked(false);
                        cbJson.setChecked(true);
                }
            }
        };
        cbCsv.setOnClickListener(formatListener);
        cbJson.setOnClickListener(formatListener);


        formatListener.onClick(cbCsv);

        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        self.setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;
        //__ll.addView(bounceScrollView,_lp_fat);
        //sdlv.setBackgroundColor(0xFFAA0000)
        invoke_virtual(self, "setTitle", "导出好友列表", CharSequence.class);
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
