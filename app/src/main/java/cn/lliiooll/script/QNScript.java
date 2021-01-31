package cn.lliiooll.script;


import bsh.EvalError;
import bsh.Interpreter;
import cn.lliiooll.QNClient;
import cn.lliiooll.params.BaseParams;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.util.Utils;

import java.io.IOException;

public class QNScript {

    private QNScriptInfo info;// 脚本信息
    private String code;// 脚本代码
    private Interpreter instance;
    private boolean execute = false;
    private boolean enable = false;

    public void setInfo(QNScriptInfo info) {
        this.info = info;
    }

    public QNScriptInfo getInfo() {
        return info;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void execute() throws EvalError {
        if (isEnable()) {
            this.instance = new Interpreter();
            getInstance().setClassLoader(QNClient.class.getClassLoader());
            getInstance().setStrictJava(false);
            getInstance().eval(this.code);
            this.execute = true;
        }

    }

    public void execute(String code) throws EvalError {
        if (isEnable() && isExecute()) {
            getInstance().eval(code);
        }
    }

    public void execute(String name, BaseParams param) throws EvalError {
        if (isEnable() && isExecute()) {
            getInstance().set(name, param);
        }
    }

    public boolean isExecute() {
        return this.execute;
    }

    public boolean isEnable() {
        return ConfigManager.getDefaultConfig().getBooleanOrDefault("qn_script_scripts#" + getInfo().getLabel(), this.enable);
    }

    public void setEnable(boolean enable) {
        //TODO: 修改配置文件
        ConfigManager mgr = ConfigManager.getDefaultConfig();
        mgr.getAllConfig().put("qn_script_scripts#" + getInfo().getLabel(), enable);
        try {
            mgr.save();
        } catch (IOException e) {
            Utils.log(e);
        }
        this.enable = enable;
    }

    public Interpreter getInstance() {
        return this.instance;
    }

    public CharSequence getEnable() {
        return isEnable() ? "[启用]" : "[禁用]";
    }
}
