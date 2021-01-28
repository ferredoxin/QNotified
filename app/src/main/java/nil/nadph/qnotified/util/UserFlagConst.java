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
package nil.nadph.qnotified.util;

public class UserFlagConst {
    public static final int BF_REJECT = 1;
    public static final int BF_SILENT_GONE = 1 << 1;
    public static final int BF_SILENT_DISABLE_LOAD = 1 << 2;
    public static final int BF_HIDE_INFO = 1 << 3;
    public static final int BF_TAMPER_BATCH_LONG_MSG = 1 << 4;
    public static final int BF_TAMPER_STARTUP_RANDOM = 1 << 5;
    public static final int BF_TAMPER_LIFECYCLE = 1 << 6;

    public static final int BF_FUNC_STICKY = 1 << 30;

    public static final int WF_NICE_USER = 1;
    public static final int WF_BYPASS_AUTH_2 = 1 << 3;
    public static final int WF_ASSERTED = 1 << 16;
    public static final int WF_INSIDER = 1 << 17;

    public static final int WF_FUNC_STICKY = 1 << 30;
}
