package nil.nadph.qnotified.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.script.QNScript;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.util.Utils;

public class ScriptSettingDialog implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private final Context ctx;
    private final AlertDialog dialog;
    private final QNScript script;
    private final Button saveBtn;
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
        @SuppressLint("InflateParams") View v = LayoutInflater.from(ctx).inflate(R.layout.script_setting_dialog, null);
        code = v.findViewById(R.id.script_code_text);
        decs = v.findViewById(R.id.script_decs_text);
        author = v.findViewById(R.id.script_author_text);
        version = v.findViewById(R.id.script_version_text);
        enable = v.findViewById(R.id.script_enable);
        saveBtn = v.findViewById(R.id.script_save);
        dialog.setView(v);
    }

    public AlertDialog show() {
        dialog.show();
        saveBtn.setOnClickListener(this);
        enable.setChecked(script.isEnable());
        enable.setOnCheckedChangeListener(this);
        version.setText(script.getVersion());
        author.setText(script.getAuthor());
        decs.setText(script.getDecs());
        code.setText(script.getCode());
        return dialog;
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View v) {
        Utils.showToast(ctx, Utils.TOAST_TYPE_ERROR, "抱歉，暂不支持修改代码", Toast.LENGTH_SHORT);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        script.setEnable(isChecked);
    }

    public static void createAndShowDialog(Context ctx, QNScript qs) {
        new ScriptSettingDialog(ctx, qs).show();
    }

    public static View.OnClickListener OnClickListener_createDialog(final Context ctx, QNScript qs) {
        return v -> createAndShowDialog(ctx, qs);
    }
}
