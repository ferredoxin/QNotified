package nil.nadph.qnotified;

import java.io.Serializable;


public class EventRecord implements Serializable, Cloneable, Comparable {
    private static final long serialVersionUID = 1L;
    public static final long TIME_UNKNOWN = -1L;
    public static final int EVENT_ERROR = 0;
    public static final int EVENT_RESERVED = 1;
    public static final int EVENT_FRIEND_ADD = 2;
    public static final int EVENT_FRIEND_DELETE = 3;
    public static final int EVENT_FRIEND_SIDE = 4;
    public static final int EVENT_NICKNAME_CHANGE = 5;
    public static final int EVENT_REMARK_CHANGE = 6;
    public static final int EVENT_LOCAL_REMARK_CHANGE = 7;
    public static final int EVENT_ACCOUNT_DESTRUCTION = 8;

    public long timeRangeBegin;
    public long timeRangeEnd;
    public int event;
    public long operator;//uin
    public String before;
    public String after;
    public String extra;
    public int _friendStatus;
    public String _remark;
    public String _nick;


    public String getShowStr() {
        if (_remark != null && _remark.length() > 0) return _remark;
        else if (_nick != null && _nick.length() > 0) return _nick;
        else return "" + operator;
    }

    public String getShowStrWithUin() {
        if (_remark != null && _remark.length() > 0) return _remark + "(" + operator + ")";
        else if (_nick != null && _nick.length() > 0) return _nick + "(" + operator + ")";
        else return "" + operator;
    }

    @Override
    public int compareTo(Object obj) {
        EventRecord ev = (EventRecord) obj;
        return (int) (ev.timeRangeEnd - this.timeRangeEnd);
    }
}
	
