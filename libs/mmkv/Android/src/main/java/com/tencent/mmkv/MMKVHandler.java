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

package com.tencent.mmkv;

// callback is called on the operating thread of the MMKV instance
public interface MMKVHandler {

    // by default MMKV will discard all data on crc32-check failure
    // return `OnErrorRecover` to recover any data on the file
    MMKVRecoverStrategic onMMKVCRCCheckFail(String mmapID);

    // by default MMKV will discard all data on file length mismatch
    // return `OnErrorRecover` to recover any data on the file
    MMKVRecoverStrategic onMMKVFileLengthError(String mmapID);

    // return false if you don't want log redirecting
    boolean wantLogRedirecting();

    // log redirecting
    void mmkvLog(MMKVLogLevel level, String file, int line, String function, String message);
}
