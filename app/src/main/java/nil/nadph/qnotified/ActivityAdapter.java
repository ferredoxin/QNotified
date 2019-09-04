package nil.nadph.qnotified;

import android.content.Intent;
import android.os.Bundle;

public interface ActivityAdapter {
    void doOnPostCreate(Bundle savedInstanceState) throws Throwable;

    void doOnPostResume() throws Throwable;

    void doOnPostPause() throws Throwable;

    void doOnPostDestory() throws Throwable;

    void doOnPostActivityResult(int requestCode, int resultCode, Intent data);

    boolean isWrapContent() throws Throwable;
}
