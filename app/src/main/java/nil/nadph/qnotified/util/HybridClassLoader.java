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
package nil.nadph.qnotified.util;

import android.content.Context;

import java.net.URL;

public class HybridClassLoader extends ClassLoader {

    private final ClassLoader clPreload;
    private final ClassLoader clBase;
    private static final ClassLoader sBootClassLoader = Context.class.getClassLoader();

    public HybridClassLoader(ClassLoader x, ClassLoader ctx) {
        clPreload = x;
        clBase = ctx;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return sBootClassLoader.loadClass(name);
        } catch (ClassNotFoundException ignored) {
        }
        if (name != null && (name.startsWith("androidx.") || name.startsWith("android.support.v4.")
                || name.startsWith("kotlin.") || name.startsWith("kotlinx."))) {
            //Nevertheless, this will not interfere with the host application,
            //classes in host application SHOULD find with their own ClassLoader, eg Class.forName()
            //use shipped androidx and kotlin lib.
            throw new ClassNotFoundException(name);
        }
        //The ClassLoader for XPatch is terrible, XposedBridge.class.getClassLoader() == Context.getClassLoader(),
        //which mess up with my kotlin lib.
        if (clPreload != null) {
            try {
                return clPreload.loadClass(name);
            } catch (ClassNotFoundException ignored) {
            }
        }
        if (clBase != null) {
            try {
                return clBase.loadClass(name);
            } catch (ClassNotFoundException ignored) {
            }
        }
        throw new ClassNotFoundException(name);
    }

    @Override
    public URL getResource(String name) {
        URL ret = clPreload.getResource(name);
        if (ret != null) return ret;
        return clBase.getResource(name);
    }
}
