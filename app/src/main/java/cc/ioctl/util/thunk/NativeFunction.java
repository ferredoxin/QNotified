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
package cc.ioctl.util.thunk;

import androidx.annotation.NonNull;
import cc.ioctl.util.thunk.pcs.Convention;

public class NativeFunction {

    @NonNull
    public ThunkProcedureCall call(@NonNull Object... arg) {
        throw new RuntimeException("Stub!");
    }

    public static class Builder {

        private long addr;

        public Builder(Convention cc) {
            if (cc == null) {
                throw new NullPointerException("calling convention is null");
            }
        }

        public Builder setAddress(long a) {
            addr = a;
            return this;
        }
    }
}
