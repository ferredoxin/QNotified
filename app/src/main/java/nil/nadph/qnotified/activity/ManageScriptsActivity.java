/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/cinit/QNotified
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
        main.addView(ViewBuilder.newListItemSwitchStub(this, "总开关(关闭后所有脚本均不生效)", null, false));
        main.addView(ViewBuilder.newListItemButton(this, "导入 ...", null, null, null));
        main.addView(ViewBuilder.newListItemDummy(this, "demo.java (禁用)", null, null));
        //main.addView(ViewBuilder.newListItemSwitch(this, "总开关", null, true, null));
        setContentView(main);
        setTitle("脚本");
        setRightButton("帮助", ViewBuilder.clickToProxyActAction(ScriptGuideActivity.class));
        setContentBackgroundDrawable(ResUtils.skin_background);
        return true;
    }

    @Override
    public void doOnResume() {
        super.doOnResume();
    }
}
