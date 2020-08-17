package me.singleneuron.data;

import androidx.annotation.NonNull;
import com.qq.taf.jce.JceInputStream;
import com.qq.taf.jce.JceOutputStream;
import com.qq.taf.jce.JceStruct;
import nil.nadph.qnotified.remote.JceId;
import nil.nadph.qnotified.remote.Utf8JceUtils;

public class BugReportArguments extends JceStruct {

    @NonNull
    @JceId(0)
    public String key = ""; //must be [a-zA-Z0-9_]{1,63}
    @NonNull
    @JceId(1)
    public String name = ""; //Chinese, display to user
    @NonNull
    @JceId(2)
    public String description = "";
    @NonNull
    @JceId(3)
    public String[] choices = Utf8JceUtils.DUMMY_STRING_ARRAY;
//    @JceId(4)  public boolean multiple;

    @Override
    public void writeTo(JceOutputStream os) {
        os.write(key, 0);
        os.write(name, 1);
        os.write(description, 2);
        os.write(choices, 3);
    }

    @Override
    public void readFrom(JceInputStream is) {
        key = is.read("", 0, true);
        name = is.read("", 1, true);
        description = is.read("", 2, true);
        choices = is.read(Utf8JceUtils.DUMMY_STRING_ARRAY, 3, true);
    }
}
