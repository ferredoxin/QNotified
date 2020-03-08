package nil.nadph.qnotified.hook;

import android.app.Application;
import android.os.Looper;
import android.widget.Toast;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.util.Utils;

import java.io.File;

import static nil.nadph.qnotified.util.Utils.*;

public class DefaultBubbleHook extends BaseDelayableHook {
    private static final DefaultBubbleHook self = new DefaultBubbleHook();

    private DefaultBubbleHook() {
    }

    public static DefaultBubbleHook get() {
        return self;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public int[] getPreconditions() {
        return new int[0];
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public boolean isInited() {
        return true;
    }

    @Override
    public boolean isValid() {
        Application app = getApplication();
        return app == null || !isTim(app);
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            File dir = new File(getApplication().getFilesDir().getAbsolutePath() + "/bubble_info");
            boolean curr = !dir.exists() || !dir.canRead();
            if (dir.exists()) {
                if (enabled && !curr) {
                    dir.setWritable(false);
                    dir.setReadable(false);
                    dir.setExecutable(false);
                }
                if (!enabled && curr) {
                    dir.setWritable(true);
                    dir.setReadable(true);
                    dir.setExecutable(true);
                }
            }
        } catch (final Exception e) {
            Utils.log(e);
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
            } else {
                SyncUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
                    }
                });
            }
        }
    }

    @Override
    public boolean isEnabled() {
        try {
            Application app = getApplication();
            if (app != null && isTim(app)) return false;
            File dir = new File(app.getFilesDir().getAbsolutePath() + "/bubble_info");
            return !dir.exists() || !dir.canRead();
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
