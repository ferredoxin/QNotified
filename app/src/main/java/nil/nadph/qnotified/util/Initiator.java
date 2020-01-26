package nil.nadph.qnotified.util;

@SuppressWarnings("rawtypes")
public class Initiator {

    private static ClassLoader qqClassLoader;

    public static void init(ClassLoader classLoader) {
        qqClassLoader = classLoader;
    }

    public static ClassLoader getClassLoader() {
        return qqClassLoader;
    }

    @Nullable
    public static Class<?> load(String className) {
        if (qqClassLoader == null || className == null || className.isEmpty()) {
            return null;
        }
        className = className.replace('/', '.');
        if (className.endsWith(";")) className = className.substring(0, className.length() - 1);
        if (className.charAt(0) == 'L' && className.charAt(1) >= 'a') className = className.substring(1);
        if (className.startsWith(".")) {
            className = Utils.PACKAGE_NAME_QQ + className;
        }
        try {
            return qqClassLoader.loadClass(className);
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
        return mPicItemBuilder;
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
                clazz = tmp.getDeclaredField("this$0").getType();
            } catch (Exception ignored) {
            }
        }
        if (clazz == null) {
            try {
                tmp = load("com.tencent.mobileqq.app.upgrade.UpgradeController$2");
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

    public static Class _QQMessageFacade() {
        return load("com/tencent/mobileqq/app/message/QQMessageFacade");
    }

    public static Class _SessionInfo() {
        return load("com/tencent/mobileqq/activity/aio/SessionInfo");
    }

    public static Class _MessageRecord() {
        return load("com/tencent/mobileqq/data/MessageRecord");
    }

    public static Class _QQAppInterface() {
        return load("com/tencent/mobileqq/app/QQAppInterface");
    }

    @Nullable
    public static Class _EmoAddedAuthCallback() {
        try {
            Class clz = load("com/tencent/mobileqq/emosm/favroaming/EmoAddedAuthCallback");
            if (clz == null) {
                Class cref = load("com/tencent/mobileqq/emosm/favroaming/EmoAddedAuthCallback$2");
                try {
                    clz = cref.getDeclaredField("this$0").getType();
                } catch (NoSuchFieldException ignored) {
                }
            }
            if (clz == null) {
                Class cref = load("com/tencent/mobileqq/emosm/favroaming/EmoAddedAuthCallback$1");
                try {
                    clz = cref.getDeclaredField("this$0").getType();
                } catch (NoSuchFieldException ignored) {
                }
            }
            return clz;
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Nullable
    public static Class _C2CMessageProcessor() {
        Class clz = load("com/tencent/mobileqq/app/message/C2CMessageProcessor");
        if (clz == null) {
            Class cref = load("com/tencent/mobileqq/app/message/C2CMessageProcessor$1");
            try {
                clz = cref.getDeclaredField("this$0").getType();
            } catch (NoSuchFieldException ignored) {
            }
        }
        if (clz == null) {
            Class cref = load("com/tencent/mobileqq/app/message/C2CMessageProcessor$5");
            try {
                clz = cref.getDeclaredField("this$0").getType();
            } catch (NoSuchFieldException ignored) {
            }
        }
        if (clz == null) {
            Class cref = load("com/tencent/mobileqq/app/message/C2CMessageProcessor$7");
            try {
                clz = cref.getDeclaredField("this$0").getType();
            } catch (NoSuchFieldException ignored) {
            }
        }
        return clz;
    }

}
