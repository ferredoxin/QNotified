package nil.nadph.qnotified.remote;

import androidx.annotation.NonNull;
import me.singleneuron.base.BaseBugReport;
import me.singleneuron.data.BugReportArguments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class NAuthBugReportArgsImpl extends BaseBugReport {
    @NonNull
    @Override
    public ArrayList<BugReportArguments> getBugReportArgumentsList() throws IOException {
        GetBugReportArgsResp resp = TransactionHelper.doGetBugReportArgs();
        return new ArrayList<>(Arrays.asList(resp.args));
    }
}
