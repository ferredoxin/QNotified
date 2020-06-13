/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 cinit@github.com
 * https://github.com/cinit/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.util;

import android.os.Parcelable;
import com.tencent.mobileqq.app.QQAppInterface;
import de.robv.android.xposed.XposedBridge;

import java.lang.reflect.Field;

import static nil.nadph.qnotified.util.Utils.log;

@SuppressWarnings("rawtypes")
public class Initiator {

    private static ClassLoader sHostClassLoader;
    private static ClassLoader sPluginClassLoader;

    public static void init(ClassLoader classLoader) {
        if (classLoader == null) throw new NullPointerException("classLoader == null");
        try {
            sHostClassLoader = classLoader;
            Field fParent = ClassLoader.class.getDeclaredField("parent");
            fParent.setAccessible(true);
            ClassLoader mine = Initiator.class.getClassLoader();
            ClassLoader curr = (ClassLoader) fParent.get(mine);
            if (curr == null) {
                curr = XposedBridge.class.getClassLoader();
            }
            if (!curr.getClass().getName().equals(HybridClassLoader.class.getName())) {
                fParent.set(mine, sPluginClassLoader = new HybridClassLoader(curr, classLoader));
            }
        } catch (Exception e) {
            log(e);
        }
    }

    public static ClassLoader getPluginClassLoader() {
        return sPluginClassLoader;
    }

    @Deprecated
    public static ClassLoader getClassLoader() {
        return sPluginClassLoader;
    }

    public static ClassLoader getHostClassLoader() {
        return sHostClassLoader;
    }

    @Nullable
    public static Class<?> load(String className) {
        if (sPluginClassLoader == null || className == null || className.isEmpty()) {
            return null;
        }
        className = className.replace('/', '.');
        if (className.endsWith(";")) {
            if (className.charAt(0) == 'L') className = className.substring(1, className.length() - 1);
            else className = className.substring(0, className.length() - 1);
        }
        if (className.startsWith(".")) {
            className = Utils.PACKAGE_NAME_QQ + className;
        }
        try {
            return sPluginClassLoader.loadClass(className);
        } catch (Throwable e) {
            return null;
        }
    }

