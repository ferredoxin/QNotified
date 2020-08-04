package nil.nadph.qnotified.remote;

import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;
import com.qq.taf.jce.JceStruct;

public class GetUserStatusReq extends JceStruct {
    public long uin;

    public GetUserStatusReq() {
    }

    public GetUserStatusReq(byte[] b) {
        JceInputStream in = Utf8JceUtils.newInputStream(b);
        readFrom(in);
    }

    public GetUserStatusReq(long uin) {
        this.uin = uin;
    }

    @Override
    public void writeTo(JceOutputStream os) {
        os.write(uin, 0);
    }

    @Override
    public void readFrom(JceInputStream is) {
        uin = is.read(0L, 0, true);
    }
}
