package me.kyuubiran.util

import nil.nadph.qnotified.ExfriendManager
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.util.Utils

fun getDefaultCfg(): ConfigManager {
    return ConfigManager.getDefaultConfig()
}

fun getExFriendCfg(): ConfigManager {
    return ExfriendManager.get(Utils.getLongAccountUin()).config
}