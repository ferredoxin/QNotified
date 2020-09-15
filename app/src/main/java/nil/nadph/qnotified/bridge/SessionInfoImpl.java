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
package nil.nadph.qnotified.bridge;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Constructor;

import nil.nadph.qnotified.util.Initiator;

import static nil.nadph.qnotified.util.Utils.log;

public class SessionInfoImpl {


    public static Parcelable createSessionInfo(String uin, int uinType) {
        Parcel parcel = Parcel.obtain();
        parcel.writeInt(uinType);
        parcel.writeString(uin);
        parcel.writeString(null);//troopUin_b
        parcel.writeString(null);//uin_name_d
        parcel.writeString(null);//phoneNum_e
        parcel.writeInt(3999);//add_friend_source_id_d
        parcel.writeBundle(null);
        parcel.setDataPosition(0);
        Parcelable ret = null;
        try {
            Class<?> clSessionInfo = Initiator._SessionInfo();
            Constructor<?> c = clSessionInfo.getConstructor(Parcel.class);
            c.setAccessible(true);
            ret = (Parcelable) c.newInstance(parcel);
        } catch (Exception e) {
            log(e);
        }
        parcel.recycle();
        return ret;
    }

}
