package me.singleneuron.base;

import java.util.ArrayList;

import me.singleneuron.data.BugReportArguments;

public abstract class BaseBugReport {

    public static BaseBugReport getInstance() {
        // Todo
        //return new BaseBugReport();
        return null;
    }

    public abstract ArrayList<BugReportArguments> getBugReportArgumentsList();

}
