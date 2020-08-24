package nil.nadph.qnotified.util.thunk.pcs;

import nil.nadph.qnotified.util.Natives;
import nil.nadph.qnotified.util.NonNull;

public abstract class Convention {

    public abstract int getPointerSize();

    @NonNull
    public static Convention getInstance() {
        //drop x86
        if (Natives.sizeofptr() == 4) {
            return Aapcs32.INSTANCE;
        } else {
            throw new RuntimeException("Stub!");
        }
    }
}
