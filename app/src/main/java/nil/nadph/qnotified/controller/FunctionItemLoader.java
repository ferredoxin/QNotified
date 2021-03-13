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

package nil.nadph.qnotified.controller;

import androidx.annotation.NonNull;
import java.util.HashMap;
import java.util.Map;
import nil.nadph.qnotified.hook.AbsDelayableHook;
import nil.nadph.qnotified.util.UnsupportedFunctionUsage;

// TODO: 21-2-22 Refactor AbsDelayableHook with IFunctionItemInterface
@UnsupportedFunctionUsage
public class FunctionItemLoader {

    public static final Map<String, String> sInitializationErrors = new HashMap<>();
    private static long sLoadBeginTime = 0L;
    private static long sLoadEndTime = 0L;
    private static AbsDelayableHook[] sFunctionItems = null;
    private FunctionItemLoader() {
        throw new AssertionError("No instance for you!");
    }

    @NonNull
    public static AbsDelayableHook[] enumerateFunctionItems() {
        /*
        if (sFunctionItems == null) {
            synchronized (FunctionItemLoader.class) {
                sLoadBeginTime = System.currentTimeMillis();
                HashSet<AbsDelayableHook> list = new HashSet<>(128);
                for (String cname : enumerateFunctionItemClassNames()) {
                    Class<?> clazz;
                    try {
                        try {
                            clazz = Class.forName(cname);
                        } catch (ClassNotFoundException e) {
                            sInitializationErrors.put(cname, e.toString());
                            continue;
                        }
                        if (IFunctionItemInterface.class.isAssignableFrom(clazz)) {
                            IFunctionItemInterface instance = null;
                            try {
                                Field field = clazz.getField("INSTANCE");
                                if ((field.getModifiers() & (Modifier.PUBLIC | Modifier.STATIC))
                                    != (Modifier.PUBLIC | Modifier.STATIC)) {
                                    continue;
                                }
                                instance = (IFunctionItemInterface) field.get(null);
                            } catch (NoSuchFieldException | IllegalAccessException ignored) {
                                //pass, not kotlin object
                            }
                            if (instance == null) {
                                try {
                                    Method method = clazz.getMethod("get");
                                    if ((method.getModifiers() & (Modifier.PUBLIC | Modifier.STATIC))
                                        != (Modifier.PUBLIC | Modifier.STATIC)) {
                                        continue;
                                    }
                                    instance = (IFunctionItemInterface) method.invoke(null);
                                } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException ignored) {
                                    //pass, not tradition get()
                                } catch (InvocationTargetException e) {
                                    sInitializationErrors.put(cname, e.getTargetException().toString());
                                }
                            }
                            if (instance == null) {
                                sInitializationErrors.put(cname, "Singleton getter not found for class " + cname
                                    + ", either a field `public static <T> INSTANCE` or a method `public static <T> get()` is required.");
                            } else {
                                if (instance instanceof AbsDelayableHook) {
                                    list.add((AbsDelayableHook) instance);
                                } else {
                                    sInitializationErrors.put(cname, cname + " does not derive from AbsDelayableHook and cannot be loaded, this is a bug, please fix it.");
                                }
                            }
                        } else {
                            sInitializationErrors.put(cname, cname + " does not derive from IFunctionItemInterface");
                        }
                    } catch (Throwable e) {
                        sInitializationErrors.put(cname, e.toString());
                    }
                }
                sFunctionItems = list.toArray(new AbsDelayableHook[0]);
                sLoadEndTime = System.currentTimeMillis();
                int delta = (int) (sLoadEndTime - sLoadBeginTime);
                Utils.logi("loaded function item count = " + sFunctionItems.length + ", cost = " + delta + ", errors = " + sInitializationErrors.size());
                for (Map.Entry<String, String> err : sInitializationErrors.entrySet()) {
                    Utils.loge(err.getKey() + ": " + err.getValue());
                }
            }
        }
         */
        return nil.nadph.qnotified.gen.AnnotatedFunctionItemList.getAnnotatedFunctionItemClassList()
            .toArray(new AbsDelayableHook[0]);
    }
/*
    @NonNull
    private static String[] enumerateFunctionItemClassNames() {
        List<String> list = nil.nadph.qnotified.gen.AnnotatedFunctionItemList.getAnnotatedFunctionItemClassList();
        return list.toArray(new String[0]);
    }
*/
}
