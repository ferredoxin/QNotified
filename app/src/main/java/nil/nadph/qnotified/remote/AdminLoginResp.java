package nil.nadph.qnotified.remote;

import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;
import com.qq.taf.jce.JceStruct;

import java.io.IOException;

public class AdminLoginResp extends JceStruct {
    public static final int E_INVALID_KEY = 1;

    public int result;//0
    public long token;//1

    public AdminLoginResp() {
    }

    public AdminLoginResp(byte[] b) throws IOException {
        JceInputStream in = new JceInputStream(b);
        readFrom(in);
    }

    public AdminLoginResp(int r, long t) {
        result = r;
        token = t;
    }

    @Override
    public void writeTo(JceOutputStream os) throws IOException {
        os.write(result, 0);
        os.write(token, 1);
    }

    @Override
    public void readFrom(JceInputStream is) throws IOException {
        result = is.read(0, 0, true);
        token = is.read(0L, 1, true);
    }
}
