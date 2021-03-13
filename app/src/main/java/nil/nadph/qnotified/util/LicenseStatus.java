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
package nil.nadph.qnotified.util;

import static nil.nadph.qnotified.util.Utils.log;

import cc.ioctl.chiral.Molecule;
import java.io.IOException;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.BuildConfig;
import nil.nadph.qnotified.activity.EulaActivity;
import nil.nadph.qnotified.config.ConfigManager;

public class LicenseStatus {

    public static final String qn_eula_status = "qh_eula_status";//typo, ignore it
    public static final String qn_auth2_molecule = "qn_auth2_molecule";
    public static final String qn_auth2_chiral = "qn_auth2_chiral";
    /**
     * No longer true, but keep it here.
     */
    public static final boolean sDisableCommonHooks = false;
    private static Molecule mAuth2Mol = null;
    private static int[] mAuth2Chiral = null;

    public static int getEulaStatus() {
        return ConfigManager.getDefaultConfig().getIntOrDefault(qn_eula_status, 0);
    }

    public static void setEulaStatus(int status) {
        ConfigManager.getDefaultConfig().putInt(qn_eula_status, status);
        try {
            ConfigManager.getDefaultConfig().save();
        } catch (IOException e) {
            log(e);
            Toasts.error(HostInformationProviderKt.getHostInfo().getApplication(), e.toString());
        }
    }

    public static boolean hasEulaUpdated() {
        int s = getEulaStatus();
        return (s != 0 && s != EulaActivity.CURRENT_EULA_VERSION);
    }

    public static boolean hasUserAcceptEula() {
        return getEulaStatus() == EulaActivity.CURRENT_EULA_VERSION;
    }

    public static boolean isAsserted() {
        return BuildConfig.DEBUG;
    }

    public static boolean isInsider() {
        return BuildConfig.DEBUG;
    }

}
