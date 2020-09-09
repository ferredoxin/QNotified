package me.singleneuron.base.bridge;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;

import me.singleneuron.data.BugReportArguments;
import nil.nadph.qnotified.remote.NAuthBugReportArgsImpl;
import nil.nadph.qnotified.util.NonUiThread;

public abstract class BugReport {

    @NonNull
    public static BugReport getInstance() {
        return new NAuthBugReportArgsImpl();
    }

    @NonNull
    @NonUiThread
    public abstract ArrayList<BugReportArguments> getBugReportArgumentsList() throws IOException;
}
