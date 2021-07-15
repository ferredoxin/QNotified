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

package cc.ioctl.hook;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.newLinearLayoutParams;
import static nil.nadph.qnotified.util.Utils.dip2px;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.core.view.ViewCompat;

import java.io.File;
import java.io.IOException;

import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.drawable.HighContrastBorder;
import nil.nadph.qnotified.util.Toasts;

public class AddAccount {

    public static void onAddAccountClick(Context context) {
        CustomDialog dialog = CustomDialog.createFailsafe(context);
        Context ctx = dialog.getContext();
        EditText editText = new EditText(ctx);
        editText.setTextSize(16);
        int _5 = dip2px(context, 5);
        editText.setPadding(_5, _5, _5, _5);
        ViewCompat.setBackground(editText, new HighContrastBorder());
        LinearLayout linearLayout = new LinearLayout(ctx);
        linearLayout.addView(editText, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, _5 * 2));
        AlertDialog alertDialog = (AlertDialog) dialog
            .setTitle("输入要添加的QQ号")
            .setView(linearLayout)
            .setPositiveButton("添加", null)
            .setNegativeButton("取消", null)
            .create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
            String uinText = editText.getText().toString();
            long uin = -1;
            try {
                uin = Long.parseLong(uinText);
            } catch (NumberFormatException ignored) {
            }
            if (uin < 10000) {
                Toasts.error(context, "QQ号无效");
                return;
            }
            boolean success;
            File f = new File(context.getFilesDir(), "user/u_" + uin + "_t");
            try {
                success = f.createNewFile();
            } catch (IOException e) {
                Toasts.error(context,
                    e.toString().replaceAll("java\\.(lang|io)\\.", ""));
                return;
            }
            if (success) {
                Toasts.success(context, "已添加");
            } else {
                Toasts.info(context, "该账号已存在");
                return;
            }
            alertDialog.dismiss();
        });
    }

}
