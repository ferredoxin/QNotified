package nil.nadph.qnotified.adapter;

import android.content.Intent;
import android.os.Bundle;

public interface ActivityAdapter {
    void doOnPostCreate(Bundle savedInstanceState) throws Throwable;

    void doOnPostResume() throws Throwable;

    void doOnPostPause() throws Throwable;

    void doOnPostDestory() throws Throwable;

    void doOnPostActivityResult(int requestCode, int resultCode, Intent data) throws Throwable;

    boolean isWrapContent() throws Throwable;
}
