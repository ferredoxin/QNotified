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

import java.lang.reflect.Field;

import static nil.nadph.qnotified.util.Utils.log;

public class CustomMenu {

    public static Object createItem(Class<?> clazz, int id, String title) {
        try {
            Object item = clazz.newInstance();
            Field f;
            f = Utils.findField(clazz, int.class, "id");
            if (f == null) f = Utils.findField(clazz, int.class, "a");
            f.setAccessible(true);
            f.set(item, id);
            f = Utils.findField(clazz, String.class, "title");
            if (f == null) f = Utils.findField(clazz, String.class, "a");
            f.setAccessible(true);
            f.set(item, title);
            return item;
        } catch (Exception e) {
            log(e);
            return null;
        }
    }
}
