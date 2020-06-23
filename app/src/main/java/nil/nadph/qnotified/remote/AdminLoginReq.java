package nil.nadph.qnotified.remote;

import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;
import com.qq.taf.jce.JceStruct;

import java.io.IOException;

public class AdminLoginReq extends JceStruct {
    public String opKey;

    public AdminLoginReq() {
    }

    public AdminLoginReq(byte[] b) throws IOException {
        JceInputStream in = new JceInputStream(b);
        readFrom(in);
    }

    public AdminLoginReq(String k) {
        opKey = k;
    }

    @Override
    public void writeTo(JceOutputStream os) throws IOException {
        os.write(opKey, 0);
    }

    @Override
    public void readFrom(JceInputStream is) throws IOException {
        opKey = is.read("", 0, true);
    }
}
