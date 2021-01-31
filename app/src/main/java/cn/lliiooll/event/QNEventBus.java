package cn.lliiooll.event;

import bsh.EvalError;
import cn.lliiooll.script.QNScript;
import cn.lliiooll.script.QNScriptFactory;

public class QNEventBus {
    public static void broadcast(QNBaseEvent event) throws EvalError {
        for (QNScript script : QNScriptFactory.enables.values()) {
            script.execute(event.getParamName(), event.doParse());
            script.execute(event.getMethodName() + "()");
        }
    }
}
