/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/cinit/QNotified
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

import java.net.URL;

public class HybridClassLoader extends ClassLoader {

    private final ClassLoader clPreload;
    private final ClassLoader clBase;

    public HybridClassLoader(ClassLoader x, ClassLoader ctx) {
        clPreload = x;
        clBase = ctx;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return ClassLoader.getSystemClassLoader().loadClass(name);
        } catch (ClassNotFoundException ignored) {
        }
        if (name != null && (name.startsWith("androidx.") || name.startsWith("android.support.v4."))) {
            //Nevertheless, this will not interfere with the host application,
            //classes in host application SHOULD find with their own ClassLoader, eg Class.forName()
            //use shipped androidx.
            throw new ClassNotFoundException(name);
        }
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
