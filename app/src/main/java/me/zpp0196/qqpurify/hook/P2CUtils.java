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

package me.zpp0196.qqpurify.hook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import nil.nadph.qnotified.config.AbstractConfigItem;
import nil.nadph.qnotified.hook.AbsDelayableHook;

public class P2CUtils {

    @Nullable
    private static AbstractConfigItem doFindConfigByName(String name) {
        return null;
    }

    @Nullable
    private static AbsDelayableHook doFindHookByName(String name) {
        for (AbsDelayableHook h : AbsDelayableHook.queryDelayableHooks()) {
            if (h.getClass().getSimpleName().equals(name)) {
                return h;
            }
        }
        return null;
    }

    @Nullable
    public static AbstractConfigItem findConfigByName(@NonNull String name) {
        //noinspection Nullability failsafe, ok?
        if (name == null || name.isEmpty()) {
            return null;
        }
        if (name.contains("$")) {
            name = name.split("\\$")[0];
        }
        AbstractConfigItem item = doFindConfigByName(name);
        if (item == null) {
            item = doFindHookByName(name);
        }
        return item;
    }
}
