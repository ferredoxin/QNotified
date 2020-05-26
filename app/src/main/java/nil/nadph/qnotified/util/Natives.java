package nil.nadph.qnotified.util;

import android.annotation.SuppressLint;

import java.io.File;

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

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    public static void load() {
        System.load(new File(Utils.getApplication().getFilesDir(), "libnatives.so").getAbsolutePath());
    }
}