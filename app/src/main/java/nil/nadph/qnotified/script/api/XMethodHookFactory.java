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

import android.annotation.SuppressLint;

import java.lang.reflect.Member;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XCallback;

@SuppressLint("UnknownNullness")
public class XMethodHookFactory {

    /**
     * Create a XC_MethodHook with default priority 50.
     * See {@link #create(XC_MethodHookImpl, int)}
     *
     * @param impl The actual method hook callback
     * @return An XC_MethodHook object you can use with {@link XposedBridge#hookMethod(Member, XC_MethodHook)}.
     */
    public static XC_MethodHook create(final XC_MethodHookImpl impl) {
        return create(impl, 50);
    }

    /**
     * This method is meant to be used for BeanShell scripts.
     * BeanShell cannot create dex used on Android platform, and the Proxy mechanism only support interface.
     * You can invoke this method to hook a method in script.
     * **WARN: USE WITH CATION!**
     * **Interpreting is time-consuming and may dramatically slow down your application!**
     *
     * @param impl     The actual method hook callback
     * @param priority See {@link XCallback#priority}.
     * @return An XC_MethodHook object you can use with {@link XposedBridge#hookMethod(Member, XC_MethodHook)}.
     */
    public static XC_MethodHook create(final XC_MethodHookImpl impl, final int priority) {
        if (impl == null) throw new NullPointerException("hookImpl == null");
        return new XC_MethodHook(priority) {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                impl.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                impl.afterHookedMethod(param);
            }
        };
    }
}
