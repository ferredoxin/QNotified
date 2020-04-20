package nil.nadph.qnotified.bridge;

import android.content.Context;
import android.os.Parcelable;
import com.tencent.mobileqq.app.QQAppInterface;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.util.DexKit;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static nil.nadph.qnotified.util.Initiator._SessionInfo;
import static nil.nadph.qnotified.util.Utils.log;

public class ChatActivityFacade {
    public static long[] sendMessage(QQAppInterface qQAppInterface, Context context, Parcelable sessionInfo, String msg,
                                     ArrayList<?> atInfo, Object sendMsgParams) {
        Class facade = DexKit.doFindClass(DexKit.C_FACADE);
        Class SendMsgParams = null;
        Method m = null;
        for (Method mi : facade.getDeclaredMethods()) {
            if (!mi.getReturnType().equals(long[].class)) continue;
            Class[] argt = mi.getParameterTypes();
            if (argt.length != 6) continue;
            if (argt[1].equals(Context.class) && argt[2].equals(_SessionInfo())
                    && argt[3].equals(String.class) && argt[4].equals(ArrayList.class)) {
                m = mi;
                m.setAccessible(true);
                SendMsgParams = argt[5];
                break;
            }
        }
        try {
            return (long[]) m.invoke(null, qQAppInterface, context, sessionInfo, msg, atInfo, sendMsgParams);
        } catch (Exception e) {
            log(e);
            return null;
        }
    }

    public static long[] sendMessage(QQAppInterface qQAppInterface, Context context, Parcelable sessionInfo, String msg) {
        Class facade = DexKit.doFindClass(DexKit.C_FACADE);
        Class SendMsgParams = null;
        Method m = null;
        for (Method mi : facade.getDeclaredMethods()) {
            if (!mi.getReturnType().equals(long[].class)) continue;
            Class[] argt = mi.getParameterTypes();
            if (argt.length != 6) continue;
            if (argt[1].equals(Context.class) && argt[2].equals(_SessionInfo())
                    && argt[3].equals(String.class) && argt[4].equals(ArrayList.class)) {
                m = mi;
                m.setAccessible(true);
                SendMsgParams = argt[5];
                break;
            }
        }
        try {
            return (long[]) m.invoke(null, qQAppInterface, context, sessionInfo, msg, new ArrayList<>(), SendMsgParams.newInstance());
        } catch (Exception e) {
            log(e);
            return null;
        }
    }

    public static long sendPttMessage(QQAppInterface qqAppInterface, Parcelable sessionInfo, String pttPath) {

        
        return (long) XposedHelpers.callStaticMethod(DexKit.doFindClass(DexKit.C_FACADE), "a", qqAppInterface, sessionInfo, pttPath);
    }
}
