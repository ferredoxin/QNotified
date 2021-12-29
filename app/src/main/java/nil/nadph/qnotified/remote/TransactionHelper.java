/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */
package nil.nadph.qnotified.remote;

import static nil.nadph.qnotified.util.Utils.log;

import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.json.JSONObject;

public class TransactionHelper {

    private static final String TAG = "TransactionHelper";
    private static final String TKS_PASSWORD = "NAuth-v1";
    private static final String apiAddress = "https://api.qwq2333.top";
    private static SSLSocketFactory sslFactory;

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public static int getUserStatus(long uin) {
        try {
            URL url = new URL(apiAddress+"/user/query");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes("{\"uin\":"+uin+"}");

            os.flush();
            os.close();

            JSONObject resp = new JSONObject(convertInputStreamToString(conn.getInputStream()));
            if (resp.getInt("code") == 200) {
                return resp.getInt("status");
            } else {
                return -1;
            }
        } catch (Exception e) {
            log(e);
            return -1;
        }
    }

    public static void initSslContext() {
        if (sslFactory != null) {
            return;
        }
        try {
            KeyStore keyStore = KeyStore.getInstance("BKS"); // 访问keytool创建的Java密钥库
            InputStream keyStream = TransactionHelper.class.getClassLoader()
                .getResourceAsStream("assets/na_cert.bks");
            if (keyStream == null) {
                throw new RuntimeException("shipped bks not found!!!");
            }
            char[] keyStorePass = TKS_PASSWORD.toCharArray();  //证书密码
            keyStore.load(keyStream, keyStorePass);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);//保存服务端的授权证书
            SSLContext ssl_ctx = SSLContext.getInstance("TLSv1.2");
            TrustManager[] m = trustManagerFactory.getTrustManagers();
            ssl_ctx.init(null, m, new SecureRandom());
            sslFactory = ssl_ctx.getSocketFactory();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GetBugReportArgsResp doGetBugReportArgs() throws IOException {
        ToServiceMsg toServiceMsg = new ToServiceMsg("NAuth.QNotified", "GetBugReportArgs",
            Utf8JceUtils.NO_DATA);
        JceOutputStream jceout = Utf8JceUtils.newOutputStream();
        toServiceMsg.writeTo(jceout);
        FromServiceMsg reply = doSendMsg(toServiceMsg);
        if (reply.getResultCode() != 0) {
            throw new IOException(
                "RemoteError: " + reply.getResultCode() + ": " + reply.getErrorMsg());
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
            if (size != size2) {
                throw new IOException("size doesn't match " + size + "/" + size2);
            }
            if (size > 1024 * 1024) {
                throw new IOException("recv size too big: " + size);
            }
            byte[] inbuf = new byte[size];
            int done = 0;
            int i;
            while (done < size && (i = in.read(inbuf, done, size - done)) > 0) {
                done += i;
            }
            out.close();
            in.close();
            s.close();
            if (done < size) {
                throw new IOException("recv " + done + " less than expected " + size);
            }
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
        if ((i = in.read()) < 0) {
            throw new EOFException();
        }
        ret |= (i & 0xFF) << 24;
        if ((i = in.read()) < 0) {
            throw new EOFException();
        }
        ret |= (i & 0xFF) << 16;
        if ((i = in.read()) < 0) {
            throw new EOFException();
        }
        ret |= (i & 0xFF) << 8;
        if ((i = in.read()) < 0) {
            throw new EOFException();
        }
        ret |= (i & 0xFF);
        return ret;
    }
}
