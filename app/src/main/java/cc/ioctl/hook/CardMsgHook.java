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
package cc.ioctl.hook;

import static nil.nadph.qnotified.util.Utils.log;

import android.content.Context;
import android.os.Parcelable;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import java.lang.reflect.InvocationTargetException;
import me.singleneuron.qn_kernel.decorator.BaseInputButtonDecorator;
import mqq.app.AppRuntime;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.CliOper;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Toasts;
import nil.nadph.qnotified.util.Utils;

@FunctionEntry
public class CardMsgHook extends BaseInputButtonDecorator {

    public static final int R_ID_COPY_CODE = 0x00EE77CC;
    public static final CardMsgHook INSTANCE = new CardMsgHook();

    private CardMsgHook() {
        super("qn_send_card_msg");
    }

    @Override
    public boolean decorate(@NonNull String text,
        @NonNull Parcelable session,
        @NonNull EditText input,
        @NonNull View sendBtn,
        @NonNull Context ctx1,
        @NonNull AppRuntime qqApp) {
        try {
            if (!isEnabled()) {
                return false;
            }
            if ((text.contains("<?xml") || text.contains("{\""))) {
                new Thread(() -> {
                    if (text.contains("<?xml")) {
                        try {
                            if (CardMsgHook
                                .ntSendCardMsg(qqApp, session, text)) {
                                Utils
                                    .runOnUiThread(() -> input.setText(""));
                                CliOper
                                    .sendCardMsg(Utils.getLongAccountUin(),
                                        text);
                            } else {
                                Toasts.error(ctx1, "XML语法错误(代码有误)");
                            }
                        } catch (Throwable e) {
                            if (e instanceof InvocationTargetException) {
                                e = e.getCause();
                            }
                            log(e);
                            Toasts.error(ctx1,
                                e.toString().replace("java.lang.", ""));
                        }
                    } else if (text.contains("{\"")) {
                        try {
                            // Object arkMsg = load("com.tencent.mobileqq.data.ArkAppMessage").newInstance();
                            if (CardMsgHook
                                .ntSendCardMsg(qqApp, session, text)) {
                                Utils
                                    .runOnUiThread(() -> input.setText(""));
                                CliOper
                                    .sendCardMsg(Utils.getLongAccountUin(),
                                        text);
                            } else {
                                Toasts.error(ctx1, "JSON语法错误(代码有误)");
                            }
                        } catch (Throwable e) {
                            if (e instanceof InvocationTargetException) {
                                e = e.getCause();
                            }
                            log(e);
                            Toasts.error(ctx1,
                                e.toString().replace("java.lang.", ""));
                        }
                    }
                }).start();
            }
            return true;
        } catch (Exception e) {
            Utils.log(e);
            return false;
        }
    }

    @SuppressWarnings("JavaJniMissingFunction")
    static native boolean ntSendCardMsg(AppRuntime rt, Parcelable session, String msg)
        throws Exception;

    @Override
    public boolean initOnce() {
        return true;
    }

    @NonNull
    @Override
    public Step[] getPreconditions() {
        return new Step[]{new DexDeobfStep(DexKit.C_ARK_APP_ITEM_BUBBLE_BUILDER),
            new DexDeobfStep(DexKit.C_FACADE),
            new DexDeobfStep(DexKit.C_TEST_STRUCT_MSG),
            new DexDeobfStep(DexKit.N_BASE_CHAT_PIE__INIT)};
    }
}
