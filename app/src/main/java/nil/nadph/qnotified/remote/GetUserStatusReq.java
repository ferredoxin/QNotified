/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 xenonhydride@gmail.com
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

public class GetUserStatusReq extends JceStruct {
    public long uin;

    public GetUserStatusReq() {
    }

    public GetUserStatusReq(byte[] b) {
        JceInputStream in = Utf8JceUtils.newInputStream(b);
        readFrom(in);
    }

    public GetUserStatusReq(long uin) {
        this.uin = uin;
    }

    @Override
    public void writeTo(JceOutputStream os) {
        os.write(uin, 0);
    }

    @Override
    public void readFrom(JceInputStream is) {
        uin = is.read(0L, 0, true);
    }
}
