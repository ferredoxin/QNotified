package nil.nadph.qnotified.hook;
import nil.nadph.qnotified.util.*;
import nil.nadph.qnotified.ipc.*;

public abstract class BaseDelayableHook {
	
	private static BaseDelayableHook[] mAllHooks;
	
	private int myId=-1;

	public static BaseDelayableHook getHookByType(int hookId) {
		return queryDelayableHooks()[hookId];
	}
	
	public static BaseDelayableHook[] queryDelayableHooks() {
        if (mAllHooks == null)mAllHooks = new BaseDelayableHook[]{
				FlashPicHook.get(),
				RepeaterHook.get(),
				EmoPicHook.get(),
				GalleryBgHook.get()
			};
		return mAllHooks;
    }
	
	public boolean isTargetProc(){
		return (getEffectiveProc()&SyncUtils.getProcessType())!=0;
	}
	
	public abstract int getEffectiveProc();
	
    public abstract boolean isInited();

    public abstract boolean init();

    public abstract int[] getPreconditions();

    public boolean checkPreconditions(){
		for(int i:getPreconditions()){
			if(DexKit.tryLoadOrNull(i)==null)return false;
		}
		return true;
	}
	
	public final int getId(){
		if(myId!=-1)return myId;
		BaseDelayableHook[] hooks=queryDelayableHooks();
		for(int i=0;i<hooks.length;i++){
			if(hook.getClass().equals(this.getClass())
		}
	}

    public abstract boolean isEnabled();
}
