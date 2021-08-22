package me.zpp0196.qqpurify.hook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import nil.nadph.qnotified.config.AbstractConfigItem;
import nil.nadph.qnotified.hook.AbsDelayableHook;

public class P2CUtils {

    @Nullable
    private static AbstractConfigItem doFindConfigByName(String name) {
        return null;
    }

    @Nullable
    private static AbsDelayableHook doFindHookByName(String name) {
        for (AbsDelayableHook h : AbsDelayableHook.queryDelayableHooks()) {
            if (h.getClass().getSimpleName().equals(name)) {
                return h;
            }
        }
        return null;
    }

    @Nullable
    public static AbstractConfigItem findConfigByName(@NonNull String name) {
        //noinspection Nullability failsafe, ok?
        if (name == null || name.isEmpty()) {
            return null;
        }
        if (name.contains("$")) {
            name = name.split("\\$")[0];
        }
        AbstractConfigItem item = doFindConfigByName(name);
        if (item == null) {
            item = doFindHookByName(name);
        }
        return item;
    }
}