    public static Class _PicItemBuilder() {
        Class tmp;
        Class mPicItemBuilder = load("com.tencent.mobileqq.activity.aio.item.PicItemBuilder");
        if (mPicItemBuilder == null) {
            try {
                tmp = load("com.tencent.mobileqq.activity.aio.item.PicItemBuilder$7");
                mPicItemBuilder = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        if (mPicItemBuilder == null) {
            try {
                tmp = load("com.tencent.mobileqq.activity.aio.item.PicItemBuilder$6");
                mPicItemBuilder = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        if (mPicItemBuilder == null) {
            try {
                tmp = load("com.tencent.mobileqq.activity.aio.item.PicItemBuilder$8");
                mPicItemBuilder = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        if (mPicItemBuilder == null) {
            try {
                tmp = load("com.tencent.mobileqq.activity.aio.item.PicItemBuilder$3");
                mPicItemBuilder = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        return mPicItemBuilder;
    }

    public static Class _TroopGagMgr() {
        Class tmp;
        Class ret = load("com.tencent.mobileqq.troop.utils.TroopGagMgr");
        if (ret == null) {
            try {
                tmp = load("com.tencent.mobileqq.troop.utils.TroopGagMgr$1");
                ret = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        if (ret == null) {
            try {
                tmp = load("com.tencent.mobileqq.troop.utils.TroopGagMgr$2");
                ret = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        return ret;
    }

    public static Class _TextItemBuilder() {
        Class tmp;
        Class mTextItemBuilder = load("com/tencent/mobileqq/activity/aio/item/TextItemBuilder");
        if (mTextItemBuilder == null) {
            try {
                tmp = load("com/tencent/mobileqq/activity/aio/item/TextItemBuilder$10");
                mTextItemBuilder = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        if (mTextItemBuilder == null) {
            try {
                tmp = load("com/tencent/mobileqq/activity/aio/item/TextItemBuilder$6");
                mTextItemBuilder = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        return mTextItemBuilder;
    }

    public static Class _UpgradeController() {
        Class tmp;
        Class clazz = load("com.tencent.mobileqq.app.upgrade.UpgradeController");
        if (clazz == null) {
            try {
                tmp = load("com.tencent.mobileqq.app.upgrade.UpgradeController$1");
                if (tmp == null) tmp = load("com.tencent.mobileqq.upgrade.UpgradeController$1");
                clazz = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        if (clazz == null) {
            try {
                tmp = load("com.tencent.mobileqq.app.upgrade.UpgradeController$2");
                if (tmp == null) tmp = load("com.tencent.mobileqq.upgrade.UpgradeController$2");
                clazz = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        return clazz;
    }

    public static Class _TroopPicEffectsController() {
        Class tmp;
        Class clazz = load("com/tencent/mobileqq/trooppiceffects/TroopPicEffectsController");
        if (clazz == null) {
            try {
                tmp = load("com/tencent/mobileqq/trooppiceffects/TroopPicEffectsController$2");
                clazz = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        return clazz;
    }

    public static Class _BannerManager() {
        Class tmp;
        Class clazz = load("com.tencent.mobileqq.activity.recent.BannerManager");
        for (int i = 38; clazz == null && i < 42; i++) {
            try {
                tmp = load("com.tencent.mobileqq.activity.recent.BannerManager$" + i);
                clazz = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        return clazz;
    }

    public static Class _PttItemBuilder() {
        Class cl_PttItemBuilder = load("com/tencent/mobileqq/activity/aio/item/PttItemBuilder");
        if (cl_PttItemBuilder == null) {
            Class cref = load("com/tencent/mobileqq/activity/aio/item/PttItemBuilder$2");
            try {
                cl_PttItemBuilder = cref.getDeclaredField("this$0").getType();
            } catch (NoSuchFieldException ignored) {
            }
        }
        return cl_PttItemBuilder;
    }

    public static Class _TroopGiftAnimationController() {
        Class cl_TroopGiftAnimationController = load("com.tencent.mobileqq.troopgift.TroopGiftAnimationController");
        if (cl_TroopGiftAnimationController == null) {
            Class cref = load("com.tencent.mobileqq.troopgift.TroopGiftAnimationController$1");
            try {
                cl_TroopGiftAnimationController = cref.getDeclaredField("this$0").getType();
            } catch (NoSuchFieldException ignored) {
            }
        }
        return cl_TroopGiftAnimationController;
    }

    public static Class _FavEmoRoamingHandler() {
        Class clz = load("com/tencent/mobileqq/app/FavEmoRoamingHandler");
        if (clz == null) {
            Class cref = load("com/tencent/mobileqq/app/FavEmoRoamingHandler$1");
            try {
                clz = cref.getDeclaredField("this$0").getType();
            } catch (NoSuchFieldException ignored) {
            }
        }
        return clz;
    }

    public static Class _StartupDirector() {
        Class director = load("com/tencent/mobileqq/startup/director/StartupDirector");
        if (director == null) {
            try {
                director = load("com/tencent/mobileqq/startup/director/StartupDirector$1").getDeclaredField("this$0").getType();
            } catch (NoSuchFieldException ignored) {
            }
        }
        return director;
    }

    public static Class _QQMessageFacade() {
        Class<?> cFacade = load("com/tencent/mobileqq/app/message/QQMessageFacade");
        if (cFacade != null) {
            return cFacade;
        }
        return load("com/tencent/imcore/message/QQMessageFacade");
    }

    public static <T extends Parcelable> Class<T> _SessionInfo() {
        return (Class<T>) load("com/tencent/mobileqq/activity/aio/SessionInfo");
    }

    public static Class _BaseChatPie() {
        return load("com.tencent.mobileqq.activity.BaseChatPie");
    }

    public static Class _ChatMessage() {
        return load("com.tencent.mobileqq.data.ChatMessage");
    }

    public static Class<?> _MessageRecord() {
        return load("com/tencent/mobileqq/data/MessageRecord");
    }

    public static Class<QQAppInterface> _QQAppInterface() {
        return (Class<QQAppInterface>) load("com/tencent/mobileqq/app/QQAppInterface");
    }

    public static Class<?> _BaseMessageManager() {
        Class<?> clz = load("com/tencent/mobileqq/app/message/BaseMessageManager");
        if (clz != null) return clz;
        clz = load("com/tencent/imcore/message/BaseMessageManager");
        if (clz != null) return clz;
        Class<?> ref = load("com/tencent/imcore/message/BaseMessageManager$1");
        if (ref != null) {
            try {
                clz = ref.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        if (clz != null) return clz;
        ref = load("com/tencent/imcore/message/BaseMessageManager$2");
        if (ref != null) {
            try {
                clz = ref.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        return clz;
    }

    @Nullable
    public static Class _EmoAddedAuthCallback() {
        try {
            Class clz = load("com/tencent/mobileqq/emosm/favroaming/EmoAddedAuthCallback");
            if (clz == null) {
                Class cref = load("com/tencent/mobileqq/emosm/favroaming/EmoAddedAuthCallback$2");
                try {
                    clz = cref.getDeclaredField("this$0").getType();
                } catch (Exception ignored) {
                }
            }
            if (clz == null) {
                Class cref = load("com/tencent/mobileqq/emosm/favroaming/EmoAddedAuthCallback$1");
                try {
                    clz = cref.getDeclaredField("this$0").getType();
                } catch (Exception ignored) {
                }
            }
            return clz;
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Nullable
    public static Class _C2CMessageProcessor() {
        Class<?> ret, cref;
        for (String clzName : new String[]{"com/tencent/mobileqq/app/message/C2CMessageProcessor",
                "com/tencent/imcore/message/C2CMessageProcessor"}) {
            ret = load(clzName);
            if (ret != null) return ret;
            for (int i : new int[]{4, 6, 1, 5, 7}) {
                cref = load(clzName + "$" + i);
                if (cref != null) {
                    try {
                        return cref.getDeclaredField("this$0").getType();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        log("Initiator/E class C2CMessageProcessor not found");
        return null;
    }
}
