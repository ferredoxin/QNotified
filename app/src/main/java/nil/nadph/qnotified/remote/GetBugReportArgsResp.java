package nil.nadph.qnotified.remote;

import com.qq.taf.jce.*;

import me.singleneuron.data.*;
import nil.nadph.qnotified.util.*;

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
