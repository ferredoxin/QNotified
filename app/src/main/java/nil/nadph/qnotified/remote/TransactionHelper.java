package nil.nadph.qnotified.remote;

import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;

import javax.net.ssl.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

public class TransactionHelper {
    private static SSLSocketFactory sslFactory;
    private static final String TAG = "TransactionHelper";
    private static final String TKS_PASSWORD = "NAuth-v1";

    public static void initSslContext() {
        if (sslFactory != null) return;
        try {
            KeyStore keyStore = KeyStore.getInstance("BKS"); // 访问keytool创建的Java密钥库
            InputStream keyStream = TransactionHelper.class.getClassLoader().getResourceAsStream("assets/na_cert.bks");
            if (keyStream == null) {
                throw new RuntimeException("shipped bks not found!!!");
            }
            char[] keyStorePass = TKS_PASSWORD.toCharArray();  //证书密码
            keyStore.load(keyStream, keyStorePass);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);//保存服务端的授权证书
            SSLContext ssl_ctx = SSLContext.getInstance("SSL");
            TrustManager[] m = trustManagerFactory.getTrustManagers();
            ssl_ctx.init(null, m, new SecureRandom());
            sslFactory = ssl_ctx.getSocketFactory();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GetUserStatusResp doQueryUserStatus(long uin) throws Exception {
        GetUserStatusReq req = new GetUserStatusReq(uin);
        JceOutputStream body = Utf8JceUtils.newOutputStream();
        req.writeTo(body);
        ToServiceMsg toServiceMsg = new ToServiceMsg("NAuth.QNotified", "GetUserStatus", body.toByteArray());
        JceOutputStream jceout = Utf8JceUtils.newOutputStream();
        toServiceMsg.writeTo(jceout);
        FromServiceMsg reply = doSendMsg(toServiceMsg);
        if (reply.getResultCode() != 0) {
            throw new IOException("RemoteError: " + reply.getResultCode() + ": " + reply.getErrorMsg());
        }
        GetUserStatusResp resp = new GetUserStatusResp();
        resp.readFrom(Utf8JceUtils.newInputStream(reply.getBody()));
        return resp;
    }

    public static GetBugReportArgsResp doGetBugReportArgs() throws IOException {
        ToServiceMsg toServiceMsg = new ToServiceMsg("NAuth.QNotified", "GetBugReportArgs", Utf8JceUtils.NO_DATA);
        JceOutputStream jceout = Utf8JceUtils.newOutputStream();
        toServiceMsg.writeTo(jceout);
        FromServiceMsg reply = doSendMsg(toServiceMsg);
        if (reply.getResultCode() != 0) {
            throw new IOException("RemoteError: " + reply.getResultCode() + ": " + reply.getErrorMsg());
        }
        GetBugReportArgsResp resp = new GetBugReportArgsResp();
        resp.readFrom(Utf8JceUtils.newInputStream(reply.getBody()));
        return resp;
    }

    public static FromServiceMsg doSendMsg(ToServiceMsg msg) throws IOException {
        initSslContext();
        SSLSocket s = (SSLSocket) sslFactory.createSocket();
        s.connect(new InetSocketAddress("ioctl.cc"/*"192.168.12.93"*/, 8080), 5000);
        InputStream in;
        OutputStream out;
        try {
            in = s.getInputStream();
            out = s.getOutputStream();
            JceOutputStream jceout = Utf8JceUtils.newOutputStream();
            msg.writeTo(jceout);
            byte[] buf = jceout.toByteArray();
            writeBe32(out, buf.length);
            writeBe32(out, buf.length);
            out.write(buf);
            out.flush();
            int size = readBe32(in);
            int size2 = readBe32(in);
            if (size != size2) throw new IOException("size doesn't match " + size + "/" + size2);
            if (size > 1024 * 1024) throw new IOException("recv size too big: " + size);
            byte[] inbuf = new byte[size];
            int done = 0;
            int i;
            while (done < size && (i = in.read(inbuf, done, size - done)) > 0) {
                done += i;
            }
            out.close();
            in.close();
            s.close();
            if (done < size) throw new IOException("recv " + done + " less than expected " + size);
            FromServiceMsg fromServiceMsg = new FromServiceMsg();
            JceInputStream jin = Utf8JceUtils.newInputStream(inbuf);
            fromServiceMsg.readFrom(jin);
            return fromServiceMsg;
        } catch (Exception e) {
            try {
                s.close();
            } catch (IOException ignored) {
            }
            throw e;
        }
    }

    public static void writeBe32(OutputStream out, int i) throws IOException {
        out.write((i >>> 24) & 0xFF);
        out.write((i >>> 16) & 0xFF);
        out.write((i >>> 8) & 0xFF);
        out.write(i & 0xFF);
    }

    public static int readBe32(InputStream in) throws IOException {
        int ret = 0;
        int i;
        if ((i = in.read()) < 0) throw new EOFException();
        ret |= (i & 0xFF) << 24;
        if ((i = in.read()) < 0) throw new EOFException();
        ret |= (i & 0xFF) << 16;
        if ((i = in.read()) < 0) throw new EOFException();
        ret |= (i & 0xFF) << 8;
        if ((i = in.read()) < 0) throw new EOFException();
        ret |= (i & 0xFF);
        return ret;
    }
}
