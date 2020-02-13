package nil.nadph.qnotified.util;

import java.net.URL;

public class HybridClassLoader extends ClassLoader {

    private ClassLoader clXposed;
    private ClassLoader clContext;

    public HybridClassLoader(ClassLoader x, ClassLoader ctx) {
        clXposed = x;
        clContext = ctx;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            return ClassLoader.getSystemClassLoader().loadClass(name);
        } catch (ClassNotFoundException ignored) {
        }
        if (clXposed != null) {
            try {
                return clXposed.loadClass(name);
            } catch (ClassNotFoundException ignored) {
            }
        }
        if (clContext != null) {
            try {
                return clContext.loadClass(name);
            } catch (ClassNotFoundException ignored) {
            }
        }
        throw new ClassNotFoundException(name);
    }

    @Override
    public URL getResource(String name) {
        URL ret = clXposed.getResource(name);
        if (ret != null) return ret;
        return clContext.getResource(name);
    }
}
