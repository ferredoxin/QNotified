package nil.nadph.qnotified.bridge;

import android.os.Parcel;
import android.os.Parcelable;
import nil.nadph.qnotified.util.Initiator;

import java.lang.reflect.Constructor;

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
