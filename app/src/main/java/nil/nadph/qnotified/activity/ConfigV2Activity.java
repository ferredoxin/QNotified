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
package nil.nadph.qnotified.activity;

import static me.ketal.ui.activity.QFileShareToIpadActivity.ENABLE_SEND_TO_IPAD;
import static me.ketal.ui.activity.QFileShareToIpadActivity.ENABLE_SEND_TO_IPAD_STATUS;
import static me.ketal.ui.activity.QFileShareToIpadActivity.SEND_TO_IPAD_CMD;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;

import me.ketal.ui.activity.QFileShareToIpadActivity;
import me.ketal.util.ComponentUtilKt;
import me.singleneuron.util.HookStatue;
import nil.nadph.qnotified.BuildConfig;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.databinding.MainV2Binding;
import nil.nadph.qnotified.lifecycle.JumpActivityEntryHook;
import nil.nadph.qnotified.startup.HookEntry;
import nil.nadph.qnotified.util.Natives;
import nil.nadph.qnotified.util.UiThread;
import nil.nadph.qnotified.util.Utils;

public class ConfigV2Activity extends AppCompatActivity {

    private static final String ALIAS_ACTIVITY_NAME = "nil.nadph.qnotified.activity.ConfigV2ActivityAlias";
    private final Looper mainLooper = Looper.getMainLooper();
    private String dbgInfo = "";
    private MainV2Binding mainV2Binding = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (R.string.res_inject_success >>> 24 == 0x7f) {
            throw new RuntimeException("package id must NOT be 0x7f");
        }
        String cmd = getIntent().getStringExtra(SEND_TO_IPAD_CMD);
        if (ENABLE_SEND_TO_IPAD.equals(cmd)) {
            boolean enabled = getIntent().getBooleanExtra(ENABLE_SEND_TO_IPAD_STATUS, false);
            ComponentName componentName = new ComponentName(this, QFileShareToIpadActivity.class);
            ComponentUtilKt.setEnable(componentName, this, enabled);
            finish();
        }
        String str = "";
        try {
            str += "SystemClassLoader:" + ClassLoader.getSystemClassLoader() +
                "\nActiveModuleVersion:" + BuildConfig.VERSION_NAME
                + "\nThisVersion:" + Utils.QN_VERSION_NAME + "";
        } catch (Throwable r) {
            str += r;
        }
        dbgInfo += str;
        HookStatue.Statue statue = HookStatue.INSTANCE.getStatue(this, false);
        boolean isDynLoad = false;
        InputStream in = ConfigV2Activity.class.getClassLoader()
            .getResourceAsStream("assets/xposed_init");
        byte[] buf = new byte[64];
        String start;
        try {
            int len = in.read(buf);
            in.close();
            start = new String(buf, 0, len).replace("\n", "").replace("\r", "").replace(" ", "");
        } catch (IOException e) {
            start = e.toString();
        }
        if ("nil.nadph.qnotified.startup.HookLoader".equals(start)) {
            isDynLoad = true;
        }
        try {
            long delta = System.currentTimeMillis();
            Natives.load(this);
            long ts = Utils.getBuildTimestamp();
            delta = System.currentTimeMillis() - delta;
            dbgInfo += "\nBuild Time: " + (ts > 0 ? new Date(ts).toString() : "unknown") + ", " +
                "delta=" + delta + "ms\n" +
                "SUPPORTED_ABIS=" + Arrays.toString(Build.SUPPORTED_ABIS) + "\npageSize=" + Natives
                .getpagesize();
        } catch (Throwable e) {
            dbgInfo += "\n" + e.toString();
        }
        mainV2Binding = MainV2Binding.inflate(LayoutInflater.from(this));
        setContentView(mainV2Binding.getRoot());
        LinearLayout frameStatus = mainV2Binding.mainV2ActivationStatusLinearLayout;
        ImageView frameIcon = mainV2Binding.mainV2ActivationStatusIcon;
        TextView statusTitle = mainV2Binding.mainV2ActivationStatusTitle;
        frameStatus.setBackground(ResourcesCompat.getDrawable(getResources(),
            HookStatue.INSTANCE.isActive(statue) ? R.drawable.bg_green_solid :
                R.drawable.bg_red_solid, getTheme()));
        frameIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
            HookStatue.INSTANCE.isActive(statue) ? R.drawable.ic_success_white :
                R.drawable.ic_failure_white, getTheme()));
        statusTitle.setText(HookStatue.INSTANCE.isActive(statue) ? "已激活" : "未激活");
        TextView tvStatus = mainV2Binding.mainV2ActivationStatusDesc;
        tvStatus.setText(getString(HookStatue.INSTANCE.getStatueName(statue)).split(" ")[0]);
        TextView tvInsVersion = mainV2Binding.mainTextViewVersion;
        tvInsVersion.setText(Utils.QN_VERSION_NAME);
        mainV2Binding.topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_debugInfo: {
                        new androidx.appcompat.app.AlertDialog.Builder(ConfigV2Activity.this)
                            .setTitle("调试信息").setPositiveButton(android.R.string.ok, null)
                            .setMessage(dbgInfo).show();
                        return true;
                    }
                    case R.id.menu_item_switchTheme: {
                        startActivity(new Intent(ConfigV2Activity.this ,ConfigActivity.class));
                        return true;
                    }
                    case R.id.menu_item_about: {
                        Toast.makeText(ConfigV2Activity.this, "暂不支持", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    case R.id.mainV2_menuItem_toggleDesktopIcon: {
                        setLauncherIconEnabled(!isLauncherIconEnabled());
                        SyncUtils.postDelayed(() -> updateMenuItems(), 500);
                        return true;
                    }
                    default: {
                        return ConfigV2Activity.super.onOptionsItemSelected(item);
                    }
                }
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    public void openModuleSettingForHost(View view) {
        String pkg = null;
        switch (view.getId()) {
            case R.id.mainRelativeLayoutButtonOpenQQ: {
                pkg = HookEntry.PACKAGE_NAME_QQ;
                break;
            }
            case R.id.mainRelativeLayoutButtonOpenTIM: {
                pkg = HookEntry.PACKAGE_NAME_TIM;
                break;
            }
            case R.id.mainRelativeLayoutButtonOpenQQLite: {
                pkg = HookEntry.PACKAGE_NAME_QQ_LITE;
                break;
            }
            default: {
            }
        }
        if (pkg != null) {
            Intent intent = new Intent();
            intent
                .setComponent(new ComponentName(pkg, "com.tencent.mobileqq.activity.JumpActivity"));
            intent.setAction(Intent.ACTION_VIEW);
            intent.putExtra(JumpActivityEntryHook.JUMP_ACTION_CMD,
                JumpActivityEntryHook.JUMP_ACTION_SETTING_ACTIVITY);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                new AlertDialog.Builder(this).setTitle("出错啦")
                    .setMessage("拉起模块设置失败, 请确认 " + pkg + " 已安装并启用(没有被关冰箱或被冻结停用)\n" + e.toString())
                    .setCancelable(true).setPositiveButton(android.R.string.ok, null).show();
            }
        }
    }

    public void handleClickEvent(View v) {
        switch (v.getId()) {
            case R.id.mainV2_githubRepo: {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/ferredoxin/QNotified"));
                startActivity(intent);
                break;
            }
            case R.id.mainV2_help: {
                new AlertDialog.Builder(this)
                    .setMessage(
                        "如模块无法使用，EdXp可尝试取消优化+开启兼容模式  ROOT用户可尝试 用幸运破解器-工具箱-移除odex更改 移除QQ与本模块的优化, 太极尝试取消优化")
                    .setCancelable(true).setPositiveButton(android.R.string.ok, null).show();
                break;
            }
            case R.id.mainV2_troubleshoot: {
                new AlertDialog.Builder(this)
                    .setTitle("你想要进入哪个App的故障排除")
                    .setItems(new String[] {"QQ", "TIM", "QQ极速版"}, (dialog, which) -> {
                        String pkg = null;
                        switch (which) {
                            case 0: {
                                pkg = HookEntry.PACKAGE_NAME_QQ;
                                break;
                            }
                            case 1: {
                                pkg = HookEntry.PACKAGE_NAME_TIM;
                                break;
                            }
                            case 2: {
                                pkg = HookEntry.PACKAGE_NAME_QQ_LITE;
                                break;
                            }
                            default: {
                            }
                        }
                        if (pkg != null) {
                            Intent intent = new Intent();
                            intent
                                .setComponent(new ComponentName(pkg, "com.tencent.mobileqq.activity.JumpActivity"));
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.putExtra(JumpActivityEntryHook.JUMP_ACTION_CMD,
                                JumpActivityEntryHook.JUMP_ACTION_START_ACTIVITY);
                            intent.putExtra(JumpActivityEntryHook.JUMP_ACTION_TARGET, "nil.nadph.qnotified.activity.TroubleshootActivity");
                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                new AlertDialog.Builder(this).setTitle("出错啦")
                                    .setMessage("拉起模块设置失败, 请确认 " + pkg + " 已安装并启用(没有被关冰箱或被冻结停用)\n" + e.toString())
                                    .setCancelable(true).setPositiveButton(android.R.string.ok, null).show();
                            }
                        }
                    })
                    .setPositiveButton(android.R.string.ok, null).show();
                break;
            }
            default: {
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMenuItems();
    }

    void updateMenuItems() {
        Menu menu = mainV2Binding.topAppBar.getMenu();
        if (menu != null) {
            menu.removeItem(R.id.mainV2_menuItem_toggleDesktopIcon);
            menu.add(Menu.CATEGORY_SYSTEM, R.id.mainV2_menuItem_toggleDesktopIcon, 0,
                isLauncherIconEnabled() ? "隐藏桌面图标" : "显示桌面图标");
        }
    }

    boolean isLauncherIconEnabled() {
        ComponentName componentName = new ComponentName(this, ALIAS_ACTIVITY_NAME);
        return ComponentUtilKt.getEnable(componentName, this);
    }

    @UiThread
    void setLauncherIconEnabled(boolean enabled) {
        ComponentName componentName = new ComponentName(this, ALIAS_ACTIVITY_NAME);
        ComponentUtilKt.setEnable(componentName, this, enabled);
    }
}
