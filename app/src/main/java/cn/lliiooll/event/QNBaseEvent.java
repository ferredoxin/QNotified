package cn.lliiooll.event;

import cn.lliiooll.params.BaseParams;

public abstract class QNBaseEvent {

    private final String method;
    private final String param;

    public QNBaseEvent(String methodName, String paramName) {
        this.method = methodName;
        this.param = paramName;
    }

    public abstract BaseParams doParse();


    public String getParamName() {
        return this.param;
    }

    public String getMethodName() {
        return this.method;
    }
}
