/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */
package cc.ioctl.activity.stub;

import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.annotation.Nullable;
import cc.ioctl.script.gui.ActivityProxyHandlerStaticHolder;
import cc.ioctl.util.internal.XMethodHookDispatchUtil;
import de.robv.android.xposed.XC_MethodHook;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Map;
import nil.nadph.qnotified.activity.AppCompatTransferActivity;

public class ScriptAppCompatProxyActivity extends AppCompatTransferActivity {

    private Map<String, XMethodHookDispatchUtil.HookHolder> mH;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        String uuid = getIntent()
            .getStringExtra(ActivityProxyHandlerStaticHolder.TAG_ACTIVITY_PROXY_HANDLER);
        if (uuid == null) {
            throw new IllegalArgumentException(
                "UUID for ScriptAppCompatProxyActivity not specified in Intent");
        }
        {
            Map<String, XMethodHookDispatchUtil.HookHolder> t = ActivityProxyHandlerStaticHolder
                .consume(uuid);
            if (t != null) {
                mH = t;
            }
        }
        if (mH == null) {
            throw new IllegalStateException("proxy handler not found, not in the same process?");
        }
        XMethodHookDispatchUtil.HookHolder h = mH.get("onCreate(Landroid/os/Bundle;)V");
        if (h == null) {
            super.onCreate(savedInstanceState);
        } else {
            XC_MethodHook.MethodHookParam p = XMethodHookDispatchUtil
                .createParam(h.hook, h.method, this, savedInstanceState);
            if (!XMethodHookDispatchUtil.callBeforeHook(h.hook, p)) {
                try {
                    super.onCreate((Bundle) p.args[0]);
                    p.setResult(null);
                } catch (Throwable e) {
                    p.setThrowable(e);
                }
            }
            XMethodHookDispatchUtil.callAfterHook(h.hook, p);
            try {
                p.getResultOrThrowable();
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Throwable t) {
                throw new UndeclaredThrowableException(t);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState,
        @Nullable PersistableBundle persistentState) {
        String uuid = getIntent()
            .getStringExtra(ActivityProxyHandlerStaticHolder.TAG_ACTIVITY_PROXY_HANDLER);
        if (uuid == null) {
            throw new IllegalArgumentException(
                "UUID for ScriptAppCompatProxyActivity not specified in Intent");
        }
        {
            Map<String, XMethodHookDispatchUtil.HookHolder> t = ActivityProxyHandlerStaticHolder
                .consume(uuid);
            if (t != null) {
                mH = t;
            }
        }

        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onResume() {
        XMethodHookDispatchUtil.HookHolder h = mH.get("onResume()V");
        if (h == null) {
            super.onResume();
        } else {
            XC_MethodHook.MethodHookParam p = XMethodHookDispatchUtil
                .createParam(h.hook, h.method, this);
            if (!XMethodHookDispatchUtil.callBeforeHook(h.hook, p)) {
                try {
                    super.onResume();
                    p.setResult(null);
                } catch (Throwable e) {
                    p.setThrowable(e);
                }
            }
            XMethodHookDispatchUtil.callAfterHook(h.hook, p);
            try {
                p.getResultOrThrowable();
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Throwable t) {
                throw new UndeclaredThrowableException(t);
            }
        }
    }
}
