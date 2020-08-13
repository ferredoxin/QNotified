package nil.nadph.qnotified.remote;

import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;
import com.qq.taf.jce.JceStruct;
import me.singleneuron.data.BugReportArguments;
import nil.nadph.qnotified.util.NonNull;

public class GetBugReportArgsResp extends JceStruct {

    private static final BugReportArguments[] DUMMY_ARGS_ARRAY = new BugReportArguments[]{new BugReportArguments()};

    @NonNull
    @JceId(0)
    public BugReportArguments[] args = DUMMY_ARGS_ARRAY;

    public GetBugReportArgsResp() {
    }

    @Override
    public void writeTo(JceOutputStream os) {
        os.write(args, 0);
    }

    @Override
    public void readFrom(JceInputStream is) {
        args = is.readArray(DUMMY_ARGS_ARRAY, 0, true);
    }
}
