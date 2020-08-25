package nil.nadph.qnotified.util.thunk.pcs;

public class Aapcs32 extends Convention {

    private Aapcs32() {
    }

    public static final Aapcs32 INSTANCE = new Aapcs32();

    @Override
    public int getPointerSize() {
        return 4;
    }
}
