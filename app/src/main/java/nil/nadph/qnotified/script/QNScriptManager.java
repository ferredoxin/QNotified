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
package nil.nadph.qnotified.script;

import android.widget.*;

import java.io.*;
import java.util.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import bsh.EvalError;
import bsh.Interpreter;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.Utils;
import bsh.*;
import cn.lliiooll.script.*;
import cn.lliiooll.utils.*;
import nil.nadph.qnotified.config.*;
import nil.nadph.qnotified.util.*;

import static nil.nadph.qnotified.util.Utils.*;

public class QNScriptManager {

    public static int enables = 0;
    public static boolean enableall = false;
    public static String scriptsPath;
    public static String error = "啥也没";
    private static List<QNScript> scripts = new ArrayList<>();
    private static boolean init = false;

    /**
     * 添加一个脚本
     *
     * @param file 文件
     * @return
     */
    public static String addScript(String file) throws Exception {
        if (isNullOrEmpty(file)) return "file is null";
        if (hasScript(file)) return "脚本已存在";
        // todo
        // 操作: 将文件移动到软件数据文件夹下
        File s = new File(file);
        File dir = new File(scriptsPath);
        if (!dir.exists()) dir.mkdirs();
        File f = new File(dir, s.getName());
        Utils.copy(s, f);
        String code = readByReader(new FileReader(f));
        if (!isNullOrEmpty(code)) {
            scripts.add(QNScriptFactory.parse(new FileInputStream(f)));
        }
        return "";
    }

