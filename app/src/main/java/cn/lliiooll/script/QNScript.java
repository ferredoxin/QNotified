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
package cn.lliiooll.script;


import java.io.*;

import bsh.*;
import cn.lliiooll.*;
import cn.lliiooll.params.*;
import nil.nadph.qnotified.config.*;
import nil.nadph.qnotified.util.*;

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
            instance = new Interpreter();
            getInstance().setClassLoader(QNClient.class.getClassLoader());
            getInstance().setStrictJava(false);
            getInstance().eval(code);
            execute = true;
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
        return execute;
    }
    
    public boolean isEnable() {
        return ConfigManager.getDefaultConfig().getBooleanOrDefault("qn_script_scripts#" + getInfo().getLabel(),
            enable);
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
        return instance;
    }
    
    public CharSequence getEnable() {
        return isEnable() ? "[启用]" : "[禁用]";
    }
}
