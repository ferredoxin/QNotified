package nil.nadph.qnotified.remote;

import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;
import com.qq.taf.jce.JceStruct;

import java.io.IOException;

public class GetUserStatusReq extends JceStruct {
    public long uin;

    public GetUserStatusReq() {
    }

    public GetUserStatusReq(byte[] b) throws IOException {
        JceInputStream in = new JceInputStream(b);
        readFrom(in);
    }

    public GetUserStatusReq(long uin) {
        this.uin = uin;
    }

    @Override
    public void writeTo(JceOutputStream os) throws IOException {
        os.write(uin, 0);
    }

    @Override
    public void readFrom(JceInputStream is) throws IOException {
        uin = is.read(0L, 0, true);
    }
}
