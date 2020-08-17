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
    /**
     * 添加一个脚本
     *
     * @param c
     */
    public static void addScript(String c) {
        if (Utils.isNullOrEmpty(c) || hasScript(c)) return;
        // to do
    }

    /**
     * 判断脚本是否存在
     *
     * @param code
     * @return
     */
    public static boolean hasScript(String code) {
        if (Utils.isNullOrEmpty(code)) return false;
        // to do
        return true;
    }

    /**
     * 判断脚本是否存在
     *
     * @param order
     * @return
     */
    public static boolean hasScript(int order) {
        return hasScript(ConfigManager.getDefaultConfig().getStringOrDefault(ConfigItems.qn_script_code + order, ""));
    }

    /**
     * 删除脚本
     *
     * @param code
     */
    public static void delScript(String code) {
        // to do
    }

    /**
     * 删除脚本
     *
     * @param order
     */
    public static void delScript(int order) {
        delScript(ConfigManager.getDefaultConfig().getStringOrDefault(ConfigItems.qn_script_code + order, ""));
    }

    /**
     * 获取所有的脚本
     *
     * @return
     */
    public static List<String> getScripts() {
        // to do
        return new ArrayList<>();
    }

    public static void init() {
        for (String code : getScripts()) {
            try {
                execute(code);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private static void execute(String code) throws Throwable {
        Interpreter ip = new Interpreter();
        ip.eval(code);
        //ip.source("./script.java");
        QNParam qp = ParamBuilder.builder()
                .setContent("测试消息")
                .setSenderUin(123456L)
                .seUin(654321L)
                .build();
       // ip.set("param", qp);
        ip.eval("qwq(param)");
    }
}
