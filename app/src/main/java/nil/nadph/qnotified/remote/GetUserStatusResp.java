package nil.nadph.qnotified.remote;

import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;
import com.qq.taf.jce.JceStruct;

public class GetUserStatusResp extends JceStruct {
    public long uin;//0
    public int blacklistFlags;//1
    public int whitelistFlags;//2

    @Override
    public void writeTo(JceOutputStream os) {
        os.write(uin, 0);
        os.write(blacklistFlags, 1);
        os.write(whitelistFlags, 2);
    }

    @Override
    public void readFrom(JceInputStream is) {
        uin = is.read(0L, 0, true);
        blacklistFlags = is.read(0, 1, true);
        whitelistFlags = is.read(0, 2, true);
    }
}
