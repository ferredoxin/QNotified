package nil.nadph.qnotified.remote;

import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;
import com.qq.taf.jce.JceStruct;

import java.io.IOException;

public class SetUserStatusResp extends JceStruct {
    public static final int E_PERM = 1;

    public long uin;
    public int result;

    public SetUserStatusResp() {
    }

    public SetUserStatusResp(long uin) {
        this.uin = uin;
    }

    @Override
    public void writeTo(JceOutputStream os) throws IOException {
        os.write(uin, 0);
        os.write(result, 1);
    }

    @Override
    public void readFrom(JceInputStream is) throws IOException {
        uin = is.read(0L, 0, true);
        result = is.read(0, 1, true);
    }
}
