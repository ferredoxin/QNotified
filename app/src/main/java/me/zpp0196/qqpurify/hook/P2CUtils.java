package me.zpp0196.qqpurify.hook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.IOException;
import nil.nadph.qnotified.config.AbstractConfigItem;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.config.MultiConfigItem;
import nil.nadph.qnotified.hook.AbsDelayableHook;
import nil.nadph.qnotified.util.Utils;

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
        if (name == null) {
            return null;
        }
        if (name.contains("$")) {
            name = name.split("\\$")[0];
        }
        if (name.equals("")) {
            return new MultiConfigItem() {
                final ConfigManager cfg = ConfigManager.getDefaultConfig();

                public boolean isValid() {
                    return true;
                }

                public boolean hasConfig(String name) {
                    return cfg.containsKey(name);
                }

                public boolean getBooleanConfig(String name) {
                    return cfg.getBooleanOrDefault(name, false);
                }

                public void setBooleanConfig(String name, boolean val) {
                    cfg.putBoolean(name, val);
                }

                public int getIntConfig(String name) {
                    return cfg.getIntOrDefault(name, -1);
                }

                public void setIntConfig(String name, int val) {
                    cfg.putInt(name, val);
                }

                public String getStringConfig(String name) {
                    return cfg.getString(name);
                }

                public void setStringConfig(String name, String val) {
                    cfg.putString(name, val);
                }

                public boolean sync() {
                    try {
                        cfg.save();
                        return true;
                    } catch (IOException e) {
                        Utils.log(e);
                        return false;
                    }
                }
            };
        }
        AbstractConfigItem item = doFindConfigByName(name);
        if (item == null) {
            item = doFindHookByName(name);
        }
        return item;
    }
}
