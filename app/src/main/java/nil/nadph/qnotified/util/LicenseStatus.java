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

import nil.nadph.qnotified.chiral.MdlMolParser;
import nil.nadph.qnotified.chiral.Molecule;
import nil.nadph.qnotified.config.ConfigManager;

import java.io.IOException;
import java.util.HashSet;

import static nil.nadph.qnotified.util.Utils.log;

public class LicenseStatus {
    public static final String qn_eula_status = "qh_eula_status";//spelling mistake, ignore it
    public static final String qn_auth2_molecule = "qn_auth2_molecule";
    public static final String qn_auth2_chiral = "qn_auth2_chiral";

    public static final int STATUS_DEFAULT = 0;
    public static final int STATUS_ACCEPT = 1;
    public static final int STATUS_DENIAL = 2;

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
            Utils.showErrorToastAnywhere(e.toString());
        }
    }

    public static boolean getAuth2Status() {
        return getAuth2Chiral() != null && getAuth2Molecule() != null;
    }

    public static int[] getAuth2Chiral() {
        if (mAuth2Chiral == null) {
            try {
                String chirals = ConfigManager.getDefaultConfig().getString(qn_auth2_chiral);
                if (chirals != null && chirals.length() > 0) {
                    HashSet<Integer> ch = new HashSet<>();
                    for (String s : chirals.split(",")) {
                        if (s.length() > 0) ch.add(Integer.parseInt(s));
                    }
                    mAuth2Chiral = Utils.integerSetToArray(ch);
                }
            } catch (Exception e) {
                log(e);
            }
        }
        return mAuth2Chiral;
    }

    public static Molecule getAuth2Molecule() {
        if (mAuth2Mol == null) {
            try {
                String mdlmol = ConfigManager.getDefaultConfig().getString(qn_auth2_molecule);
                if (mdlmol != null && mdlmol.length() > 0) {
                    mAuth2Mol = MdlMolParser.parseString(mdlmol);
                }
            } catch (Exception e) {
                log(e);
            }
        }
        return mAuth2Mol;
    }

    public static void setAuth2Status(Molecule mol, int[] chiral) {
        mAuth2Mol = mol;
        mAuth2Chiral = chiral;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chiral.length; i++) {
            if (i != 0) sb.append(',');
            sb.append(chiral[i]);
        }
        ConfigManager.getDefaultConfig().putString(qn_auth2_molecule, mol.toMdlMolString());
        ConfigManager.getDefaultConfig().putString(qn_auth2_chiral, sb.toString());
    }

    public static boolean hasUserAgreeEula() {
        return getEulaStatus() == STATUS_ACCEPT;
    }
}
