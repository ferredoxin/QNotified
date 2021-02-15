/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package cc.ioctl.hook;

import android.os.Parcelable;

import com.tencent.mobileqq.app.QQAppInterface;

import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.DexKit;


public class CardMsgHook extends CommonDelayableHook {
    public static final int R_ID_COPY_CODE = 0x00EE77CC;
    private static final CardMsgHook self = new CardMsgHook();

    private CardMsgHook() {
        super("qn_send_card_msg");
    }

    public static CardMsgHook get() {
        return self;
    }

    @Override
    public boolean initOnce() {
        return true;
    }

    @SuppressWarnings("JavaJniMissingFunction")
    static native boolean ntSendCardMsg(QQAppInterface rt, Parcelable session, String msg) throws Exception;

    @Override
    public Step[] getPreconditions() {
        return new Step[]{new DexDeobfStep(DexKit.C_ARK_APP_ITEM_BUBBLE_BUILDER), new DexDeobfStep(DexKit.C_FACADE),
                new DexDeobfStep(DexKit.C_TEST_STRUCT_MSG), new DexDeobfStep(DexKit.N_BASE_CHAT_PIE__INIT)};
    }
}
