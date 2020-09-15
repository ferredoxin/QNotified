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
package nil.nadph.qnotified.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.LinearLayout;

public class IsolatedStateLinearLayout extends LinearLayout {
    public IsolatedStateLinearLayout(Context context) {
        super(context);
    }

    public IsolatedStateLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IsolatedStateLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IsolatedStateLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        int id = getId();
        if (id == NO_ID) return;
        Parcelable superState = super.onSaveInstanceState();
        IsolatedLayoutSaveState iss = new IsolatedLayoutSaveState(superState);
        SparseArray<Parcelable> array = iss.childStates;
        super.dispatchSaveInstanceState(array);
        container.put(id, iss);
    }

    @Override
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        int id = getId();
        if (id == NO_ID) return;
        Parcelable state = container.get(id);
        if (state == null) return;
        IsolatedLayoutSaveState ss = (IsolatedLayoutSaveState) state;
        SparseArray<Parcelable> array = ss.childStates;
        super.dispatchRestoreInstanceState(array);
        super.onRestoreInstanceState(ss.getSuperState());
    }
}
