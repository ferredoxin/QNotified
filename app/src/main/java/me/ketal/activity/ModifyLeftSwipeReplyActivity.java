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
package me.ketal.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.view.ViewCompat;

import com.tencent.mobileqq.widget.BounceScrollView;

import me.ketal.hook.LeftSwipeReplyHook;
import nil.nadph.qnotified.activity.IphoneTitleBarActivityCompat;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.HighContrastBorder;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.Toasts;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.newLinearLayoutParams;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemButton;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemHookSwitchInit;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemSwitch;
import static nil.nadph.qnotified.ui.ViewBuilder.subtitle;
import static nil.nadph.qnotified.util.Utils.TOAST_TYPE_ERROR;
import static nil.nadph.qnotified.util.Utils.dip2px;

@SuppressLint("Registered")
public class ModifyLeftSwipeReplyActivity extends IphoneTitleBarActivityCompat {

    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        ViewGroup bounceScrollView = new BounceScrollView(this, null);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        LeftSwipeReplyHook hook = LeftSwipeReplyHook.INSTANCE;
        ll.addView(newListItemHookSwitchInit(this, "总开关", "打开后才可使用以下功能", hook));
        ll.addView(newListItemSwitch(this, "取消消息左滑动作", "取消取消，一定要取消", hook.isNoAction(), (v, on) -> hook.setNoAction(on)));
        ll.addView(newListItemButton(this, "修改左滑消息灵敏度", "妈妈再也不用担心我误触了", null, v -> {
            CustomDialog dialog = CustomDialog.createFailsafe(ModifyLeftSwipeReplyActivity.this);
            Context ctx = dialog.getContext();
            final EditText editText = new EditText(ctx);
            editText.setTextSize(16);
            int _5 = dip2px(ModifyLeftSwipeReplyActivity.this, 5);
            editText.setPadding(_5, _5, _5, _5);
            editText.setText(String.format("%s", hook.getReplyDistance()));
            ViewCompat.setBackground(editText, new HighContrastBorder());
            LinearLayout linearLayout = new LinearLayout(ctx);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.addView(subtitle(this, "若显示为-1，代表为初始化，请先在消息界面使用一次消息左滑回复，即可获得初始阈值。\n当你修改出错时，输入一个小于0的值，即可使用默认值"));
            linearLayout.addView(editText, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, _5 * 2));
            final AlertDialog alertDialog = (AlertDialog) dialog.setTitle("输入响应消息左滑的距离")
                    .setView(linearLayout)
                    .setCancelable(true)
                    .setPositiveButton("确认", null)
                    .setNegativeButton("取消", null)
                    .create();
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = editText.getText().toString();
                    if (text.equals("")) {
                        Toasts.showToast(ModifyLeftSwipeReplyActivity.this, TOAST_TYPE_ERROR, "请输入响应消息左滑的距离", Toast.LENGTH_SHORT);
                        return;
                    }
                    int distance = 0;
                    try {
                        distance = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        Toasts.showToast(ModifyLeftSwipeReplyActivity.this, TOAST_TYPE_ERROR, "请输入有效的数据", Toast.LENGTH_SHORT);
                    }
                    alertDialog.dismiss();
                    hook.setReplyDistance(distance);
                }
            });
        }));
        ll.addView(newListItemSwitch(this, "左滑多选消息", "娱乐功能，用途未知", hook.isMultiChose(), (v, on) -> hook.setMultiChose(on)));
        setContentView(bounceScrollView);

        setContentBackgroundDrawable(ResUtils.skin_background);
        setTitle("修改消息左滑动作");
        return true;
    }
}
