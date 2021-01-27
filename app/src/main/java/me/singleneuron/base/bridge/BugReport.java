package me.singleneuron.base.bridge;

import androidx.annotation.NonNull;

import java.io.*;
import java.util.*;

import me.singleneuron.data.*;
import nil.nadph.qnotified.remote.*;
import nil.nadph.qnotified.util.*;

public abstract class BugReport {
    
    @NonNull
    public static BugReport getInstance() {
        return new NAuthBugReportArgsImpl();
    }
    
    @NonNull
    @NonUiThread
    public abstract ArrayList<BugReportArguments> getBugReportArgumentsList() throws IOException;
}
