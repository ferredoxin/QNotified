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
package nil.nadph.qnotified.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.widget.LinearLayout;

import java.io.FileDescriptor;

import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.dialog.ScriptSettingDialog;
import nil.nadph.qnotified.script.QNScript;
import nil.nadph.qnotified.script.QNScriptManager;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.ui.ViewBuilder;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Utils.log;

@SuppressLint("Registered")
public class ManageScriptsActivity extends IphoneTitleBarActivityCompat {

    private final int REQUEST_CODE = 114514;

    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);

        main.addView(ViewBuilder.newListItemSwitch(this, "总开关(关闭后所有脚本均不生效)", null, ConfigManager.getDefaultConfig().getBooleanOrDefault(ConfigItems.qn_script_global, false), QNScriptManager::changeGlobal));
        main.addView(ViewBuilder.newListItemButton(this, "导入 ...", null, null, v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/x-java");
            startActivityForResult(intent, REQUEST_CODE);
        }));
        main.addView(ViewBuilder.newListItemSwitch(this, "全部启用", null, QNScriptManager.isEnableAll(), QNScriptManager::enableAll));
        addAllScript(main);
        setContentView(main);
        setTitle("脚本");
        setRightButton("帮助", ViewBuilder.clickToProxyActAction(ScriptGuideActivity.class));
        setContentBackgroundDrawable(ResUtils.skin_background);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                // 用户未选择任何文件，直接返回
                return;
            }
            Uri uri = data.getData(); // 获取用户选择文件的URI
            if (uri != null) {
                ContentResolver resolver = this.getContentResolver();
                Cursor c = resolver.query(uri, null, null, null, null);
                if (c == null) {
                    String path = uri.getPath();
                    try {
                        QNScriptManager.addScript(path);
                        Utils.showToastShort(this, "添加完毕");
                    } catch (Exception e) {
                        log(e);
                        Utils.showToastShort(this, "未知错误: " + e.getMessage());
                    }
                } else {
                    if (c.moveToFirst()) {
                        String scriptName = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        try {
                            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                            if (parcelFileDescriptor != null) {
                                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                                String err = QNScriptManager.addScriptFD(fileDescriptor, scriptName);
                                if (err.isEmpty()) {
                                    Utils.showToastShort(this, "添加完毕");
                                } else {
                                    Utils.showToastShort(this, err);
                                }
                            }
                        } catch (Throwable e) {
                            log(e);
                            Utils.showToastShort(this, "未知错误：" + e.getMessage());
                        }
                    }
                    c.close();
                }
            } else {
                Utils.showToastShort(this, "内部错误");
            }
        } else {
            Utils.showToastShort(this, "未知错误");
        }
    }


    private void addAllScript(LinearLayout main) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            String name = qs.getName() == null ? "出错" : qs.getName();
            String decs = qs.getDecs() == null ? "出错" : qs.getDecs();
            main.addView(ViewBuilder.newListItemButton(this, name, decs, qs.getEnable(), view -> ScriptSettingDialog.OnClickListener_createDialog(view.getContext(), qs)));
        }
    }
}
