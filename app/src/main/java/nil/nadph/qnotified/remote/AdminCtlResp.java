package nil.nadph.qnotified.remote;

import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;
import com.qq.taf.jce.JceStruct;

import java.io.IOException;

public class AdminCtlResp extends JceStruct {
    public static final int E_PERM = 1;
    public static final int E_INVALID_OP = 2;
    public static final int E_INVALID_ARG = 3;

    public int result;//0
    public String msg = "";//1

    public AdminCtlResp() {
    }

    public AdminCtlResp(byte[] b) throws IOException {
        JceInputStream in = new JceInputStream(b);
        readFrom(in);
    }

    public AdminCtlResp(int r, String m) {
        result = r;
        msg = m;
    }

    @Override
    public void writeTo(JceOutputStream os) throws IOException {
        os.write(result, 0);
        os.write(msg, 1);
    }

    @Override
    public void readFrom(JceInputStream is) throws IOException {
        result = is.read(0, 0, true);
        msg = is.read("", 1, false);
    }
}
