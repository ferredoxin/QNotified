/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.remote;

import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;
import com.qq.taf.jce.JceStruct;

import java.io.IOException;

public class Utf8JceUtils {

    public static final String[] DUMMY_STRING_ARRAY = new String[]{""};
    public static final byte[] NO_DATA = new byte[0];

    public static <T extends JceStruct> T decodeJceStruct(T struct, byte[] bs) throws IOException {
        JceInputStream is = newInputStream(bs);
        struct.readFrom(is);
        return struct;
    }

    public static byte[] encodeJceStruct(JceStruct struct) throws IOException {
        JceOutputStream os = newOutputStream();
        struct.writeTo(os);
        return os.toByteArray();
    }

    public static JceOutputStream newOutputStream() {
        JceOutputStream is = new JceOutputStream();
        is.setServerEncoding("UTF-8");
        return is;
    }

    public static JceInputStream newInputStream(byte[] bs) {
        JceInputStream os = new JceInputStream(bs);
        os.setServerEncoding("UTF-8");
        return os;
    }
}
