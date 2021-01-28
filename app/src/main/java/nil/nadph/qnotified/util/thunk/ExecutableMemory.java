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
package nil.nadph.qnotified.util.thunk;

import nil.nadph.qnotified.util.Natives;

public class ExecutableMemory {

    public static long allocate(int size) {
        int ps = Natives.getpagesize();
        if (size > ps) {
            throw new OutOfMemoryError("cannot allocate " + size + ", while page size is " + ps);
        }
        throw new RuntimeException("Stub!");
    }

    public static void free(long p) {
        throw new RuntimeException("Stub!");
    }
}
