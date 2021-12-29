/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */

package me.zpp0196.qqpurify.utils;

/**
 * Created by zpp0196 on 2018/3/11.
 */
public interface Constants {

    String PACKAGE_NAME_QQ = "com.tencent.mobileqq";
    String INTENT_BUILD_NUM = "buildNum";
    String INTENT_LAUNCH = "isLaunchFromQQ";
    String APP_NAME = "QQPurify";

    String KEY_GROUPS = "groups";
    String KEY_DISABLE_MODULE = "disableModule";
    String KEY_HIDE_NEW_FRIEND = "hideNewFriend";
    String KEY_GRAY_TIP_KEYWORDS = "grayTipKeywords";
    String KEY_TRANSPARENT_IMG_BG = "transparentImgBg";
    String KEY_IMAGE_BG_COLOR = "imageBgColor";
    String KEY_RENAME_BASE_FORMAT = "renameBaseFormat";
    String KEY_REDIRECT_FILE_REC_PATH = "redirectFileRecPath";
    String KEY_LOG_COUNT = "logCount";
    String KEY_LOG_SWITCH = "logSwitch";
    String KEY_LAST_MODIFIED = "lastModified";
}
