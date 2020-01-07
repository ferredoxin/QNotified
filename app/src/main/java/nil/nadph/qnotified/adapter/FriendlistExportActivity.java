package nil.nadph.qnotified.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.record.FriendRecord;
import nil.nadph.qnotified.util.QQViewBuilder;
import nil.nadph.qnotified.util.QThemeKit;
import nil.nadph.qnotified.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.QQViewBuilder.subtitle;
import static nil.nadph.qnotified.util.Utils.*;

public class FriendlistExportActivity implements ActivityAdapter {

    private Activity self;

    private static final int R_ID_CHECKBOX_CSV = 0x300AFF61;
    private static final int R_ID_CHECKBOX_JSON = 0x300AFF62;

    private static final int R_ID_RB_CRLF = 0x300AFF63;
    private static final int R_ID_RB_CR = 0x300AFF65;
    private static final int R_ID_RB_LF = 0x300AFF64;

    private static final int R_ID_CB_FRIENDS = 0x300AFF66;
    private static final int R_ID_CB_EXFRIENDS = 0x300AFF67;


    public FriendlistExportActivity(Activity activity) {
        self = activity;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void doOnPostCreate(Bundle savedInstanceState) throws Throwable {
        LinearLayout ll = new LinearLayout(self);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(self);
        __ll.setOrientation(LinearLayout.VERTICAL);
        final ViewGroup bounceScrollView = (ViewGroup) new_instance(load("com/tencent/mobileqq/widget/BounceScrollView"), self, null, Context.class, AttributeSet.class);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        bounceScrollView.setBackgroundDrawable(QThemeKit.qq_setting_item_bg_nor);
        LinearLayout.LayoutParams fixlp = new LinearLayout.LayoutParams(MATCH_PARENT, dip2px(self, 48));
        RelativeLayout.LayoutParams __lp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int mar = (int) (dip2px(self, 12) + 0.5f);
        __lp_l.setMargins(mar, 0, mar, 0);
        __lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        __lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams __lp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        __lp_r.setMargins(mar, 0, mar, 0);
        __lp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        __lp_r.addRule(RelativeLayout.CENTER_VERTICAL);

        LinearLayout.LayoutParams stdlp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        stdlp.setMargins(mar, mar / 4, mar, mar / 4);

        ll.addView(subtitle(self, "导出范围"));

        final CheckBox exfonly = new CheckBox(self);
        exfonly.setText("历史好友");
        exfonly.setTextColor(QThemeKit.skin_black);
        exfonly.setButtonDrawable(QThemeKit.getCheckBoxBackground());
        exfonly.setId(R_ID_CB_EXFRIENDS);
        ll.addView(exfonly, stdlp);
        final CheckBox frionly = new CheckBox(self);
        frionly.setText("当前好友");
        frionly.setTextColor(QThemeKit.skin_black);
        frionly.setButtonDrawable(QThemeKit.getCheckBoxBackground());
        frionly.setId(R_ID_CB_FRIENDS);
        ll.addView(frionly, stdlp);

        frionly.setChecked(true);

        ll.addView(subtitle(self, "导出格式"));

        final CheckBox cbCsv = new CheckBox(self);
        cbCsv.setButtonDrawable(QThemeKit.getCheckBoxBackground());
        cbCsv.setText("CSV");
        cbCsv.setTextColor(QThemeKit.skin_black);
        cbCsv.setId(R_ID_CHECKBOX_CSV);
        ll.addView(cbCsv, stdlp);
        final CheckBox cbJson = new CheckBox(self);
        cbJson.setButtonDrawable(QThemeKit.getCheckBoxBackground());
        cbJson.setText("Json");
        cbCsv.setTextColor(QThemeKit.skin_black);
        cbJson.setId(R_ID_CHECKBOX_JSON);
        ll.addView(cbJson, stdlp);

        LinearLayout llcsvopt = new LinearLayout(self);
        llcsvopt.setOrientation(LinearLayout.VERTICAL);
        llcsvopt.addView(QQViewBuilder.subtitle(self, "CSV设定"));

        final RadioGroup gcsvcrlf = new RadioGroup(self);
        gcsvcrlf.setOrientation(RadioGroup.VERTICAL);
        ll.addView(gcsvcrlf, stdlp);
        gcsvcrlf.addView(subtitle(self, "换行符"));
        RadioButton crlf = new RadioButton(self);
        crlf.setText("CRLF - \\r\\n");
        crlf.setTextColor(QThemeKit.skin_black);
        crlf.setButtonDrawable(QThemeKit.getCheckBoxBackground());
        crlf.setId(R_ID_RB_CRLF);
        gcsvcrlf.addView(crlf, stdlp);
        RadioButton cr = new RadioButton(self);
        cr.setText("CR - \\r");
        cr.setTextColor(QThemeKit.skin_black);
        cr.setButtonDrawable(QThemeKit.getCheckBoxBackground());
        cr.setId(R_ID_RB_CR);
        gcsvcrlf.addView(cr, stdlp);
        RadioButton lf = new RadioButton(self);
        lf.setText("LF - \\n");
        lf.setTextColor(QThemeKit.skin_black);
        lf.setButtonDrawable(QThemeKit.getCheckBoxBackground());
        lf.setId(R_ID_RB_LF);
        gcsvcrlf.addView(lf, stdlp);

        View.OnClickListener formatListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R_ID_CHECKBOX_CSV:
                        cbCsv.setChecked(true);
                        cbJson.setChecked(false);
                        gcsvcrlf.setVisibility(View.VISIBLE);
                        break;
                    case R_ID_CHECKBOX_JSON:
                        cbCsv.setChecked(false);
                        cbJson.setChecked(true);
                        gcsvcrlf.setVisibility(View.GONE);

                }
            }
        };
        cbCsv.setOnClickListener(formatListener);
        cbJson.setOnClickListener(formatListener);

        lf.setChecked(true);
        formatListener.onClick(cbCsv);

        ll.addView(subtitle(self, "请输入要导出列表的QQ号(默认为当前登录的QQ号):"));
        final EditText etuin = new EditText(self);
        etuin.setBackgroundDrawable(null);
        etuin.setTextSize(Utils.dip2sp(self, 18));
        etuin.setTextColor(QThemeKit.skin_black);
        ll.addView(etuin, stdlp);
        long currentUin = -1;
        try {
            currentUin = Long.parseLong(Utils.getAccount());
            etuin.setHint(currentUin + "");
        } catch (Throwable e) {
            etuin.setHint("输入QQ号");
        }

        ll.addView(subtitle(self, "导出文件保存路径(默认在内置存储根目录下):"));
        final EditText expath = new EditText(self);
        expath.setBackgroundDrawable(null);
        expath.setTextSize(Utils.dip2sp(self, 18));
        expath.setTextColor(QThemeKit.skin_black);
        String refpath = new File(Environment.getExternalStorageDirectory(), new Date().toString().replace(" ", "") + ".txt").getAbsolutePath();
        expath.setHint(refpath);
        ll.addView(expath, stdlp);

        Button exportbtn = new Button(self);
        exportbtn.setText("导出");

        final long refuin = currentUin;

        exportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t_uin = etuin.getText().toString();
                if (t_uin.equals("")) {
                    t_uin = "" + refuin;
                }
                String export = expath.getText().toString();
                if (export.equals("")) {
                    export = expath.getHint().toString();
                }
                int format = cbCsv.isChecked() ? 1 : 2;
                int rn = gcsvcrlf.getCheckedRadioButtonId();
                doExportFile(t_uin, frionly.isChecked(), exfonly.isChecked(), export, format, rn);
            }
        });
        ll.addView(exportbtn, stdlp);

        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        self.setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;
        //__ll.addView(bounceScrollView,_lp_fat);
        ActProxyMgr.setContentBackgroundDrawable(self, QThemeKit.skin_background);
        invoke_virtual(self, "setTitle", "导出好友列表", CharSequence.class);
        invoke_virtual(self, "setImmersiveStatus");
        invoke_virtual(self, "enableLeftBtn", true, boolean.class);
        //TextView rightBtn=(TextView)invoke_virtual(self,"getRightTextView");
        //log("Title:"+invoke_virtual(self,"getTextTitle"));
    }


    private void doExportFile(String suin, boolean fri, boolean exf, String output, int format, int crlf) {
        long luin;
        try {
            luin = Long.parseLong(suin);
        } catch (NumberFormatException ignored) {
            try {
                Utils.showToast(self, Utils.TOAST_TYPE_ERROR, "请输入有效QQ号", Toast.LENGTH_LONG);
            } catch (Throwable e2) {
            }
            return;
        }
        if (!new File(Utils.getApplication().getFilesDir().getAbsolutePath() + "/qnotified_" + luin + ".dat").exists()) {
            try {
                Utils.showToast(self, Utils.TOAST_TYPE_ERROR, "此QQ在本机没有记录", Toast.LENGTH_LONG);
            } catch (Throwable e2) {
            }
            return;
        }
        if (!exf && !fri) {
            try {
                Utils.showToast(self, Utils.TOAST_TYPE_ERROR, "请至少选择一个进行导出", Toast.LENGTH_LONG);
            } catch (Throwable e2) {
            }
            return;
        }
        String rn;
        ExfriendManager exm = ExfriendManager.get(luin);
        StringBuilder sb = new StringBuilder();
        switch (format) {
            case 1://csv
                switch (crlf) {
                    case R_ID_RB_CRLF:
                        rn = "\r\n";
                        break;
                    case R_ID_RB_CR:
                        rn = "\r";
                        break;
                    case R_ID_RB_LF:
                        rn = "\n";
                        break;
                    default:
                        try {
                            Utils.showToast(self, Utils.TOAST_TYPE_ERROR, "无效换行符", Toast.LENGTH_LONG);
                        } catch (Throwable e2) {
                        }
                        return;
                }
                if (fri) {
                    HashMap<Long, FriendRecord> friends = exm.getPersons();
                    for (HashMap.Entry<Long, FriendRecord> ent : friends.entrySet()) {
                        long uin = ent.getKey();
                        FriendRecord rec = ent.getValue();
                        if (rec.friendStatus != FriendRecord.STATUS_EXFRIEND) {
                            sb.append(uin).append(",");
                            sb.append(csvenc(rec.remark)).append(",");
                            sb.append(csvenc(rec.nick)).append(",");
                            sb.append(rec.friendStatus).append(rn);
                        }
                    }
                }
                if (exf) {
                    HashMap<Long, FriendRecord> friends = exm.getPersons();
                    for (HashMap.Entry<Long, FriendRecord> ent : friends.entrySet()) {
                        long uin = ent.getKey();
                        FriendRecord rec = ent.getValue();
                        if (!fri || rec.friendStatus != FriendRecord.STATUS_FRIEND_MUTUAL) {
                            sb.append(uin).append(",");
                            sb.append(csvenc(rec.remark)).append(",");
                            sb.append(csvenc(rec.nick)).append(",");
                            sb.append(rec.friendStatus).append(rn);
                        }
                    }
                }
                if (sb.length() > 1 && sb.charAt(sb.length() - 1) < 17) {
                    sb.delete(sb.length() - rn.length(), sb.length());
                }
                break;
            case 2://json
                sb.append('[');
                if (fri) {
                    HashMap<Long, FriendRecord> friends = exm.getPersons();
                    for (HashMap.Entry<Long, FriendRecord> ent : friends.entrySet()) {
                        long uin = ent.getKey();
                        FriendRecord rec = ent.getValue();
                        if (rec.friendStatus != FriendRecord.STATUS_EXFRIEND) {
                            sb.append('{');
                            sb.append("\"uin\":").append(uin).append(",");
                            sb.append("\"remark\":").append(en(rec.remark)).append(",");
                            sb.append("\"nick\":").append(en(rec.nick)).append(",");
                            sb.append("\"status\":").append(rec.friendStatus).append('}');
                            sb.append(',');
                        }
                    }
                }
                if (exf) {
                    HashMap<Long, FriendRecord> friends = exm.getPersons();
                    for (HashMap.Entry<Long, FriendRecord> ent : friends.entrySet()) {
                        long uin = ent.getKey();
                        FriendRecord rec = ent.getValue();
                        if (!fri || rec.friendStatus != FriendRecord.STATUS_FRIEND_MUTUAL) {
                            sb.append('{');
                            sb.append("\"uin\":").append(uin).append(",");
                            sb.append("\"remark\":").append(en(rec.remark)).append(",");
                            sb.append("\"nick\":").append(en(rec.nick)).append(",");
                            sb.append("\"status\":").append(rec.friendStatus).append('}');
                            sb.append(',');
                        }
                    }
                }
                if (sb.length() > 1 && sb.charAt(sb.length() - 1) == ',') {
                    sb.delete(sb.length() - 1, sb.length());
                }
                sb.append(']');
                break;
            default:
                try {
                    Utils.showToast(self, Utils.TOAST_TYPE_ERROR, "格式转换错误", Toast.LENGTH_LONG);
                } catch (Throwable e2) {
                }
                return;
        }
        if (sb.length() == 0) {
            try {
                Utils.showToast(self, Utils.TOAST_TYPE_ERROR, "格式转换错误", Toast.LENGTH_LONG);
            } catch (Throwable e2) {
            }
            return;
        }
        File f = new File(output);
        if (!f.exists()) {
            try {
                f.createNewFile();
                FileOutputStream fout = new FileOutputStream(f);
                fout.write(sb.toString().getBytes());
                fout.flush();
                fout.close();
                try {
                    Utils.showToast(self, Utils.TOAST_TYPE_SUCCESS, "操作完成", Toast.LENGTH_SHORT);
                } catch (Throwable e2) {
                }
                return;
            } catch (IOException e) {
                try {
                    Toast.makeText(self, "创建输出文件失败\n" + e.toString(), Toast.LENGTH_LONG).show();
                } catch (Throwable e2) {
                }
                return;
            }
        }

    }

    @Override
    public void doOnPostResume() throws Throwable {
    }

    @Override
    public void doOnPostPause() throws Throwable {
    }

    @Override
    public void doOnPostDestory() throws Throwable {
    }

    @Override
    public void doOnPostActivityResult(int requestCode, int resultCode, Intent data) throws Throwable {
    }

    @Override
    public boolean isWrapContent() throws Throwable {
        return true;
    }
}
