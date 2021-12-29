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

package com.tencent.qphone.base.remote;

import java.util.HashMap;

public class SimpleAccount {

    public static final String _ISLOGINED = "_isLogined";
    public static final String _LOGINPROCESS = "_loginedProcess";
    public static final String _LOGINTIME = "_loginTime";
    public static final String _UIN = "_uin";
    private static final String tag = "SimpleAccount";
    private HashMap attributes = new HashMap();

    public static SimpleAccount parseSimpleAccount(String src) {
        throw new RuntimeException("Stub!");
    }

    public static boolean isSameAccount(SimpleAccount A, SimpleAccount B) {
        return A.getUin().equals(B.getUin()) && A.isLogined() == B.isLogined();
    }

    public String getUin() {
        return getAttribute(_UIN, "");
    }

    public void setUin(String uin) {
        setAttribute(_UIN, uin);
    }

    public boolean isLogined() {
        return Boolean.parseBoolean(getAttribute(_ISLOGINED, String.valueOf(false)));
    }

    public String getLoginProcess() {
        return getAttribute(_LOGINPROCESS, "");
    }

    public void setLoginProcess(String loginProcess) {
        setAttribute(_LOGINPROCESS, loginProcess);
    }

    public boolean containsKey(String key) {
        return this.attributes.containsKey(key);
    }

    public String getAttribute(String key, String defaultValue) {
        if (this.attributes.containsKey(key)) {
            return (String) this.attributes.get(key);
        }
        return defaultValue;
    }

    public String removeAttribute(String key) {
        return (String) this.attributes.remove(key);
    }

    public void setAttribute(String key, String value) {
        if (key.indexOf(" ") > 0) {
            throw new RuntimeException("key found space ");
        }
        this.attributes.put(key, value);
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public String toStoreString() {
        throw new RuntimeException("Stub!");
    }

    public HashMap getAttributes() {
        return this.attributes;
    }

    public boolean equals(Object o) {
        if (o instanceof SimpleAccount) {
            return isSameAccount(this, (SimpleAccount) o);
        }
        return false;
    }
}
