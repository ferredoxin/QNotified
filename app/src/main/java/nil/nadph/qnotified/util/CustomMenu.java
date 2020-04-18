package nil.nadph.qnotified.util;

import java.lang.reflect.Field;

import static nil.nadph.qnotified.util.Utils.log;

public class CustomMenu {

    public static Object createItem(Class<?> clazz, int id, String title) {
        try {
            Object item = clazz.newInstance();
            Field f;
            f = Utils.findField(clazz, int.class, "id");
            if (f == null) f = Utils.findField(clazz, int.class, "a");
            f.setAccessible(true);
            f.set(item, id);
            f = Utils.findField(clazz, String.class, "title");
            if (f == null) f = Utils.findField(clazz, String.class, "a");
            f.setAccessible(true);
            f.set(item, title);
            return item;
        } catch (Exception e) {
            log(e);
            return null;
        }
    }
}
