package nil.nadph.qnotified.util.thunk;

import nil.nadph.qnotified.util.NonNull;
import nil.nadph.qnotified.util.thunk.pcs.Convention;

public class NativeFunction {

    @NonNull
    public ThunkProcedureCall call(@NonNull Object... arg) {
        throw new RuntimeException("Stub!");
    }

    public static class Builder {
        private long addr;

        public Builder(Convention cc) {
            if (cc == null) throw new NullPointerException("calling convention is null");
        }

        public Builder setAddress(long a) {
            addr = a;
            return this;
        }
    }
}
