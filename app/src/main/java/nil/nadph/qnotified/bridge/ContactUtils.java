package nil.nadph.qnotified.bridge;

import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Initiator;

import java.lang.reflect.Modifier;

import static nil.nadph.qnotified.util.Initiator._QQAppInterface;
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
            String nickname = (String) invoke_static_declared_ordinal_modifier(DexKit.doFindClass(DexKit.C_CONTACT_UTILS), 1, 3, true, Modifier.PUBLIC, 0,
                    getQQAppInterface(), memberUin, true, _QQAppInterface(), String.class, boolean.class, String.class);
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
