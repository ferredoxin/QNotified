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
package com.rymmmmm.hook;

import java.lang.reflect.Field;

import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import cc.ioctl.dialog.RikkaCustomDeviceModelDialog;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.Utils;

//自定义机型
public class CustomDeviceModel extends CommonDelayableHook {
    private static final CustomDeviceModel self = new CustomDeviceModel();

    public static CustomDeviceModel get() {
        return self;
    }

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
            manufacturer.set(Clz.newInstance(), RikkaCustomDeviceModelDialog.getCurrentDeviceManufacturer());
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
