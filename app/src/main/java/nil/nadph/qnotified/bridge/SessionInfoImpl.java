/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */
package nil.nadph.qnotified.bridge;

import static nil.nadph.qnotified.util.Utils.log;

import android.os.Parcel;
import android.os.Parcelable;
import java.lang.reflect.Constructor;
import nil.nadph.qnotified.util.Initiator;

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
            Constructor<?> c = clSessionInfo.getDeclaredConstructor(Parcel.class);
            c.setAccessible(true);
            ret = (Parcelable) c.newInstance(parcel);
        } catch (Exception e) {
            log(e);
        }
        parcel.recycle();
        return ret;
    }

}
