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
package cc.ioctl.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import cc.ioctl.script.QNScript;
import cc.ioctl.script.QNScriptManager;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.util.Toasts;

public class ScriptSettingDialog implements CompoundButton.OnCheckedChangeListener,
    View.OnClickListener {

    private final Context ctx;
    private final AlertDialog dialog;
    private final QNScript script;
    private final Button saveBtn;
    private final Button delBtn;
    private final EditText code;
    private final TextView decs;
    private final TextView author;
    private final TextView version;
    private final Switch enable;

    public ScriptSettingDialog(Context context, QNScript qs) {
        this.script = qs;
        dialog = (AlertDialog) CustomDialog.createFailsafe(context).setTitle(qs.getName())
            .setCancelable(true).create();
        ctx = dialog.getContext();
        dialog.setCanceledOnTouchOutside(false);
        View v = LayoutInflater.from(ctx).inflate(R.layout.script_setting_dialog, null);
        code = v.findViewById(R.id.script_code_text);
        decs = v.findViewById(R.id.script_decs_text);
        author = v.findViewById(R.id.script_author_text);
        version = v.findViewById(R.id.script_version_text);
        enable = v.findViewById(R.id.script_enable);
        saveBtn = v.findViewById(R.id.script_save);
        delBtn = v.findViewById(R.id.script_delete);
        dialog.setView(v);
    }

    public static void createAndShowDialog(Context ctx, QNScript qs) {
        new ScriptSettingDialog(ctx, qs).show();
    }

    public static void OnClickListener_createDialog(final Context ctx, QNScript qs) {
        createAndShowDialog(ctx, qs);
    }

    public AlertDialog show() {
        dialog.show();
        saveBtn.setOnClickListener(this);
        delBtn.setOnClickListener(this);
        enable.setChecked(script.isEnable());
        enable.setOnCheckedChangeListener(this);
        version.setText("版本: " + script.getVersion());
        author.setText("作者: " + script.getAuthor());
        decs.setText("简介: " + script.getDecs());
        code.setText(script.getCode());
        return dialog;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.script_save) {
            Toasts.error(ctx, "抱歉，暂不支持保存代码");
            return;
        }
        QNScriptManager.delScript(script);
        Toasts.error(ctx, "删除完毕");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        script.setEnable(isChecked);
        Toasts.error(ctx, "重启" + HostInformationProviderKt.getHostInfo().getHostName() + "生效");
    }
}
