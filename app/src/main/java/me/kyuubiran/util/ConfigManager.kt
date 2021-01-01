package me.kyuubiran.util

import nil.nadph.qnotified.ExfriendManager
import nil.nadph.qnotified.config.ConfigManager

fun getDefaultCfg(): ConfigManager {
    return ConfigManager.getDefaultConfig()
}

fun getExFriendCfg(): ConfigManager {
    return ExfriendManager.getCurrent().config
}
