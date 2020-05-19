/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 cinit@github.com
 * https://github.com/cinit/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.util;

import nil.nadph.qnotified.config.ConfigManager;

import java.io.IOException;

import static nil.nadph.qnotified.util.Utils.log;

public class LicenseStatus {
    public static final String qh_eula_status = "qh_eula_status";

    public static final int STATUS_DEFAULT = 0;
    public static final int STATUS_ACCEPT = 1;
    public static final int STATUS_DENIAL = 2;

    public static int getEulaStatus() {
        return ConfigManager.getDefaultConfig().getIntOrDefault(qh_eula_status, 0);
    }

    public static void setEulaStatus(int status) {
        ConfigManager.getDefaultConfig().putInt(qh_eula_status, status);
        try {
            ConfigManager.getDefaultConfig().save();
        } catch (IOException e) {
            log(e);
            Utils.showErrorToastAnywhere(e.toString());
        }
    }

    public static boolean isAdvancedUser() {
        return false;
    }

    public static boolean hasUserAgreeEula() {
        return getEulaStatus() == STATUS_ACCEPT;
    }

}
