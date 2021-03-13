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
package cc.ioctl;

import androidx.annotation.NonNull;
import de.robv.android.xposed.XC_MethodHook;

@SuppressWarnings("JavaJniMissingFunction")
public final class NativeHookCallback extends XC_MethodHook {

    public final long a;

    public NativeHookCallback(long a, int b) {
        super(b);
        this.a = a;
    }

    @Override
    protected native void beforeHookedMethod(@NonNull MethodHookParam param) throws Throwable;

    @Override
    protected native void afterHookedMethod(@NonNull MethodHookParam param) throws Throwable;
}
