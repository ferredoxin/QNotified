package nil.nadph.qnotified.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.LinearLayout;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.ui.ViewBuilder;

@SuppressLint("Registered")
public class ManageScriptsActivity extends IphoneTitleBarActivityCompat {
    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.addView(ViewBuilder.newListItemSwitch(this, "总开关(关闭后所有脚本均不生效)", null, false, null));
        main.addView(ViewBuilder.newListItemButton(this, "导入 ...", null, null, null));
        main.addView(ViewBuilder.newListItemDummy(this, "dummy.js (禁用)", null, null));
        //main.addView(ViewBuilder.newListItemSwitch(this, "总开关", null, true, null));
        setContentView(main);
        setTitle("脚本管理(.js)");
        setRightButton("帮助", ViewBuilder.clickToProxyActAction(ScriptGuideActivity.class));
        setContentBackgroundDrawable(ResUtils.skin_background);
        return true;
    }

    @Override
    public void doOnResume() {
        super.doOnResume();
    }
}
