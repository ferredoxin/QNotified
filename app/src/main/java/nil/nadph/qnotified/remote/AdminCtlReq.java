package nil.nadph.qnotified.remote;

import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;
import com.qq.taf.jce.JceStruct;

import java.io.IOException;

public class AdminCtlReq extends JceStruct {
    public static final int OP_NOP = 0;
    public static final int OP_LOGOUT = 1;

    public int op;//0
    public int arg1;//1

    public AdminCtlReq() {
    }

    public AdminCtlReq(byte[] b) throws IOException {
        JceInputStream in = new JceInputStream(b);
        readFrom(in);
    }

    public AdminCtlReq(int o, int a1) {
        op = o;
        arg1 = a1;
    }

    @Override
    public void writeTo(JceOutputStream os) throws IOException {
        os.write(op, 0);
        os.write(arg1, 1);
    }

    @Override
    public void readFrom(JceInputStream is) throws IOException {
        op = is.read(0, 0, true);
        arg1 = is.read(0, 1, true);
    }
}
