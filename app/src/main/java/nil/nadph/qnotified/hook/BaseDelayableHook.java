package nil.nadph.qnotified.hook;

public interface BaseDelayableHook {
    boolean isInited();

    boolean init();

    int[] getPreconditions();

    boolean checkPreconditions();

    boolean isEnabled();
}