    public static String addScriptFD(FileDescriptor fileDescriptor, String scriptName) throws Throwable {
        File dir = new File(scriptsPath);
        if (!dir.exists()) dir.mkdirs();
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(fileDescriptor);
            StringBuffer stringBuffer = new StringBuffer();
            byte[] buf = new byte[1024];
            int len;
            while ((len = fileInputStream.read(buf)) > 0) {
                stringBuffer.append(new String(buf, 0, len));
            }
            if (hasScriptStr(stringBuffer.toString())) return "脚本已存在";
            fileOutputStream = new FileOutputStream(scriptsPath + scriptName);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(stringBuffer.toString());
            outputStreamWriter.close();
            fileOutputStream.flush();
        } finally {
            if (fileInputStream != null) fileInputStream.close();
            if (fileOutputStream != null) fileOutputStream.close();
        }
        String code = readByReader(new FileReader(scriptsPath + scriptName));
        if (!isNullOrEmpty(code)) {
            scripts.add(QNScriptFactory.parse(new FileInputStream(new File(scriptsPath + scriptName))));
        }
        return "";
    }

    public static void addEnable() {
        enables++;
        if (enables > scripts.size() - 1) enables = scripts.size();
    }

    public static void delEnable() {
        enables--;
        if (enables < 0) enables = 0;
    }

    /**
     * 判断脚本是否存在
     *
     * @param file 文件
     * @return 是否存在
     */
    public static boolean hasScript(String file) throws Exception {
        if (Utils.isNullOrEmpty(file)) return false;
        // to do
        // 判断文件
        QNScriptInfo info = QNScriptInfo.parse(QNScriptUtils.readLines(new FileInputStream(new File(file))));
        if (info == null) throw new RuntimeException("不是有效的脚本文件");
        for (QNScript q : getScripts()) {
            if (info.getLabel().equalsIgnoreCase(q.getInfo().getLabel())) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasScriptStr(String code) throws Exception {
        QNScriptInfo info = QNScriptInfo.parse(new ArrayList<>(Arrays.asList(code.split("\n"))));
        if (info == null) throw new RuntimeException("不是有效的脚本文件");
        for (QNScript q : getScripts()) {
            if (info.getLabel().equalsIgnoreCase(q.getInfo().getLabel())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除脚本
     *
     * @param script
     * @return
     */
    public static boolean delScript(QNScript script) {
        // to do
        // 删除文件
        File dir = new File(scriptsPath);
        if (!dir.exists()) dir.mkdirs();
        if (!dir.isDirectory()) {
            log(new RuntimeException("脚本文件夹不应为一个文件"));
            return false;
        }
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) continue;
            try {
                QNScriptInfo info = QNScriptInfo.parse(QNScriptUtils.readLines(f));
                if (info.getLabel().equalsIgnoreCase(script.getInfo().getLabel())) {
                    f.delete();
                    return true;
                }
            } catch (Exception e) {
                log(e);
            }
        }
        for (QNScript q : scripts) {
            if (q.getInfo().getLabel().equalsIgnoreCase(script.getInfo().getLabel())) {
                scripts.remove(q);
            }
        }
        return false;
    }
    
    /**
     * 获取所有的脚本代码
     *
     * @return
     */
    
    @Deprecated
    public static List<String> getScriptCodes() {
        // to do
        // 返回全部脚本代码
        List<String> codes = new ArrayList<String>() {{
            try {
                add(Utils.readByReader(new InputStreamReader(Utils.toInputStream("demo.java"))));
            } catch (IOException e) {
                log(e);
            }
        }};
        File dir = new File(scriptsPath);
        if (!dir.exists()) dir.mkdirs();
        if (!dir.isDirectory()) {
            log(new RuntimeException("脚本文件夹不应为一个文件"));
            return codes;
        }
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) continue;
            try {
                String code = Utils.readByReader(new FileReader(f));
                if (!Utils.isNullOrEmpty(code)) {
                    codes.add(code);
                }
            } catch (Exception e) {
                log(e);
            }
        }
        return codes;
    }
    
    /**
     * 获取所有的脚本输入流
     *
     * @return 所有的脚本输入流
     */
    public static List<InputStream> getScriptInputStreams() {
        // to do
        // 返回全部脚本代码
        List<InputStream> codes = new ArrayList<InputStream>() {{
            add(Utils.toInputStream("demo.java"));
        }};
        File dir = new File(scriptsPath);
        if (!dir.exists()) dir.mkdirs();
        if (!dir.isDirectory()) {
            log(new RuntimeException("脚本文件夹不应为一个文件"));
            return codes;
        }
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) continue;
            try {
                codes.add(new FileInputStream(f));
            } catch (Exception e) {
                log(e);
            }
        }
        return codes;
    }
    
    /**
     * 获取所有的脚本
     *
     * @return
     */
    public static List<QNScript> getScripts() {
        return scripts;
    }
    
    public static void init() {
        if (init) return;
        scriptsPath = HostInformationProviderKt.getHostInformationProvider().getApplicationContext().getFilesDir().getAbsolutePath() + "/qn_script/";
        for (InputStream is : getScriptInputStreams()) {
            try {
                QNScript qs = QNScriptFactory.parse(is);
                scripts.add(qs);
                if (qs.isEnable()) {
                    QNScriptFactory.initScript(qs);
                }
            } catch (EvalError e) {
                log(e);
            }
        }
        
        
        init = true;
    }


    public static void changeGlobal(CompoundButton compoundButton, boolean b) {
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        cfg.putBoolean(ConfigItems.qn_script_global, b);
        try {
            cfg.save();
        } catch (IOException e) {
            log(e);
        }
    }

    public static void enableAll() {
        enableall = true;
        for (QNScript qs : QNScriptManager.getScripts())
            if (!qs.isEnable()) {
                try {
                    QNScriptFactory.enable(qs);
                    addEnable();
                } catch (EvalError evalError) {
                    Utils.log(evalError);
                }
    
            }

    }

    public static void disableAll() {
        enableall = false;
        for (QNScript qs : QNScriptManager.getScripts())
            if (qs.isEnable()) {
                QNScriptFactory.disable(qs);
                delEnable();
            }

    }

    public static int getAllCount() {
        return scripts.size();
    }

    public static int getEnableCount() {
        return enables;
    }

    public static void enableAll(CompoundButton compoundButton, boolean b) {
        if (b) enableAll();
        else disableAll();
        Utils.showToast(compoundButton.getContext(), Utils.TOAST_TYPE_ERROR, "重启" + HostInformationProviderKt.getHostInformationProvider().getHostName() + "生效", Toast.LENGTH_SHORT);
    }

    public static boolean isEnableAll() {
        return enableall;
    }
}
