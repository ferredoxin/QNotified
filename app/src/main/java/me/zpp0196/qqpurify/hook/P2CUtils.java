package me.zpp0196.qqpurify.hook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import nil.nadph.qnotified.config.AbstractConfigItem;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.BaseDelayableHook;

public class P2CUtils {

    @Nullable
    private static AbstractConfigItem doFindConfigByName(String name) {
        return null;
    }

    @Nullable
    private static BaseDelayableHook doFindHookByName(String name) {
        for (BaseDelayableHook h : BaseDelayableHook.queryDelayableHooks()) {
            if (h.getClass().getSimpleName().equals(name)) {
                return h;
            }
        }
        return null;
    }

    @Nullable
    public static AbstractConfigItem findConfigByName(@NonNull String name) {
        //noinspection Nullability failsafe, ok?
        if (name == null) return null;
        if (name.contains("$")) name = name.split("\\$")[0];
        if (name.equals("")) return ConfigManager.getDefaultConfig();
        AbstractConfigItem item = doFindConfigByName(name);
        if (item == null) item = doFindHookByName(name);
        return item;
    }
}
