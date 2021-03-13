/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
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

import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;
import com.qq.taf.jce.JceStruct;
import java.io.IOException;
import java.util.Random;

public class ToServiceMsg extends JceStruct {

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final Random r = new Random();
    private int uniSeq;//0
    private String serviceName = "";//1
    private String serviceCmd = "";//2
    private long token;//3
    private byte[] body = EMPTY_BYTE_ARRAY;//4

    public ToServiceMsg() {
        uniSeq = r.nextInt();
    }

    public ToServiceMsg(String name, String cmd, JceStruct struct) throws IOException {
        uniSeq = r.nextInt();
        JceOutputStream jout = Utf8JceUtils.newOutputStream();
        struct.writeTo(jout);
        body = jout.toByteArray();
        serviceName = name;
        serviceCmd = cmd;
    }

    public ToServiceMsg(String name, String cmd, byte[] b) {
        this(name, cmd, b, 0L);
    }

    public ToServiceMsg(String name, String cmd, byte[] b, long t) {
        uniSeq = r.nextInt();
        serviceName = name;
        serviceCmd = cmd;
        body = b;
        token = t;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public long getToken() {
        return token;
    }

    public void setToken(long token) {
        this.token = token;
    }

    public int getUniSeq() {
        return uniSeq;
    }

    public void setUniSeq(int uniSeq) {
        this.uniSeq = uniSeq;
    }

    public String getServiceCmd() {
        return serviceCmd;
    }

    public void setServiceCmd(String serviceCmd) {
        this.serviceCmd = serviceCmd;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public void writeTo(JceOutputStream os) {
        os.write(uniSeq, 0);
        os.write(serviceName, 1);
        os.write(serviceCmd, 2);
        os.write(token, 3);
        os.write(body, 4);
    }

    @Override
    public void readFrom(JceInputStream is) {
        uniSeq = is.read(0, 0, true);
        serviceName = is.readString(1, true);
        serviceCmd = is.readString(2, true);
        token = is.read(0L, 3, false);
        body = is.read(EMPTY_BYTE_ARRAY, 4, true);
    }

    public void ensureNonNull() {
        if (body == null) {
            body = EMPTY_BYTE_ARRAY;
        }
        if (serviceCmd == null) {
            serviceCmd = "";
        }
        if (serviceName == null) {
            serviceName = "";
        }
    }
}
