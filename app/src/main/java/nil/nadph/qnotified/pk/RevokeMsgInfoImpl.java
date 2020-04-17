package nil.nadph.qnotified.pk;

import android.os.Parcel;
import android.os.Parcelable;
import nil.nadph.qnotified.util.Nullable;

import static nil.nadph.qnotified.util.Utils.log;

public class RevokeMsgInfoImpl {

    public static final int OP_TYPE_TROOP_UNKNOWN = -1;
    public static final int OP_TYPE_TROOP_MEMBER = 0;
    public static final int OP_TYPE_TROOP_ADMIN = 1;
    public static final int OP_TYPE_TROOP_OWNER = 2;

    public int istroop;
    public long shmsgseq;
    public String friendUin;
    //@Deprecated
    //public int longmsgid;
    public long msgUid;
    public String fromUin;
    //public int f89926c;longMsgCount
    public long time;
    public String sendUin;
    //public int d;longMsgIndex
    @Nullable
    public String authorUin = null;
    @Nullable
    public int opType = -1;

    public RevokeMsgInfoImpl(Parcelable o) {
        if (o == null) throw new NullPointerException("RevokeMsgInfo == null");
        Parcel p = Parcel.obtain();
        try {
            o.writeToParcel(p, 0);
            p.setDataPosition(0);
            istroop = p.readInt();
            shmsgseq = p.readLong();
            friendUin = p.readString();
            sendUin = p.readString();
            msgUid = p.readLong();
            time = p.readLong();
            if (p.dataAvail() > 0) authorUin = p.readString();
            if (p.dataAvail() > 0) opType = p.readInt();
        } catch (Exception e) {
            log(e);
        }
        p.recycle();
        String summery = o.toString();
        int keyIndex = summery.indexOf("fromuin");
        if (keyIndex == -1) {
            log("RevokeMsgInfoImpl/E indexOf('fromuin') == -1, leave fromUin null");
            return;
        }
        int valueStart = summery.indexOf('=', keyIndex) + 1;
        if (summery.charAt(valueStart) == ' ') valueStart++;
        int end1 = summery.indexOf(',', valueStart);
        int end2 = summery.indexOf(' ', valueStart);
        if (end1 == -1) end1 = summery.length() - 1;
        if (end2 == -1) end2 = summery.length() - 1;
        int end = Math.min(end1, end2);
        String str = summery.substring(valueStart, end);
        if (str.equals("null")) {
            fromUin = null;
        } else {
            fromUin = str;
        }
    }

    @Override
    public String toString() {
        return "RevokeMsgInfoImpl{" +
                "istroop=" + istroop +
                ", shmsgseq=" + shmsgseq +
                ", friendUin='" + friendUin + '\'' +
                ", msgUid=" + msgUid +
                ", fromUin='" + fromUin + '\'' +
                ", time=" + time +
                ", sendUin='" + sendUin + '\'' +
                ", authorUin='" + authorUin + '\'' +
                ", opType=" + opType +
                '}';
    }
}
