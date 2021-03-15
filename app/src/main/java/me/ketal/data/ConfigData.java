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

package me.ketal.data;

import android.os.Looper;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.util.Toasts;
import nil.nadph.qnotified.util.Utils;

public class ConfigData<T> {

    final String mKeyName;
    final ConfigManager mgr;

    public ConfigData(String keyName) {
        this(keyName, ConfigManager.getDefaultConfig());
    }

    public ConfigData(String keyName, ConfigManager manager) {
        mKeyName = keyName;
        mgr = manager;
    }

    public void remove() {
        try {
            mgr.remove(mKeyName);
        } catch (Exception e) {
            Utils.log(e);
        }
    }

    public T getValue() {
        try {
            return (T) mgr.getObject(mKeyName);
        } catch (Exception e) {
            Utils.log(e);
            return null;
        }
    }

    public void setValue(T value) {
        try {
            mgr.getAllConfig().put(mKeyName, value);
            mgr.save();
        } catch (Exception e) {
            Utils.log(e);
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Toasts.error(HostInformationProviderKt.getHostInfo().getApplication(), e + "");
            } else {
                SyncUtils.post(() -> Toasts
                    .error(HostInformationProviderKt.getHostInfo().getApplication(), e + ""));
            }
        }
    }

    public T getOrDefault(T def) {
        try {
            return (T) mgr.getOrDefault(mKeyName, def);
        } catch (Exception e) {
            Utils.log(e);
            return def;
        }
    }
}
