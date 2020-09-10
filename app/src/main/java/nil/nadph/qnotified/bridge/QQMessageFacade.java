package nil.nadph.qnotified.bridge;

import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.Utils;
import me.singleneuron.util.QQVersion;

import java.lang.reflect.Modifier;

import static nil.nadph.qnotified.util.Utils.*;

public class QQMessageFacade {

    public static Object get() {
        try {
            return Utils.invoke_virtual_any(Utils.getQQAppInterface(), Initiator._QQMessageFacade());
        } catch (Exception e) {
            loge("QQMessageFacade.get() failed!");
            log(e);
            return null;
        }
    }

    public static Object getMessageManager(int istroop) {
        try {
            return Utils.invoke_virtual_declared_modifier_any(get(), Modifier.PUBLIC, 0, istroop, int.class, Initiator._BaseMessageManager());
        } catch (Exception e) {
            loge("QQMessageFacade.getMessageManager() failed!");
            log(e);
            return null;
        }
    }

    public static void revokeMessage(Object msg) throws Exception {
        if (msg == null) throw new NullPointerException("msg == null");
        int istroop = (int) iget_object_or_null(msg, "istroop");
        //if (istroop != 0) throw new IllegalArgumentException("istroop(" + istroop + ") is not supported");
        Object mgr = getMessageManager(istroop);
        try {
            Object msg2 = invoke_static(DexKit.doFindClass(DexKit.C_MSG_REC_FAC), "a", msg, Initiator._MessageRecord(), Initiator._MessageRecord());
            long t = (long) iget_object_or_null(msg2, "time");
            t -= 1 + 10f * Math.random();
            iput_object(msg2, "time", t);
            String fuckingMethod = "a";
            if (Utils.getHostVersionCode() >= QQVersion.QQ_8_4_8) fuckingMethod = "getMsgCache";
            Object msgCache = invoke_virtual(getQQAppInterface(), fuckingMethod, DexKit.doFindClass(DexKit.C_MessageCache));
            invoke_virtual(msgCache, "b", true, boolean.class, void.class);
            invoke_virtual_declared_fixed_modifier_ordinal(mgr, Modifier.PUBLIC, 0, Initiator._BaseMessageManager(), 2, 4, true, msg2, Initiator._MessageRecord(), void.class);
        } catch (Exception e) {
            loge("revokeMessage failed: " + msg);
            log(e);
            throw e;
        }
    }
//
//    private static class FindMessageRecordClass extends Step {
//
//        public static Class<?> getMessageRecordClass() {
//            String klass = null;
//            ConfigManager cache = ConfigManager.getCache();
//            int lastVersion = cache.getIntOrDefault(cache_avatar_long_click_listener_version_code, 0);
//            int version = getHostInfo(getApplication()).versionCode;
//            if (version == lastVersion) {
//                String name = cache.getString(cache_avatar_long_click_listener_class);
//                if (name != null && name.length() > 0) {
//                    klass = name;
//                }
//            }
//            Class<?> c = Initiator.load(klass);
//            if (c != null) return c;
//            Class<?> decl = Initiator.load("com/tencent/mobileqq/activity/aio/BaseBubbleBuilder");
//            if (decl == null) return null;
//            String fname = null;
//            for (Field f : decl.getDeclaredFields()) {
//                if (f.getType().equals(View.OnLongClickListener.class)) {
//                    fname = f.getName();
//                    break;
//                }
//            }
//            if (fname == null) {
//                log("getLongClickListenerClass: field name is null");
//                return null;
//            }
//            DexMethodDescriptor _init_ = null;
//            byte[] dex = DexKit.getClassDeclaringDex("Lcom/tencent/mobileqq/activity/aio/BaseBubbleBuilder;", new int[]{7, 11, 6});
//            for (DexMethodDescriptor m : DexFlow.getDeclaredDexMethods(dex, "Lcom/tencent/mobileqq/activity/aio/BaseBubbleBuilder;")) {
//                if ("<init>".equals(m.name)) {
//                    _init_ = m;
//                    break;
//                }
//            }
//            DexFieldDescriptor f = new DexFieldDescriptor("Lcom/tencent/mobileqq/activity/aio/BaseBubbleBuilder;",
//                    fname, DexMethodDescriptor.getTypeSig(View.OnLongClickListener.class));
//            try {
//                klass = DexFlow.guessNewInstanceType(dex, _init_, f);
//            } catch (Exception e) {
//                log(e);
//                return null;
//            }
//            if (klass != null && klass.startsWith("L")) {
//                klass = klass.replace('/', '.').substring(1, klass.length() - 1);
//                cache.putString(cache_avatar_long_click_listener_class, klass);
//                cache.putInt(cache_avatar_long_click_listener_version_code, version);
//                try {
//                    cache.save();
//                } catch (IOException e) {
//                    log(e);
//                }
//                return Initiator.load(klass);
//            }
//            return null;
//        }
//
//        @Override
//        public boolean step() {
//            return getLongClickListenerClass() != null;
//        }
//
//        @Override
//        public boolean isDone() {
//            try {
//                ConfigManager cache = ConfigManager.getCache();
//                int lastVersion = cache.getIntOrDefault(cache_avatar_long_click_listener_version_code, 0);
//                if (getHostInfo(getApplication()).versionCode != lastVersion) {
//                    return false;
//                }
//                String name = cache.getString(cache_avatar_long_click_listener_class);
//                return name != null && name.length() > 0;
//            } catch (Exception e) {
//                log(e);
//                return false;
//            }
//        }
//
//        @Override
//        public int getPriority() {
//            return 20;
//        }
//
//        @Override
//        public String getDescription() {
//            return "定位com/tencent/mobileqq/activity/aio/BaseBubbleBuilder$3";
//        }
//    }
}
