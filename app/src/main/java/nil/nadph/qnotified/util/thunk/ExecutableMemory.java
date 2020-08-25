package nil.nadph.qnotified.util.thunk;

import nil.nadph.qnotified.util.Natives;

public class ExecutableMemory {

    public static long allocate(int size) {
        int ps = Natives.getpagesize();
        if (size > ps) {
            throw new OutOfMemoryError("cannot allocate " + size + ", while page size is " + ps);
        }
        throw new RuntimeException("Stub!");
    }

    public static void free(long p) {
        throw new RuntimeException("Stub!");
    }
}
