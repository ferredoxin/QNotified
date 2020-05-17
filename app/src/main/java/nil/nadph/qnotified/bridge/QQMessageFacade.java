package nil.nadph.qnotified.bridge;

import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.Utils;

import java.lang.reflect.Modifier;

import static nil.nadph.qnotified.util.Utils.*;

public class QQMessageFacade {

    public static Object get() {
        try {
            return Utils.invoke_virtual_any(Utils.getQQAppInterface(), Initiator._QQMessageFacade());
        } catch (Exception e) {
            log("QQMessageFacade.get() failed!");
            log(e);
            return null;
        }
    }

    public static Object getMessageManager(int istroop) {
        try {
            return Utils.invoke_virtual_declared_modifier_any(get(), Modifier.PUBLIC, 0, istroop, int.class, Initiator._BaseMessageManager());
        } catch (Exception e) {
            log("QQMessageFacade.getMessageManager() failed!");
            log(e);
            return null;
        }
    }

    public static void revokeMessage(Object msg) {
        if (msg == null) throw new NullPointerException("msg == null");
        int istroop = (int) iget_object_or_null(msg, "istroop");
        //if (istroop != 0) throw new IllegalArgumentException("istroop(" + istroop + ") is not supported");
        Object mgr = getMessageManager(istroop);
        try {
            Object msg2 = invoke_static(Initiator.load("azaf"), "a", msg, Initiator._MessageRecord(), Initiator._MessageRecord());
            long t = (long) iget_object_or_null(msg2, "time");
            t -= 1 + 10f * Math.random();
            iput_object(msg2, "time", t);
            Object ayzl = invoke_virtual(getQQAppInterface(), "a", Initiator.load("ayzl"));
            invoke_virtual(ayzl, "b", true, boolean.class, void.class);
            invoke_virtual_declared_fixed_modifier_ordinal(mgr, Modifier.PUBLIC, 0, Initiator._BaseMessageManager(), 2, 4, true, msg2, Initiator._MessageRecord(), void.class);
        } catch (Exception e) {
            log("revokeMessage failed");
            log(e);
        }
    }
}
