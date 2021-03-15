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
package cc.ioctl.hook;

import static me.singleneuron.util.QQVersion.QQ_8_2_0;
import static nil.nadph.qnotified.util.Initiator._EmoAddedAuthCallback;
import static nil.nadph.qnotified.util.Initiator._FavEmoRoamingHandler;
import static nil.nadph.qnotified.util.ReflexUtil.iput_object;
import static nil.nadph.qnotified.util.ReflexUtil.sput_object;
import static nil.nadph.qnotified.util.Utils.log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Method;
import java.util.List;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;

@FunctionEntry
public class FavMoreEmo extends CommonDelayableHook {

    public static final FavMoreEmo INSTANCE = new FavMoreEmo();

    FavMoreEmo() {
        super("qqhelper_fav_more_emo", new DexDeobfStep(DexKit.C_FAV_EMO_CONST));
    }

    @Override
    public boolean initOnce() {
        try {
            final Class mEmoAddedAuthCallback = _EmoAddedAuthCallback();
            final Class mFavEmoRoamingHandler = _FavEmoRoamingHandler();
            if (mEmoAddedAuthCallback == null) {
                if (mFavEmoRoamingHandler == null) {
                    setEmoNum();
                } else {
                    XposedHelpers
                        .findAndHookMethod(mFavEmoRoamingHandler, "a", List.class, List.class,
                            new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param)
                                    throws Throwable {
                                    setEmoNum();
                                }
                            });
                }
            } else {
                Class mUpCallBack$SendResult = null;
                for (Method m : mEmoAddedAuthCallback.getDeclaredMethods()) {
                    if (m.getName().equals("b") && m.getReturnType().equals(void.class)
                        && m.getParameterTypes().length == 1) {
                        mUpCallBack$SendResult = m.getParameterTypes()[0];
                        break;
                    }
                }
                XposedHelpers.findAndHookMethod(mEmoAddedAuthCallback, "b", mUpCallBack$SendResult,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            Object msg = param.args[0];
                            iput_object(msg, "a", int.class, 0);
                        }
                    });
                XposedHelpers.findAndHookMethod(mFavEmoRoamingHandler, "a", List.class, List.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            setEmoNum();
                        }
                    });
            }
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    private void setEmoNum() {
        Class mFavEmoConstant = DexKit.doFindClass(DexKit.C_FAV_EMO_CONST);
        sput_object(mFavEmoConstant, "a", 800);
        sput_object(mFavEmoConstant, "b", 800);
    }

    @Override
    public boolean isValid() {
        return !HostInformationProviderKt.hostInfo.isTim()
            && HostInformationProviderKt.hostInfo.getVersionCode() < QQ_8_2_0;
    }
}
