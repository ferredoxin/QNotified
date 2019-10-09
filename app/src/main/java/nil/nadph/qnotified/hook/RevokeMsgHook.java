package nil.nadph.qnotified.hook;
import nil.nadph.qnotified.ipc.*;
import nil.nadph.qnotified.util.*;
import nil.nadph.qnotified.record.*;

import static nil.nadph.qnotified.util.Utils.log;

public class RevokeMsgHook extends BaseDelayableHook {
	private static final RevokeMsgHook self = new RevokeMsgHook();

	RevokeMsgHook(){}
	
    public static RevokeMsgHook get() {
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
		return SyncUtils.PROC_PEAK;
	}

    @Override
    public boolean checkPreconditions() {
        return DexKit.tryLoadOrNull(DexKit.C_ABS_GAL_SCENE) != null;
    }

    @Override
    public int[] getPreconditions() {
        return new int[]{DexKit.C_ABS_GAL_SCENE};
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
