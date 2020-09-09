package nil.nadph.qnotified.remote;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import me.singleneuron.base.bridge.BugReport;
import me.singleneuron.data.BugReportArguments;

public class NAuthBugReportArgsImpl extends BugReport {
    @NonNull
    @Override
    public ArrayList<BugReportArguments> getBugReportArgumentsList() throws IOException {
        GetBugReportArgsResp resp = TransactionHelper.doGetBugReportArgs();
        return new ArrayList<>(Arrays.asList(resp.args));
    }
}
