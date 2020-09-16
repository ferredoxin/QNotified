/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
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
package nil.nadph.qnotified.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.core.view.ViewCompat;

import com.tencent.mobileqq.widget.BounceScrollView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.config.FriendRecord;
import nil.nadph.qnotified.ui.HighContrastBorder;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.ui.ViewBuilder;
import nil.nadph.qnotified.util.Utils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.subtitle;
import static nil.nadph.qnotified.util.Utils.*;

@SuppressLint("Registered")
public class FriendlistExportActivity extends IphoneTitleBarActivityCompat {

    private static final int R_ID_CHECKBOX_CSV = 0x300AFF61;
    private static final int R_ID_CHECKBOX_JSON = 0x300AFF62;
    private static final int R_ID_RB_CRLF = 0x300AFF63;
    private static final int R_ID_RB_CR = 0x300AFF65;
    private static final int R_ID_RB_LF = 0x300AFF64;
    private static final int R_ID_CB_FRIENDS = 0x300AFF66;
    private static final int R_ID_CB_EXFRIENDS = 0x300AFF67;

    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        LinearLayout ll = new LinearLayout(FriendlistExportActivity.this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(FriendlistExportActivity.this);
        __ll.setOrientation(LinearLayout.VERTICAL);
        final ViewGroup bounceScrollView = new BounceScrollView(this, null);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.setId(R.id.rootBounceScrollView);
        ll.setId(R.id.rootMainLayout);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        LinearLayout.LayoutParams fixlp = new LinearLayout.LayoutParams(MATCH_PARENT, dip2px(FriendlistExportActivity.this, 48));
        RelativeLayout.LayoutParams __lp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int mar = (int) (dip2px(FriendlistExportActivity.this, 12) + 0.5f);
        int __3_ = (int) (dip2px(FriendlistExportActivity.this, 3) + 0.5f);
        __lp_l.setMargins(mar, 0, mar, 0);
        __lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        __lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams __lp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        __lp_r.setMargins(mar, 0, mar, 0);
        __lp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        __lp_r.addRule(RelativeLayout.CENTER_VERTICAL);

        LinearLayout.LayoutParams stdlp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        stdlp.setMargins(mar, mar / 4, mar, mar / 4);

        ll.addView(subtitle(FriendlistExportActivity.this, "导出范围"));

        final CheckBox exfonly = new CheckBox(FriendlistExportActivity.this);
        exfonly.setText("历史好友");
        exfonly.setPadding(__3_, __3_, __3_, __3_);
        exfonly.setTextColor(ResUtils.skin_black);
        exfonly.setButtonDrawable(ResUtils.getCheckBoxBackground());
        exfonly.setId(R_ID_CB_EXFRIENDS);
        ll.addView(exfonly, stdlp);
        final CheckBox frionly = new CheckBox(FriendlistExportActivity.this);
        frionly.setText("当前好友");
        frionly.setPadding(__3_, __3_, __3_, __3_);
        frionly.setTextColor(ResUtils.skin_black);
        frionly.setButtonDrawable(ResUtils.getCheckBoxBackground());
        frionly.setId(R_ID_CB_FRIENDS);
        ll.addView(frionly, stdlp);

        frionly.setChecked(true);

        ll.addView(subtitle(FriendlistExportActivity.this, "导出格式"));

        final CheckBox cbCsv = new CheckBox(FriendlistExportActivity.this);
        cbCsv.setButtonDrawable(ResUtils.getCheckBoxBackground());
        cbCsv.setText("CSV");
        cbCsv.setPadding(__3_, __3_, __3_, __3_);
        cbCsv.setTextColor(ResUtils.skin_black);
        cbCsv.setId(R_ID_CHECKBOX_CSV);
        ll.addView(cbCsv, stdlp);
        final CheckBox cbJson = new CheckBox(FriendlistExportActivity.this);
        cbJson.setButtonDrawable(ResUtils.getCheckBoxBackground());
        cbJson.setText("Json");
        cbJson.setPadding(__3_, __3_, __3_, __3_);
        cbJson.setTextColor(ResUtils.skin_black);
        cbJson.setId(R_ID_CHECKBOX_JSON);
        ll.addView(cbJson, stdlp);

        LinearLayout llcsvopt = new LinearLayout(FriendlistExportActivity.this);
        llcsvopt.setOrientation(LinearLayout.VERTICAL);
        llcsvopt.addView(ViewBuilder.subtitle(FriendlistExportActivity.this, "CSV设定"));

        final RadioGroup gcsvcrlf = new RadioGroup(FriendlistExportActivity.this);
        gcsvcrlf.setOrientation(RadioGroup.VERTICAL);
        ll.addView(gcsvcrlf, stdlp);
        gcsvcrlf.addView(subtitle(FriendlistExportActivity.this, "换行符"));
        RadioButton crlf = new RadioButton(FriendlistExportActivity.this);
        crlf.setText("CRLF - \\r\\n");
        crlf.setPadding(__3_, __3_, __3_, __3_);
        crlf.setTextColor(ResUtils.skin_black);
        crlf.setButtonDrawable(ResUtils.getCheckBoxBackground());
        crlf.setId(R_ID_RB_CRLF);
        gcsvcrlf.addView(crlf, stdlp);
        RadioButton cr = new RadioButton(FriendlistExportActivity.this);
        cr.setText("CR - \\r");
        cr.setPadding(__3_, __3_, __3_, __3_);
        cr.setTextColor(ResUtils.skin_black);
        cr.setButtonDrawable(ResUtils.getCheckBoxBackground());
        cr.setId(R_ID_RB_CR);
        gcsvcrlf.addView(cr, stdlp);
        RadioButton lf = new RadioButton(FriendlistExportActivity.this);
        lf.setText("LF - \\n");
        lf.setTextColor(ResUtils.skin_black);
        lf.setPadding(__3_, __3_, __3_, __3_);
        lf.setButtonDrawable(ResUtils.getCheckBoxBackground());
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

        ll.addView(subtitle(FriendlistExportActivity.this, "请输入要导出列表的QQ号(默认为当前登录的QQ号):"));
        final EditText etuin = new EditText(FriendlistExportActivity.this);
        //etuin.setBackgroundDrawable(new HighContrastBorder());
        ViewCompat.setBackground(etuin, new HighContrastBorder());
        etuin.setPadding(__3_, __3_, __3_, __3_);
        etuin.setTextSize(Utils.dip2sp(FriendlistExportActivity.this, 18));
        etuin.setTextColor(ResUtils.skin_black);
        ll.addView(etuin, stdlp);
        long currentUin = -1;
        try {
            currentUin = Long.parseLong(Utils.getAccount());
            etuin.setHint(currentUin + "");
        } catch (Throwable e) {
            etuin.setHint("输入QQ号");
        }

        ll.addView(subtitle(FriendlistExportActivity.this, "导出文件保存路径(默认在内置存储根目录下):"));
        final EditText expath = new EditText(FriendlistExportActivity.this);
       // expath.setBackgroundDrawable(new HighContrastBorder());
        ViewCompat.setBackground(expath, new HighContrastBorder());
        expath.setPadding(__3_, __3_, __3_, __3_);
        expath.setTextSize(Utils.dip2sp(FriendlistExportActivity.this, 18));
        expath.setTextColor(ResUtils.skin_black);
        String refpath = new File(Environment.getExternalStorageDirectory(), new Date().toString().replace(" ", "") + ".txt").getAbsolutePath();
        expath.setHint(refpath);
        ll.addView(expath, stdlp);

        Button exportbtn = new Button(FriendlistExportActivity.this);
        ResUtils.applyStyleCommonBtnBlue(exportbtn);
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
        FriendlistExportActivity.this.setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;
        //__ll.addView(bounceScrollView,_lp_fat);
        setContentBackgroundDrawable(ResUtils.skin_background);
        setTitle("导出好友列表");
        //TextView rightBtn=(TextView)invoke_virtual(FriendlistExportActivity.this,"getRightTextView");
        //log("Title:"+invoke_virtual(FriendlistExportActivity.this,"getTextTitle"));
        return true;
    }


