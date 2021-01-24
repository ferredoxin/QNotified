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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;

import static nil.nadph.qnotified.util.Utils.logi;

/**
 * Handy utils used for debug/development env, not to use in production.
 */
public class DebugUtils {
    public static final XC_MethodHook dummyHook = new XC_MethodHook(200) {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
        }
    };
    public static final XC_MethodHook invokeRecord = new XC_MethodHook(200) {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws IllegalAccessException, IllegalArgumentException {
            Member m = param.method;
            StringBuilder ret = new StringBuilder(m.getDeclaringClass().getSimpleName() + "->" + ((m instanceof Method) ? m.getName() : "<init>") + "(");
            Class[] argt;
            if (m instanceof Method)
                argt = ((Method) m).getParameterTypes();
            else if (m instanceof Constructor)
                argt = ((Constructor) m).getParameterTypes();
            else argt = new Class[0];
            for (int i = 0; i < argt.length; i++) {
                if (i != 0) ret.append(",\n");
                ret.append(param.args[i]);
            }
            ret.append(")=").append(param.getResult());
            Utils.logi(ret.toString());
            ret = new StringBuilder("↑dump object:" + m.getDeclaringClass().getCanonicalName() + "\n");
            Field[] fs = m.getDeclaringClass().getDeclaredFields();
            for (int i = 0; i < fs.length; i++) {
                fs[i].setAccessible(true);
                ret.append(i < fs.length - 1 ? "├" : "↓").append(fs[i].getName()).append("=").append(Utils.en_toStr(fs[i].get(param.thisObject))).append("\n");
            }
            logi(ret.toString());
            Utils.dumpTrace();
        }
    };
    public static final XC_MethodHook invokeInterceptor = new XC_MethodHook(200) {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws IllegalAccessException, IllegalArgumentException {
            Member m = param.method;
            StringBuilder ret = new StringBuilder(m.getDeclaringClass().getSimpleName() + "->" + ((m instanceof Method) ? m.getName() : "<init>") + "(");
            Class[] argt;
            if (m instanceof Method)
                argt = ((Method) m).getParameterTypes();
            else if (m instanceof Constructor)
                argt = ((Constructor) m).getParameterTypes();
            else argt = new Class[0];
            for (int i = 0; i < argt.length; i++) {
                if (i != 0) ret.append(",\n");
                ret.append(param.args[i]);
            }
            ret.append(")=").append(param.getResult());
            Utils.logi(ret.toString());
            ret = new StringBuilder("↑dump object:" + m.getDeclaringClass().getCanonicalName() + "\n");
            Field[] fs = m.getDeclaringClass().getDeclaredFields();
            for (int i = 0; i < fs.length; i++) {
                fs[i].setAccessible(true);
                ret.append(i < fs.length - 1 ? "├" : "↓").append(fs[i].getName()).append("=").append(Utils.en_toStr(fs[i].get(param.thisObject))).append("\n");
            }
            logi(ret.toString());
            Utils.dumpTrace();
        }
    };


    //    public static ClassLoader targetLoader = null;
//
//    public static void startFakeString() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(3_000);
//                } catch (InterruptedException e) {
//                }
//                if (SyncUtils.getProcessType() != 1) return;
//                Map sHookedMethodCallbacks = (Map) sget_object(XposedBridge.class, "sHookedMethodCallbacks");
//                a:
//                for (Object ent : sHookedMethodCallbacks.entrySet()) {
//                    Object[] elem = (Object[]) iget_object_or_null(((Map.Entry) ent).getValue(), "elements");
//                    for (Object cb : elem) {
//                        Class hook = cb.getClass();
//                        if (hook.getName().contains(_clz_name_)) {
//                            targetLoader = hook.getClassLoader();
//                            break a;
////                            try {
////                                Class de = cl.loadClass(_decoder_.replace(" ", ""));
////                                Method[] ms = de.getDeclaredMethods();
////                                Method m = null;
////                                for (Method mi : ms) {
////                                    if (mi.getReturnType().equals(String.class)) {
////                                        m = mi;
////                                        m.setAccessible(true);
////                                    }
////                                }
////                                StringBuilder fout = new StringBuilder();
////                                for (int i = 0; i < 4000; i++) {
////                                    String ret = null;
////                                    try {
////                                        ret = (String) m.invoke(null, i);
////                                    } catch (Exception e) {
////                                        ret = null;
////                                    }
////                                    ret = Utils.en(ret);
////                                    fout.append(ret);
////                                    fout.append('\n');
////                                }
////                                FileOutputStream f2out = new FileOutputStream(_out_);
////                                f2out.write(fout.toString().getBytes());
////                                f2out.flush();
////                                f2out.close();
////                            } catch (Exception e) {
////                                log(e);
////                            }
//                        }
//                    }
//                }
//                Natives.load();
//                long loadAddr = -1;
//                try {
//                    FileInputStream fin = new FileInputStream("/proc/self/maps");
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
//                    String map;
//                    while ((map = reader.readLine()) != null) {
//                        if (map.contains(_so_name_)) {
//                            long start = Long.parseLong(map.split(" ")[0].split("-")[0], 16);
//                            if (start > 0) {
//                                if (loadAddr > 0) {
//                                    loadAddr = Math.min(loadAddr, start);
//                                } else {
//                                    loadAddr = start;
//                                }
//                            }
//                        }
//                    }
//                    fin.close();
//                    byte[] buf = new byte[64];
//                    if (loadAddr > 0) {
//                        int ps = Natives.getpagesize();
//                        long addr = loadAddr + offset;
//                        long delta = addr % ps;
//                        long pstart = addr - delta;
//                        int ret = Natives.mprotect(pstart, (int) (delta + 16), Natives.PROT_EXEC | Natives.PROT_WRITE | Natives.PROT_READ);
//                        Natives.mread(addr, 64, buf);
//                        byte[] patch = new byte[]{0x70, 0x47};
//                        Natives.mwrite(addr, 2, patch);
//                    }
//
//                    File tmpin = new File("/tmp/index1.txt");
//                    StringBuilder sb = new StringBuilder();
//                    if (tmpin.exists()) {
//
//
//                        reader = new BufferedReader(new InputStreamReader(new FileInputStream(tmpin)));
//                        while ((map = reader.readLine()) != null) {
//                            if (map.contains(",")) {
//                                String[] parts = map.split(",");
//                                String clz = parts[0];
//                                String name = parts[1];
//                                String str1 = parts[2];
//                                String str2 = parts[3];
//                                String ret = null;
//                                try {
//                                    Method m = targetLoader.loadClass(_decode_class_).getDeclaredMethod(_m_);
//                                    m.setAccessible(true);
//                                    ret = Utils.en((String) m.invoke(null,_argv_));
//                                } catch (Exception e) {
//                                    ret = e.toString();
//                                }
//                                sb.append(clz).append(',').append(name).append(',').append(ret).append('\n');
//                            }
//                        }
//                        reader.close();
//                    }
//                    File outFile = new File("/tmp/out" + Math.random() + ".txt");
//                    outFile.createNewFile();
//                    FileOutputStream fout = new FileOutputStream(outFile);
//                    fout.write(sb.toString().getBytes());
//                    fout.flush();
//                    fout.close();
//                } catch (Exception e) {
//                    log(e);
//                }
//
//            }
//        }).start();
//    }
}
