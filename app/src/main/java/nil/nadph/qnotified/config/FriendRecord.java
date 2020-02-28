package nil.nadph.qnotified.config;

import java.io.Serializable;

import static nil.nadph.qnotified.util.Utils.en;


public class FriendRecord implements Serializable, Cloneable {

    public static final int STATUS_ERROR = 0;
    public static final int STATUS_RESERVED = 1;
    public static final int STATUS_STRANGER = 2;
    public static final int STATUS_EXFRIEND = 3;
    public static final int STATUS_FRIEND_MUTUAL = 4;
    public static final int STATUS_FRIEND_SIDE_0 = 5;
    public static final int STATUS_FRIEND_SIDE_1 = 6;
    public static final int STATUS_BLACKLIST = 7;
    private static final long serialVersionUID = 1L;
    public long uin;

    public String nick;

    public String remark;//local or normal

    public int friendStatus;

    /**
     * 10dec-length,sec
     */
    public long serverTime;

    @Override
    public int hashCode() {
        return (int) uin;
    }

    public String getShowStr() {
        if (remark != null && remark.length() > 0) return remark;
        else if (nick != null && nick.length() > 0) return nick;
        else return "" + uin;
    }

    public String getShowStrWithUin() {
        if (remark != null && remark.length() > 0) return remark + "(" + uin + ")";
        else if (nick != null && nick.length() > 0) return nick + "(" + uin + ")";
        else return "" + uin;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{uin=");
        sb.append(uin);
        sb.append(",nick=");
        sb.append(en(nick));
        sb.append(",remark=");
        sb.append(en(remark));
        sb.append(",friendStatus=");
        sb.append(friendStatus);
        sb.append(",serverTime=");
        sb.append(serverTime);
		/*if(events==null){
			sb.append("null}");
			return sb.toString();
		}
		sb.append("[");
		for(int i=0;i<events.length;i++){
			sb.append("{timeRangeBegin=");
			sb.append(events[i].timeRangeBegin);
			sb.append(",timeRangeEnd=");
			sb.append(events[i].timeRangeEnd);
			sb.append(",event=");
			sb.append(events[i].event);
			sb.append(",operand=");
			sb.append(events[i].operand);
			sb.append(",before=");
			sb.append(en(events[i].before));
			sb.append(",after=");
			sb.append(en(events[i].after));
			sb.append(",extra=");
			sb.append(en(events[i].extra));
			sb.append("},");
		}*/
        if (sb.charAt(sb.length() - 1) == ',') sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        try {
            return uin == ((FriendRecord) obj).uin;
        } catch (Exception e) {
            return false;
        }
    }


}
