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
package cc.ioctl.script.gui;

import cc.ioctl.script.api.RestrictedProxyParamList;
import cc.ioctl.util.internal.XMethodHookDispatchUtil;
import de.robv.android.xposed.XC_MethodHook;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import nil.nadph.qnotified.util.DexMethodDescriptor;

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

    public Map<String, XMethodHookDispatchUtil.HookHolder> createHandler(final Class<?> clazz,
        RestrictedProxyParamList paramList) throws NoSuchMethodException {
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
            if (m == null) {
                throw new NoSuchMethodException(
                    nameSig + " in " + clazz.getName() + " and its superclass");
            }
            result.put(nameSig, new XMethodHookDispatchUtil.HookHolder(h.getValue(), m));
        }
        return result;
    }
}
