package nil.nadph.qnotified.script.gui;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

import de.robv.android.xposed.*;
import nil.nadph.qnotified.script.api.*;
import nil.nadph.qnotified.util.*;
import nil.nadph.qnotified.util.internal.*;

public final class ActivityProxyHandlerStaticHolder {
    
    public static final String TAG_ACTIVITY_PROXY_HANDLER = "qn_activity_proxy_handler";
    private static final ConcurrentHashMap<String, Map<String, XMethodHookDispatchUtil.HookHolder>> sList = new ConcurrentHashMap<>();
    
    public static String offer(Map<String, XMethodHookDispatchUtil.HookHolder> param) {
        String k = UUID.randomUUID().toString();
        sList.put(k, Objects.requireNonNull(param));
        return k;
    }
    
    public static Map<String, XMethodHookDispatchUtil.HookHolder> consume(String k) {
        return sList.remove(k);
    }
    
    public Map<String, XMethodHookDispatchUtil.HookHolder> createHandler(Class<?> clazz, RestrictedProxyParamList paramList) throws NoSuchMethodException {
        Objects.requireNonNull(clazz, "class == null");
        Objects.requireNonNull(paramList, "param == null");
        Map<String, XMethodHookDispatchUtil.HookHolder> result = new HashMap<>();
        HashMap<String, Method> overridableMethods = new HashMap<>();
        Class cl = clazz;
        do
        {
            for (Method m : cl.getDeclaredMethods()) {
                int modifier = m.getModifiers();
                if ((((Modifier.PUBLIC | Modifier.PROTECTED) & modifier) != 0)
                    && ((Modifier.STATIC & modifier) == 0)) {
                    DexMethodDescriptor desc = new DexMethodDescriptor(m);
                    String tag = desc.name + desc.signature;
                    if (!overridableMethods.containsKey(tag)) {
                        overridableMethods.put(tag, m);
                    }
                }
            }
            cl = cl.getSuperclass();
        } while (cl != null);
        for (Map.Entry<String, XC_MethodHook> h : paramList.getProxyCallbacks().entrySet()) {
            String nameSig = h.getKey();
            Method m = overridableMethods.get(nameSig);
            if (m == null) {
                throw new NoSuchMethodException(nameSig + " in " + clazz.getName() + " and its superclass");
            }
            result.put(nameSig, new XMethodHookDispatchUtil.HookHolder(h.getValue(), m));
        }
        return result;
    }
}
