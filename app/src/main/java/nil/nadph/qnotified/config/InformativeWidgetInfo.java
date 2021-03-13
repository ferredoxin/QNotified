/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
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
package nil.nadph.qnotified.config;

import android.view.View;
import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;
import com.qq.taf.jce.JceStruct;
import nil.nadph.qnotified.remote.JceId;
import nil.nadph.qnotified.util.Utils;

public class InformativeWidgetInfo extends JceStruct implements View.OnClickListener,
    View.OnLongClickListener {

    public static final InformativeWidgetInfo[] TYPE_ARRAY = new InformativeWidgetInfo[]{
        new InformativeWidgetInfo()};
    public static final int FRAGMENT_MAIN_SETTING = 1;
    public static final int FRAGMENT_PENDING_FUNC = 2;
    public static final int TYPE_BUTTON = 1;
    public static final int TYPE_SWITCH = 2;
    public static final int STATE_FLAG_CHECKABLE = 0x100;
    public static final int STATE_FLAG_CHECKED = 0x200;
    public static final int OP_TYPE_NULL = 0;
    public static final int OP_TYPE_TOAST = 1;//raw
    public static final int OP_TYPE_DIALOG = 2;//json
    public static final int OP_TYPE_CLIPBOARD_COPY = 3;//raw
    public static final int OP_TYPE_PROFILE_USER = 4;//uin
    public static final int OP_TYPE_PROFILE_TROOP = 5;//uin
    public static final int OP_TYPE_TEMP_CHAT_UIN = 6;//uin
    public static final int OP_TYPE_JUMP_URL = 7;//raw
    public static final int OP_TYPE_JUMP_TELEGRAM = 8;//"@name" or "http*"
    public static final int TOAST_TYPE_MASK = 0xF;
    public static final int TOAST_TYPE_DEFAULT = 0xF;
    public static final int TOAST_TYPE_INFO = Utils.TOAST_TYPE_INFO;
    public static final int TOAST_TYPE_SUCCESS = Utils.TOAST_TYPE_SUCCESS;
    public static final int TOAST_TYPE_ERROR = Utils.TOAST_TYPE_ERROR;
    public static final int TOAST_FLAG_SHORT = 0x0;
    public static final int TOAST_FLAG_LONG = 0x10;
    public static final int DIALOG_TYPE_DEFAULT = 0;
    public static final int DIALOG_TYPE_CUSTOM = 0x1;
    public static final int DIALOG_FLAG_CANCELABLE = 0x2;
    @JceId(0)
    public int id;
    @JceId(1)
    public int section;
    @JceId(2)
    public int index;
    @JceId(3)
    public int blackRequiredFlags;
    @JceId(4)
    public int blackExcludedFlags;
    @JceId(5)
    public int whiteRequiredFlags;
    @JceId(6)
    public int whiteExcludedFlags;
    @JceId(7)
    public int type;
    @JceId(8)
    public int typeFlags;
    @JceId(9)
    public int stateFlags;
    @JceId(10)
    public String title = "";
    @JceId(11)
    public String description = "";
    @JceId(12)
    public String value = "";
    @JceId(13)
    public int onClickOpType;
    @JceId(14)
    public int onClickOpFlags;
    @JceId(15)
    public String onClickOpArgs = "";
    @JceId(16)
    public int onLongClickOpType;
    @JceId(17)
    public int onLongClickOpFlags;
    @JceId(18)
    public String onLongClickOpArgs = "";


    public InformativeWidgetInfo() {
        //used for jce
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public void writeTo(JceOutputStream jceOutputStream) {

    }

    @Override
    public void readFrom(JceInputStream jceInputStream) {

    }
}
