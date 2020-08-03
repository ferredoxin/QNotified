package nil.nadph.qnotified.remote;

import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;
import com.qq.taf.jce.JceStruct;

public class FromServiceMsg extends JceStruct {
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private int uniSeq;//0
    private int resultCode;//1
    private String errorMsg = "";//2
    private byte[] body = EMPTY_BYTE_ARRAY;//3

    public FromServiceMsg() {
    }

    public FromServiceMsg(int u) {
        uniSeq = u;
    }

    public FromServiceMsg(int u, JceStruct struct) {
        uniSeq = u;
        JceOutputStream jout = Utf8JceUtils.newOutputStream();
        try {
            struct.writeTo(jout);
            resultCode = 0;
            errorMsg = "";
            body = jout.toByteArray();
        } catch (Exception e) {
            resultCode = 500;
            errorMsg = "internal server error";
            body = EMPTY_BYTE_ARRAY;
        }
    }

    public FromServiceMsg(int u, byte[] b) {
        uniSeq = u;
        resultCode = 0;
        errorMsg = "";
        body = b;
    }

    public FromServiceMsg(int u, int e, String str) {
        uniSeq = u;
        resultCode = e;
        errorMsg = str;
        body = EMPTY_BYTE_ARRAY;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public byte[] getBody() {
        return body;
    }

    public void getBody(JceStruct struct) {
        byte[] b = getBody();
        JceInputStream in = Utf8JceUtils.newInputStream(b);
        struct.readFrom(in);
    }

    public void setUniSeq(int uniSeq) {
        this.uniSeq = uniSeq;
    }

    public int getUniSeq() {
        return uniSeq;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public void writeTo(JceOutputStream os) {
        os.write(uniSeq, 0);
        os.write(resultCode, 1);
        os.write(errorMsg, 2);
        os.write(body, 3);
    }

    @Override
    public void readFrom(JceInputStream is) {
        uniSeq = is.read(0, 0, true);
        resultCode = is.read(0, 1, true);
        errorMsg = is.readString(2, false);
        body = is.read(EMPTY_BYTE_ARRAY, 3, false);
    }
}
