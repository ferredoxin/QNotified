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
package nil.nadph.qnotified.bridge;

import java.lang.reflect.Modifier;

import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.DexKit;

import static nil.nadph.qnotified.util.Initiator._QQAppInterface;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_static_declared_ordinal_modifier;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_virtual_declared_ordinal;
import static nil.nadph.qnotified.util.Utils.*;

public class ContactUtils {
    
    public static String getTroopMemberNick(String troopUin, String memberUin) {
        if (troopUin != null && troopUin.length() > 0) {
            try {
                Object mTroopManager = getTroopManager();
                Object troopMemberInfo = invoke_virtual_declared_ordinal(mTroopManager, 0, 3, false,
                    troopUin, memberUin, String.class, String.class, Initiator._TroopMemberInfo());
                if (troopMemberInfo != null) {
                    String troopnick = (String) XposedHelpers.getObjectField(troopMemberInfo, "troopnick");
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
                String nickname = (String) invoke_static_declared_ordinal_modifier(DexKit.doFindClass(DexKit.C_CONTACT_UTILS),
                    2, 10, false, Modifier.PUBLIC, 0,
                    getQQAppInterface(), troopUin, memberUin, _QQAppInterface(), String.class, String.class);
                if (nickname != null && (ret = nickname.replaceAll("\\u202E", "")).trim().length() > 0) {
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
                nickname = (String) invoke_static_declared_ordinal_modifier(DexKit.doFindClass(DexKit.C_CONTACT_UTILS), 1, 3, true, Modifier.PUBLIC, 0,
                    getQQAppInterface(), memberUin, true, _QQAppInterface(), String.class, boolean.class, String.class);
            } catch (Throwable e2) {
                log(e2);
                try {
                    nickname = (String) invoke_static_declared_ordinal_modifier(DexKit.doFindClass(DexKit.C_CONTACT_UTILS), 1, 4, true, Modifier.PUBLIC, 0,
                        getQQAppInterface(), memberUin, true, _QQAppInterface(), String.class, boolean.class, String.class);
                } catch (Throwable e3) {
                    log(e3);
                }
            }
            if (nickname != null && (ret = nickname.replaceAll("\\u202E", "")).trim().length() > 0) {
                return ret;
            }
        } catch (Throwable e) {
            log(e);
        }
        //**sigh**
        return memberUin;
    }
}
