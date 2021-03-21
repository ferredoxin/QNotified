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
package nil.nadph.qnotified.bridge;

import static nil.nadph.qnotified.util.Initiator._QQAppInterface;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_static_declared_ordinal_modifier;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_virtual_declared_ordinal;
import static nil.nadph.qnotified.util.Utils.getQQAppInterface;
import static nil.nadph.qnotified.util.Utils.getTroopManager;
import static nil.nadph.qnotified.util.Utils.log;

import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Modifier;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Initiator;

public class ContactUtils {

    public static String getTroopMemberNick(String troopUin, String memberUin) {
        if (troopUin != null && troopUin.length() > 0) {
            try {
                Object mTroopManager = getTroopManager();
                Object troopMemberInfo = invoke_virtual_declared_ordinal(mTroopManager, 0, 3, false,
                    troopUin, memberUin, String.class, String.class, Initiator._TroopMemberInfo());
                if (troopMemberInfo != null) {
                    String troopnick = (String) XposedHelpers
                        .getObjectField(troopMemberInfo, "troopnick");
                    if (troopnick != null) {
                        String ret = troopnick.replaceAll("\\u202E", "");
                        if (ret.trim().length() > 0) {
                            return ret;
                        }
                    }
                }
            } catch (Throwable e) {
                log(e);
            }
            try {
                String ret;//getDiscussionMemberShowName
                String nickname = (String) invoke_static_declared_ordinal_modifier(
                    DexKit.doFindClass(DexKit.C_CONTACT_UTILS),
                    2, 10, false, Modifier.PUBLIC, 0,
                    getQQAppInterface(), troopUin, memberUin, _QQAppInterface(), String.class,
                    String.class);
                if (nickname != null
                    && (ret = nickname.replaceAll("\\u202E", "")).trim().length() > 0) {
                    return ret;
                }
            } catch (Throwable e) {
                log(e);
            }
        }
        try {
            String ret;//getBuddyName
            String nickname = null;
            try {
                nickname = (String) invoke_static_declared_ordinal_modifier(
                    DexKit.doFindClass(DexKit.C_CONTACT_UTILS), 1, 3, true, Modifier.PUBLIC, 0,
                    getQQAppInterface(), memberUin, true, _QQAppInterface(), String.class,
                    boolean.class, String.class);
            } catch (Throwable e2) {
                try {
                    nickname = (String) invoke_static_declared_ordinal_modifier(
                        DexKit.doFindClass(DexKit.C_CONTACT_UTILS), 1, 4, true, Modifier.PUBLIC, 0,
                        getQQAppInterface(), memberUin, true, _QQAppInterface(), String.class,
                        boolean.class, String.class);
                } catch (Throwable e3) {
                    try {
                        nickname = (String) invoke_static_declared_ordinal_modifier(
                            DexKit.doFindClass(DexKit.C_CONTACT_UTILS), 1, 2, false,
                            Modifier.PUBLIC,
                            0,
                            getQQAppInterface(), memberUin, true,
                            load("com.tencent.common.app.AppInterface"), String.class,
                            boolean.class, String.class);
                    } catch (Throwable e4) {
                        e2.addSuppressed(e3);
                        e2.addSuppressed(e4);
                        log(e2);
                    }
                }
            }
            if (nickname != null
                && (ret = nickname.replaceAll("\\u202E", "")).trim().length() > 0) {
                return ret;
            }
        } catch (Throwable e) {
            log(e);
        }
        //**sigh**
        return memberUin;
    }
}
