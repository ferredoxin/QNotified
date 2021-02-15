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

import java.io.IOException;
import java.util.HashSet;

import nil.nadph.qnotified.BuildConfig;
import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.activity.EulaActivity;
import nil.nadph.qnotified.chiral.MdlMolParser;
import nil.nadph.qnotified.chiral.Molecule;
import nil.nadph.qnotified.config.ConfigManager;

import static nil.nadph.qnotified.util.Utils.log;
import static nil.nadph.qnotified.util.Utils.showErrorToastAnywhere;

public class LicenseStatus {
    public static final String qn_eula_status = "qh_eula_status";//typo, ignore it
    public static final String qn_auth2_molecule = "qn_auth2_molecule";
    public static final String qn_auth2_chiral = "qn_auth2_chiral";

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
            showErrorToastAnywhere(e.toString());
        }
    }

    public static boolean hasEulaUpdated() {
        int s = getEulaStatus();
        return (s != 0 && s != EulaActivity.CURRENT_EULA_VERSION);
    }

    public static boolean hasUserAcceptEula() {
        return getEulaStatus() == EulaActivity.CURRENT_EULA_VERSION;
    }

    /**
     * No longer true, but keep it here.
     */
    public static final boolean sDisableCommonHooks = false;

    public static boolean isAsserted() {
        return BuildConfig.DEBUG;
    }

    public static boolean isInsider() {
        return BuildConfig.DEBUG;
    }

}
