package cn.lliiooll.script;

import bsh.EvalError;
import cn.lliiooll.QNClient;
import cn.lliiooll.utils.QNScriptUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;

public class QNScriptFactory {

    public static HashMap<String, QNScript> enables = new HashMap<>();
    public static HashMap<String, QNScript> scripts = new HashMap<>();

    /**
     * 从InputStream中读取脚步文件并解析成QNScript对象
     *
     * @param is 输入流
     * @return QNScript对象
     */
    public static QNScript parse(InputStream is) {
        QNScript script = new QNScript();
        List<String> lines = QNScriptUtils.readLines(is);
        if (QNScriptFactory.isQNScript(is)) {
            QNScriptInfo info = QNScriptInfo.parse(lines);
            script.setInfo(info);
            script.setCode(QNScriptUtils.parseListToString(lines));
        }
        return script;
    }

    /**
     * 初始化一个脚本(在脚本启用后)
     *
     * @param script 脚本
     * @throws EvalError 脚本解析错误
     */
    public static void initScript(QNScript script) throws EvalError {
        if (script.isEnable())
            script.execute();
        script.execute("onLoad()");
    }

    /**
     * 启用一个脚本
     *
     * @param script 脚本
     * @throws EvalError 脚本解析错误
     */
    public static void enable(QNScript script) throws EvalError {
        script.setEnable(true);
        initScript(script);
        enables.put(script.getInfo().getLabel(), script);
    }

    /**
     * 初始化
     */
    public static void init() throws FileNotFoundException, EvalError {
       // File dir = new File("plugins");
        //for (File f : dir.listFiles()) {
            //if (f.getName().endsWith(".java")) {
              // QNScript script = QNScriptFactory.parse(new FileInputStream(f));
              //  scripts.put(script.getInfo().getLabel(), script);
                ////测试代码
               // enable(script);
           // }
      //  }
    }

    /**
     * 禁用一个脚本
     *
     * @param script 脚本
     */
    public static void disable(QNScript script) {
        script.setEnable(false);
        enables.remove(script.getInfo().getLabel());
    }


    public static boolean isQNScript(InputStream is) {
        //TODO: 判断文件类型及是否有标识
        return true;
    }

    public static QNScript parse(String code) {
        return QNScriptFactory.parse(new StringBufferInputStream(code));
    }
}
