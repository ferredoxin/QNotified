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

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;

import nil.nadph.qnotified.util.Initiator;

public class IsolatedLayoutSaveState extends View.BaseSavedState {
    public static final Parcelable.Creator<IsolatedLayoutSaveState> CREATOR
            = new Creator<IsolatedLayoutSaveState>() {
        @Override
        public IsolatedLayoutSaveState createFromParcel(Parcel source) {
            return new IsolatedLayoutSaveState(source);
        }

        @Override
        public IsolatedLayoutSaveState[] newArray(int size) {
            return new IsolatedLayoutSaveState[size];
        }
    };

    public final SparseArray<Parcelable> childStates;

    public IsolatedLayoutSaveState(Parcel source) {
        super(source);
        childStates = source.readSparseArray(Initiator.getPluginClassLoader());
    }

    public IsolatedLayoutSaveState(Parcelable superState) {
        super(superState);
        childStates = new SparseArray<>();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeSparseArray(childStates);
    }
}
