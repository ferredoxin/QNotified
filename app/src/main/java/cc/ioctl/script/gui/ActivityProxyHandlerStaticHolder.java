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
package cc.ioctl.script.gui;

import de.robv.android.xposed.XC_MethodHook;
import cc.ioctl.script.api.RestrictedProxyParamList;
import nil.nadph.qnotified.util.DexMethodDescriptor;
import cc.ioctl.util.internal.XMethodHookDispatchUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ActivityProxyHandlerStaticHolder {

    public static final String TAG_ACTIVITY_PROXY_HANDLER = "qn_activity_proxy_handler";
    private static final ConcurrentHashMap<String, Map<String, XMethodHookDispatchUtil.HookHolder>> sList = new ConcurrentHashMap<>();

    public Map<String, XMethodHookDispatchUtil.HookHolder> createHandler(final Class<?> clazz, RestrictedProxyParamList paramList) throws NoSuchMethodException {
        Objects.requireNonNull(clazz, "class == null");
        Objects.requireNonNull(paramList, "param == null");
        Map<String, XMethodHookDispatchUtil.HookHolder> result = new HashMap<>();
        HashMap<String, Method> overridableMethods = new HashMap<>();
        Class cl = clazz;
        do {
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
            if (m == null) throw new NoSuchMethodException(nameSig + " in " + clazz.getName() + " and its superclass");
            result.put(nameSig, new XMethodHookDispatchUtil.HookHolder(h.getValue(), m));
        }
        return result;
    }

    public static String offer(Map<String, XMethodHookDispatchUtil.HookHolder> param) {
        String k = UUID.randomUUID().toString();
        sList.put(k, Objects.requireNonNull(param));
        return k;
    }

    public static Map<String, XMethodHookDispatchUtil.HookHolder> consume(String k) {
        return sList.remove(k);
    }
}
