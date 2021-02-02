/* QNotified - An Xposed module for QQ/TIM
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
package nil.nadph.qnotified.dialog;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;

import bsh.*;
import cn.lliiooll.script.*;
import me.singleneuron.qn_kernel.data.*;
import nil.nadph.qnotified.*;
import nil.nadph.qnotified.script.*;
import nil.nadph.qnotified.ui.*;
import nil.nadph.qnotified.util.*;

public class ScriptSettingDialog implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

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
        script = qs;
        dialog = (AlertDialog) CustomDialog.createFailsafe(context).setTitle(qs.getInfo().getName())
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

    public AlertDialog show() {
        dialog.show();
        saveBtn.setOnClickListener(this);
        delBtn.setOnClickListener(this);
        enable.setChecked(script.isEnable());
        enable.setOnCheckedChangeListener(this);
        version.setText("版本: " + script.getInfo().getVersion());
        author.setText("作者: " + script.getInfo().getAuthor());
        decs.setText("简介: " + script.getInfo().getDecs());
        code.setText(script.getCode());
        return dialog;
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.script_save) {
            Utils.showToast(ctx, Utils.TOAST_TYPE_ERROR, "抱歉，暂不支持保存代码", Toast.LENGTH_SHORT);
            return;
        }
        QNScriptManager.delScript(script);
        Utils.showToast(ctx, Utils.TOAST_TYPE_ERROR, "删除完毕", Toast.LENGTH_SHORT);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            try {
                QNScriptFactory.enable(script);
            } catch (EvalError evalError) {
                Utils.log(evalError);
            }
        } else {
            QNScriptFactory.disable(script);
        }
        Utils.showToast(ctx, Utils.TOAST_TYPE_ERROR,
            "重启" + HostInformationProviderKt.getHostInformationProvider().getHostName() + "生效", Toast.LENGTH_SHORT);
    }
    
    public static void createAndShowDialog(Context ctx, QNScript qs) {
        new ScriptSettingDialog(ctx, qs).show();
    }
    
    public static void OnClickListener_createDialog(Context ctx, QNScript qs) {
        createAndShowDialog(ctx, qs);
    }
}
