package nil.nadph.qnotified.remote;

import androidx.annotation.*;

import java.io.*;
import java.util.*;

import me.singleneuron.base.bridge.*;
import me.singleneuron.data.*;

public class NAuthBugReportArgsImpl extends BugReport {
    @NonNull
    @Override
    public ArrayList<BugReportArguments> getBugReportArgumentsList() throws IOException {
        GetBugReportArgsResp resp = TransactionHelper.doGetBugReportArgs();
        return new ArrayList<>(Arrays.asList(resp.args));
    }
}
