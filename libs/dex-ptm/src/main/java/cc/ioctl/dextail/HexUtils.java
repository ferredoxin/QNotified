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

package cc.ioctl.dextail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class HexUtils {

    private static final String hexChars = "0123456789ABCDEF";

    public static byte[] subByteArray(byte[] src, int index, int length) {
        byte[] ret = new byte[length];
        System.arraycopy(src, index, ret, 0, length);
        return ret;
    }

    public static String byteArrayToString(byte[] arr) {
        if (arr == null || arr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            int b = arr[i] & 0xff;
            sb.append(hexChars.charAt(b >> 4));
            sb.append(hexChars.charAt(b & 0xf));
            if (i != arr.length - 1) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }


    public static byte[] int2u4le(int i) {
        return new byte[]{(byte) i, (byte) (i >> 8), (byte) (i >> 16), (byte) (i >> 24)};
    }

    public static byte[] int2u2le(int i) {
        return new byte[]{(byte) i, (byte) (i >> 8)};
    }

    public static int readLe32(byte[] buf, int index) {
        return buf[index] & 0xFF | (buf[index + 1] << 8) & 0xff00
            | (buf[index + 2] << 16) & 0xff0000 | (buf[index + 3] << 24) & 0xff000000;
    }

    public static void writeLe32(byte[] buf, int index, int val) {
        buf[index] = (byte) (val);
        buf[index + 1] = (byte) (val >>> 8);
        buf[index + 2] = (byte) (val >>> 16);
        buf[index + 3] = (byte) (val >>> 24);
    }

    public static int readLe16(byte[] buf, int off) {
        return (buf[off] & 0xFF) | ((buf[off + 1] << 8) & 0xff00);
    }

    public static byte[] readFileData(String path) throws IOException {
        FileInputStream in = new FileInputStream(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int ii;
        byte[] buf = new byte[1024];
        while ((ii = in.read(buf)) != -1) {
            baos.write(buf, 0, ii);
        }
        in.close();
        return baos.toByteArray();
    }

    public static void writeFileData(String path, byte[] data) throws IOException {
        if (data == null) {
            throw new NullPointerException("data == null");
        }
        File f = new File(path);
        if (!f.exists()) {
            f.createNewFile();
        }
        FileOutputStream fout = new FileOutputStream(path);
        fout.write(data);
        fout.flush();
        fout.close();
    }

    public static boolean bytesEqu(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        if (a == b) {
            return true;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    public static byte[] getTimeAsByteArray() {
        byte[] buf = new byte[8];
        long cts = System.currentTimeMillis();
        HexUtils.writeLe32(buf, 0, (int) (cts));
        HexUtils.writeLe32(buf, 4, (int) (cts >>> 32));
        return buf;
    }
}
