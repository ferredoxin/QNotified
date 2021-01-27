package nil.nadph.qnotified.util.thunk.pcs;

import nil.nadph.qnotified.util.*;

public abstract class Convention {
    
    @NonNull
    public static Convention getInstance() {
        //drop x86
        if (Natives.sizeofptr() == 4) {
            return Aapcs32.INSTANCE;
        } else {
            throw new RuntimeException("Stub!");
        }
    }
    
    public abstract int getPointerSize();
}
