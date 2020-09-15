/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
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
package nil.nadph.qnotified.script.api;

import java.lang.reflect.Method;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import nil.nadph.qnotified.util.DexMethodDescriptor;

public class RestrictedProxyParamList {

    private final HashMap<String, XC_MethodHook> proxyCallbacks = new HashMap<>();

    public RestrictedProxyParamList() {
    }

    public RestrictedProxyParamList addMethod(Method method, XC_MethodHook hook) {
        proxyCallbacks.put(method.getName() + DexMethodDescriptor.getMethodTypeSig(method), hook);
        return this;
    }

    public RestrictedProxyParamList addMethod(Method method, XC_MethodHookImpl hook) {
        proxyCallbacks.put(method.getName() + DexMethodDescriptor.getMethodTypeSig(method), XMethodHookFactory.create(hook));
        return this;
    }

    public RestrictedProxyParamList removeMethod(Method method) {
        proxyCallbacks.remove(method.getName() + DexMethodDescriptor.getMethodTypeSig(method));
        return this;
    }

    public HashMap<String, XC_MethodHook> getProxyCallbacks() {
        return proxyCallbacks;
    }
}