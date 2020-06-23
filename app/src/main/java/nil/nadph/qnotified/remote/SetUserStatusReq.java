package nil.nadph.qnotified.remote;

import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;
import com.qq.taf.jce.JceStruct;

import java.io.IOException;

public class SetUserStatusReq extends JceStruct {
    public long uin;//0
    public int blacklistFlags;//1
    public int whitelistFlags;//2
    public String comment = "";//3

    public SetUserStatusReq() {
    }

    public SetUserStatusReq(byte[] b) throws IOException {
        JceInputStream in = new JceInputStream(b);
        readFrom(in);
    }

    @Override
    public void writeTo(JceOutputStream os) throws IOException {
        os.write(uin, 0);
        os.write(blacklistFlags, 1);
        os.write(whitelistFlags, 2);
        os.write(comment, 3);
    }

    @Override
    public void readFrom(JceInputStream is) throws IOException {
        uin = is.read(0L, 0, true);
        blacklistFlags = is.read(0, 1, true);
        whitelistFlags = is.read(0, 2, true);
        comment = is.read("", 2, false);
    }
}
