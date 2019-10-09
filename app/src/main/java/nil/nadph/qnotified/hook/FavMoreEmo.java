package nil.nadph.qnotified.hook;
import nil.nadph.qnotified.SyncUtils;

public class FavMoreEmo extends BaseDelayableHook{
	private static final FavMoreEmo self = new FavMoreEmo();

	FavMoreEmo(){}

    public static FavMoreEmo get() {
        return self;
    }

    private boolean inited = false;

    @Override
    public boolean init() {
        if (inited) return true;
        try {

            inited = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

	@Override
	public int getEffectiveProc() {
		return SyncUtils.PROC_MAIN;
	}

    @Override
    public boolean checkPreconditions() {
        return true;
    }

    @Override
    public int[] getPreconditions() {
        return new int[]{};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public boolean isEnabled() {
		return false;

    }
}
