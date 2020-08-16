package me.singleneuron.base;

import androidx.annotation.NonNull;
import me.singleneuron.data.BugReportArguments;
import nil.nadph.qnotified.remote.NAuthBugReportArgsImpl;
import nil.nadph.qnotified.util.NonUiThread;

import java.io.IOException;
import java.util.ArrayList;

public abstract class BaseBugReport {

    @NonNull
    public static BaseBugReport getInstance() {
        return new NAuthBugReportArgsImpl();
    }

    @NonNull
    @NonUiThread
    public abstract ArrayList<BugReportArguments> getBugReportArgumentsList() throws IOException;
}
