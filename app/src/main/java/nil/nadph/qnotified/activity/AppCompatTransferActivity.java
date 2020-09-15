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

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import nil.nadph.qnotified.util.CliOper;
import nil.nadph.qnotified.util.Utils;

public class AppCompatTransferActivity extends AppCompatActivity {
    @Override
    public ClassLoader getClassLoader() {
        return AppCompatTransferActivity.class.getClassLoader();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Bundle windowState = savedInstanceState.getBundle("android:viewHierarchyState");
            if (windowState != null) {
                windowState.setClassLoader(AppCompatTransferActivity.class.getClassLoader());
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CliOper.enterModuleActivity(Utils.getShort$Name(this));
    }
}
