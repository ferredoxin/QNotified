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
package com.rymmmmm.hook;

import cc.ioctl.dialog.RikkaCustomDeviceModelDialog;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Field;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.Utils;

//自定义机型
@FunctionEntry
public class CustomDeviceModel extends CommonDelayableHook {

    public static final CustomDeviceModel INSTANCE = new CustomDeviceModel();

    protected CustomDeviceModel() {
        super("__NOT_USED__", SyncUtils.PROC_ANY);
    }

    @Override
    public boolean initOnce() {
        try {
            Class<?> Clz = Initiator.load("android.os.Build");
            Field manufacturer = XposedHelpers.findField(Clz, "MANUFACTURER");
            Field model = XposedHelpers.findField(Clz, "MODEL");
            manufacturer.setAccessible(true);
            model.setAccessible(true);
            manufacturer.set(Clz.newInstance(),
                RikkaCustomDeviceModelDialog.getCurrentDeviceManufacturer());
            model.set(Clz.newInstance(), RikkaCustomDeviceModelDialog.getCurrentDeviceModel());
            return true;
        } catch (Throwable e) {
            Utils.log(e);
            return false;
        }
    }

    @Override
    public boolean isEnabled() {
        return RikkaCustomDeviceModelDialog.IsEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        //not supported.
    }
}