    private void doExportFile(String suin, boolean fri, boolean exf, String output, int format, int crlf) {
        long luin;
        try {
            luin = Long.parseLong(suin);
        } catch (NumberFormatException ignored) {
            Utils.showToast(FriendlistExportActivity.this, Utils.TOAST_TYPE_ERROR, "请输入有效QQ号", Toast.LENGTH_LONG);
            return;
        }
        if (!new File(Utils.getApplication().getFilesDir().getAbsolutePath() + "/qnotified_" + luin + ".dat").exists()) {

            Utils.showToast(FriendlistExportActivity.this, Utils.TOAST_TYPE_ERROR, "此QQ在本机没有记录", Toast.LENGTH_LONG);
            return;
        }
        if (!exf && !fri) {
            Utils.showToast(FriendlistExportActivity.this, Utils.TOAST_TYPE_ERROR, "请至少选择一个进行导出", Toast.LENGTH_LONG);
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
                        Utils.showToast(FriendlistExportActivity.this, Utils.TOAST_TYPE_ERROR, "无效换行符", Toast.LENGTH_LONG);
                        return;
                }
                if (fri) {
                    Map<Long, FriendRecord> friends = exm.getPersons();
                    for (Map.Entry<Long, FriendRecord> ent : friends.entrySet()) {
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
                    Map<Long, FriendRecord> friends = exm.getPersons();
                    for (Map.Entry<Long, FriendRecord> ent : friends.entrySet()) {
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
                    Map<Long, FriendRecord> friends = exm.getPersons();
                    for (Map.Entry<Long, FriendRecord> ent : friends.entrySet()) {
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
                    Map<Long, FriendRecord> friends = exm.getPersons();
                    for (Map.Entry<Long, FriendRecord> ent : friends.entrySet()) {
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
                Utils.showToast(FriendlistExportActivity.this, Utils.TOAST_TYPE_ERROR, "格式转换错误", Toast.LENGTH_LONG);
                return;
        }
        if (sb.length() == 0) {
            Utils.showToast(FriendlistExportActivity.this, Utils.TOAST_TYPE_ERROR, "格式转换错误", Toast.LENGTH_LONG);
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
                Utils.showToast(FriendlistExportActivity.this, Utils.TOAST_TYPE_SUCCESS, "操作完成", Toast.LENGTH_SHORT);
                return;
            } catch (IOException e) {
                Toast.makeText(FriendlistExportActivity.this, "创建输出文件失败\n" + e.toString(), Toast.LENGTH_LONG).show();
                return;
            }
        }

    }

}
