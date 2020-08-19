package nil.nadph.qnotified.script;

import bsh.EvalError;
import bsh.Interpreter;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QNScriptManager {

    private static List<QNScript> scripts = new ArrayList<>();

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
        return new ArrayList<>();
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
        scripts.add(QNScript.create(lp));
    }
}
