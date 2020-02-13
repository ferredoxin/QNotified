package nil.nadph.qnotified.util;

import java.net.URL;

public class HybridClassLoader extends ClassLoader {

    private ClassLoader clPreload;
    private ClassLoader clBase;

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
