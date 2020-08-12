package me.singleneuron.data;

import java.util.ArrayList;

public class BugReportArguments {

    public String key; //must be [a-z]|[0-9]|_
    public String name; //Chinese, display to user
    public String description;
    //public boolean multiple;
    public String[] choices;

}
