/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
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
package nil.nadph.qnotified.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import com.tencent.mmkv.MMKV;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import nil.nadph.qnotified.BuildConfig;
import nil.nadph.qnotified.startup.StartupInfo;

public class Natives {

    public static final int RTLD_LAZY = 0x00001;    /* Lazy function call binding.  */
    public static final int RTLD_NOW = 0x00002;    /* Immediate function call binding.  */
    public static final int RTLD_BINDING_MASK = 0x3;    /* Mask of binding time value.  */
    public static final int RTLD_NOLOAD = 0x00004;    /* Do not load the object.  */
    public static final int RTLD_DEEPBIND = 0x00008;    /* Use deep binding.  */
    /* If the following bit is set in the MODE argument to `dlopen',
       the symbols of the loaded object and its dependencies are made
       visible as if the object were linked directly into the program.  */
    public static final int RTLD_GLOBAL = 0x00100;
    /* Unix98 demands the following flag which is the inverse to RTLD_GLOBAL.
       The implementation does this by default and so we can define the
       value to zero.  */
    public static final int RTLD_LOCAL = 0;

    /* Do not delete object when closed.  */
    public static final int RTLD_NODELETE = 0x01000;

    public static final int PROT_READ = 0x1;        /* Page can be read.  */
    public static final int PROT_WRITE = 0x2;        /* Page can be written.  */
    public static final int PROT_EXEC = 0x4;        /* Page can be executed.  */
    public static final int PROT_NONE = 0x0;        /* Page can not be accessed.  */
    public static final int PROT_GROWSDOWN = 0x01000000;	/* Extend change to start of
					   growsdown vma (mprotect only).  */
    public static final int PROT_GROWSUP = 0x02000000;	/* Extend change to start of
					   growsup vma (mprotect only).  */

    private Natives() {
        throw new AssertionError("No instance for you!");
    }

    public static native void mwrite(long ptr, int len, byte[] buf, int offset);

    public static void mwrite(long ptr, int len, byte[] buf) {
        mwrite(ptr, len, buf, 0);
    }

    public static native void mread(long ptr, int len, byte[] buf, int offset);

    public static void mread(long ptr, int len, byte[] buf) {
        mread(ptr, len, buf, 0);
    }

    public static native long malloc(int size);

    public static native void free(long ptr);

    public static native void memcpy(long dest, long src, int num);

    public static native void memset(long addr, int c, int num);

    public static native int mprotect(long addr, int len, int prot);

    public static native long dlsym(long ptr, String symbol);

    public static native long dlopen(String filename, int flag);

    public static native int dlclose(long ptr);

    public static native String dlerror();

    public static native int sizeofptr();

    public static native int getpagesize();

    public static native long call(long addr);

    public static native long call(long addr, long argv);

    private static void registerNativeLibEntry(String soTailingName) {
        if (soTailingName == null || soTailingName.length() == 0) {
            return;
        }
        try {
            Class<?> xp = Class.forName("de.robv.android.xposed.XposedBridge");
            try {
                xp.getClassLoader()
                    .loadClass("org.lsposed.lspd.nativebridge.NativeAPI")
                    .getMethod("recordNativeEntrypoint", String.class)
                    .invoke(null, soTailingName);
            } catch (ClassNotFoundException ignored) {
                // not LSPosed, ignore
            } catch (NoSuchMethodException | IllegalArgumentException
                | InvocationTargetException | IllegalAccessException e) {
                Utils.log(e);
            }
        } catch (ClassNotFoundException e) {
            // not in host process, ignore
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("UnsafeDynamicallyLoadedCode")
    public static void load(Context ctx) throws Throwable {
        try {
            getpagesize();
            return;
        } catch (UnsatisfiedLinkError ignored) {
        }
        try {
            Class.forName("de.robv.android.xposed.XposedBridge");
            // in host process
            try {
                if (StartupInfo.modulePath != null) {
                    // try direct memory map
                    System.load(StartupInfo.modulePath
                        + "!/lib/" + Build.CPU_ABI + "/libnatives.so");
                    Utils.logd("dlopen by mmap success");
                }
            } catch (UnsatisfiedLinkError e1) {
                // direct memory map load failed, extract and dlopen
                File libnatives = extractNativeLibrary(ctx, "natives");
                registerNativeLibEntry(libnatives.getName());
                System.load(libnatives.getAbsolutePath());
                Utils.logd("dlopen by extract success");
            }
        } catch (ClassNotFoundException e) {
            // not in host process, ignore
            System.loadLibrary("natives");
        }
        getpagesize();
        File mmkvDir = new File(ctx.getFilesDir(), "qn_mmkv");
        if (!mmkvDir.exists()) {
            mmkvDir.mkdirs();
        }
        MMKV.initialize(mmkvDir.getAbsolutePath(), s -> {
            // nop, mmkv is attached with libnatives.so already
        });
        MMKV.mmkvWithID("global_config", MMKV.MULTI_PROCESS_MODE);
        MMKV.mmkvWithID("global_cache", MMKV.MULTI_PROCESS_MODE);
    }

    /**
     * Extract or update native library into "qn_dyn_lib" dir
     *
     * @param libraryName library name without "lib" or ".so", eg. "natives", "mmkv"
     */
    static File extractNativeLibrary(Context ctx, String libraryName) throws IOException {
        String abi = Build.CPU_ABI;
        String soName = "lib" + libraryName + ".so." + BuildConfig.VERSION_CODE + "." + abi;
        File dir = new File(ctx.getFilesDir(), "qn_dyn_lib");
        if (!dir.isDirectory()) {
            if (dir.isFile()) {
                dir.delete();
            }
            dir.mkdir();
        }
        File soFile = new File(dir, soName);
        if (!soFile.exists()) {
            InputStream in = Natives.class.getClassLoader()
                .getResourceAsStream("lib/" + abi + "/lib" + libraryName + ".so");
            if (in == null) {
                throw new UnsatisfiedLinkError("Unsupported ABI: " + abi);
            }
            //clean up old files
            for (String name : dir.list()) {
                if (name.startsWith("lib" + libraryName + "_")
                    || name.startsWith("lib" + libraryName + ".so")) {
                    new File(dir, name).delete();
                }
            }
            //extract so file
            soFile.createNewFile();
            FileOutputStream fout = new FileOutputStream(soFile);
            byte[] buf = new byte[1024];
            int i;
            while ((i = in.read(buf)) > 0) {
                fout.write(buf, 0, i);
            }
            in.close();
            fout.flush();
            fout.close();
        }
        return soFile;
    }
}
