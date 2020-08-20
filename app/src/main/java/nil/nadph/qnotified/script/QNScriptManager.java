package nil.nadph.qnotified.script;

import android.view.View;
import android.widget.CompoundButton;
import bsh.EvalError;
import bsh.Interpreter;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class QNScriptManager {

    private static List<QNScript> scripts = new ArrayList<>();
    public static int enables = 0;
    public static boolean enableall = false;

    /**
     * 添加一个脚本
     *
     * @param file 文件
     */
    public static void addScript(String file) {
        if (Utils.isNullOrEmpty(file) || hasScript(file)) return;
        // to do
        // 操作: 将文件移动到软件数据文件夹下
    }

    /**
     * 判断脚本是否存在
     *
     * @param file 文件
     * @return 是否存在
     */
    public static boolean hasScript(String file) {
        if (Utils.isNullOrEmpty(file)) return false;
        // to do
        // 判断文件
        return true;
    }

    /**
     * 删除脚本
     *
     * @param file
     */
    public static void delScript(String file) {
        // to do
        // 删除文件
    }

    /**
     * 获取所有的脚本代码
     *
     * @return
     */
    public static List<String> getScriptCodes() {
        // to do
        // 返回全部脚本代码
        return new ArrayList<String>() {{
            try {
                add(Utils.readByReader(new BufferedReader(new InputStreamReader(Utils.toInputStream("/assets/demo.java")))));
            } catch (IOException e) {
                add("// 示例java\n" +
                        "        String name=\"示例脚本\";// 脚本名称\n" +
                        "        String label=\"demo\";// 脚本标签\n" +
                        "        String version=\"0.0.1\";// 脚本版本\n" +
                        "        String author=\"lliiooll\";// 脚本作者\n" +
                        "        String decs=\"用于QN脚本开发的入门示例\";// 脚本简介\n" +
                        "\n" +
                        "public void onLoad(){\n" +
                        "// 将会在脚本加载时调用，在这里注册hook\n" +
                        "        \n" +
                        "}\n" +
                        "public void onEnable(){\n" +
                        "// 将会在脚本启用时调用\n" +
                        "}\n" +
                        "public void onDisable(){\n" +
                        "// 将会在脚本禁用时调用\n" +
                        "}");
            }
        }};
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
        for (String code : getScriptCodes()) {
            try {
                execute(code);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public static void execute(String code) throws Throwable {
        Interpreter lp = new Interpreter();
        lp.eval(code);
        scripts.add(QNScript.create(lp, code));
    }

    public static void openSelect(View v) {
    }

    public static void changeGlobal(CompoundButton compoundButton, boolean b) {
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        cfg.putBoolean(ConfigItems.qn_script_global, b);
    }

    public static void enableAll() {
        enableall = true;
        for (QNScript qs : QNScriptManager.getScripts())
            if (!qs.isEnable()) {
                qs.setEnable(true);
                enables++;
            }

    }

    public static void disableAll() {
        enableall = false;
        for (QNScript qs : QNScriptManager.getScripts())
            if (qs.isEnable()) {
                qs.setEnable(false);
                enables--;
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
    }

    public static boolean isEnableAll() {
        return enableall;
    }
}
