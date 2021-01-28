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
import me.singleneuron.data.BugReportArguments;
import nil.nadph.qnotified.util.NonNull;

public class GetBugReportArgsResp extends JceStruct {

    private static final BugReportArguments[] DUMMY_ARGS_ARRAY = new BugReportArguments[]{new BugReportArguments()};

    @NonNull
    @JceId(0)
    public BugReportArguments[] args = DUMMY_ARGS_ARRAY;

    public GetBugReportArgsResp() {
    }

    @Override
    public void writeTo(JceOutputStream os) {
        os.write(args, 0);
    }

    @Override
    public void readFrom(JceInputStream is) {
        args = is.readArray(DUMMY_ARGS_ARRAY, 0, true);
    }
}
