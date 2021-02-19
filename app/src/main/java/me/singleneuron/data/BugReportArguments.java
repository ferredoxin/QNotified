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
package me.singleneuron.data;

import androidx.annotation.NonNull;
import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;
import com.qq.taf.jce.JceStruct;
import nil.nadph.qnotified.remote.JceId;
import nil.nadph.qnotified.remote.Utf8JceUtils;

public class BugReportArguments extends JceStruct {

    @NonNull
    @JceId(0)
    public String key = ""; //must be [a-zA-Z0-9_]{1,63}
    @NonNull
    @JceId(1)
    public String name = ""; //Chinese, display to user
    @NonNull
    @JceId(2)
    public String description = "";
    @NonNull
    @JceId(3)
    public String[] choices = Utf8JceUtils.DUMMY_STRING_ARRAY;

    @Override
    public void writeTo(JceOutputStream os) {
        os.write(key, 0);
        os.write(name, 1);
        os.write(description, 2);
        os.write(choices, 3);
    }

    @Override
    public void readFrom(JceInputStream is) {
        key = is.read("", 0, true);
        name = is.read("", 1, true);
        description = is.read("", 2, true);
        choices = is.read(Utf8JceUtils.DUMMY_STRING_ARRAY, 3, true);
    }
}
