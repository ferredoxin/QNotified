package nil.nadph.qnotified.util;

import android.view.View;
import nil.nadph.qnotified.record.ConfigManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class DexKit {

    public static final int C_DIALOG_UTIL = 1;
    public static final int C_FACADE = 2;
    public static final int C_FLASH_PIC_HELPER = 3;
    public static final int C_BASE_PIC_DL_PROC = 4;
    public static final int C_ITEM_BUILDER_FAC = 5;
    public static final int C_AIO_UTILS = 6;
    public static final int C_ABS_GAL_SCENE = 7;
    //public static final int C_FAV_EMO_ROAM_HANDLER = 8;
    public static final int C_FAV_EMO_CONST = 9;
    public static final int C_MSG_REC_FAC = 10;
    public static final int C_CONTACT_UTILS = 11;

    @Nullable
    public static Class tryLoadOrNull(int i) {
        Class ret = load(c(i));
        if (ret != null) return ret;
        try {
            ConfigManager cfg = ConfigManager.getDefault();
            int lastVersion = cfg.getIntOrDefault("cache_" + a(i) + "_code", 0);
            if (getHostInfo(getApplication()).versionCode != lastVersion) {
                return null;
            }
            String clzn = cfg.getString("cache_" + a(i) + "_class");
            ret = load(clzn);
            return ret;
        } catch (IOException e) {
            return null;
        }
    }

    public static Class doFindClass(int i) {
        Class ret = tryLoadOrNull(i);
        if (ret != null) return ret;
        try {
            ArrayList<String> names;
            ConfigManager cfg = ConfigManager.getDefault();
            names = e(i);
            if (names == null || names.size() == 0) {
                log("Unable to deobf: " + c(i));
                return null;
            }
            if (names.size() == 1) {
                ret = load(names.get(0));
            } else {
                Class[] cas = new Class[names.size()];
                for (int j = 0; j < names.size(); j++) {
                    cas[j] = load(names.get(j));
                }
                ret = a(i, cas);
            }
            cfg.putString("cache_" + a(i) + "_class", ret.getName());
            cfg.getAllConfig().put("cache_" + a(i) + "_code", getHostInfo(getApplication()).versionCode);
            cfg.save();
        } catch (IOException e) {
            log(e);
        }
        return ret;
    }

    public static String a(int i) {
        switch (i) {
            case C_DIALOG_UTIL:
                return "dialog_util";
            case C_FACADE:
                return "facade";
            case C_FLASH_PIC_HELPER:
                return "flash_helper";
            case C_BASE_PIC_DL_PROC:
                return "base_pic_dl_proc";
            case C_ITEM_BUILDER_FAC:
                return "item_builder_fac";
            case C_AIO_UTILS:
                return "aio_utils";
            case C_ABS_GAL_SCENE:
                return "abs_gal_sc";
            case C_FAV_EMO_CONST:
                return "fav_emo_const";
            case C_MSG_REC_FAC:
                return "msg_rec_fac";
            case C_CONTACT_UTILS:
                return "contact_utils";
        }
        return null;
    }

    public static String c(int i) {
        switch (i) {
            case C_DIALOG_UTIL:
                return "com/tencent/mobileqq/utils/DialogUtil";
            case C_FACADE:
                return "com/tencent/mobileqq/activity/ChatActivityFacade";
            case C_FLASH_PIC_HELPER:
                return "com.tencent.mobileqq.app.FlashPicHelper";
            case C_BASE_PIC_DL_PROC:
                return "com/tencent/mobileqq/transfile/BasePicDownloadProcessor";
            case C_ITEM_BUILDER_FAC:
                return "com/tencent/mobileqq/activity/aio/item/ItemBuilderFactory";
            case C_AIO_UTILS:
                return "com.tencent.mobileqq.activity.aio.AIOUtils";
            case C_ABS_GAL_SCENE:
                return "com/tencent/common/galleryactivity/AbstractGalleryScene";
            case C_FAV_EMO_CONST:
                return "com/tencent/mobileqq/emosm/favroaming/FavEmoConstant";
            case C_MSG_REC_FAC:
                return "com/tencent/mobileqq/service/message/MessageRecordFactory";
            case C_CONTACT_UTILS:
                return "com/tencent/mobileqq/utils/ContactUtils";
        }
        return null;
    }

    public static byte[] b(int i) {
        switch (i) {
            case C_DIALOG_UTIL:
                return new byte[]{0x04, (byte) 0xE6, (byte) 0x9A, (byte) 0x82, (byte) 0xE4, (byte) 0xB8, (byte) 0x8D, (byte) 0xE5, (byte) 0x8D, (byte) 0x87, (byte) 0xE7, (byte) 0xBA, (byte) 0xA7};
            case C_FACADE:
                return new byte[]{0x20, 0x72, 0x65, 0x53, 0x65, 0x6E, 0x64, 0x45, 0x6D, 0x6F};
            case C_FLASH_PIC_HELPER:
                return new byte[]{0x0E, 0x46, 0x6C, 0x61, 0x73, 0x68, 0x50, 0x69, 0x63, 0x48};
            case C_BASE_PIC_DL_PROC:
                return new byte[]{0x2C, 0x42, 0x61, 0x73, 0x65, 0x50, 0x69, 0x63, 0x44, 0x6F, 0x77, 0x6E, 0x6C};
            case C_ITEM_BUILDER_FAC:
                return new byte[]{0x24, 0x49, 0x74, 0x65, 0x6D, 0x42, 0x75, 0x69, 0x6C, 0x64, 0x65, 0x72, 0x20, 0x69, 0x73, 0x3A, 0x20, 0x44};
            case C_AIO_UTILS:
                return new byte[]{0x0D, 0x6F, 0x70, 0x65, 0x6E, 0x41, 0x49, 0x4F, 0x20, 0x62, 0x79, 0x20, 0x4D, 0x54};
            case C_ABS_GAL_SCENE:
                return new byte[]{0x16, 0x67, 0x61, 0x6C, 0x6C, 0x65, 0x72, 0x79, 0x20, 0x73, 0x65, 0x74, 0x43, 0x6F, 0x6C, 0x6F, 0x72, 0x20, 0x62, 0x6C};
            case C_FAV_EMO_CONST:
                return new byte[]{0x11, 0x68, 0x74, 0x74, 0x70, 0x3A, 0x2F, 0x2F, 0x70, 0x2E, 0x71, 0x70, 0x69, 0x63, 0x2E};
            case C_MSG_REC_FAC:
                return new byte[]{0x2C, 0x63, 0x72, 0x65, 0x61, 0x74, 0x65, 0x50, 0x69, 0x63, 0x4D, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65};
            case C_CONTACT_UTILS:
                return new byte[]{0x07, 0x20, 0x2D, 0x20, 0x57, 0x69, 0x46, 0x69};
        }
        return null;
    }

    public static int[] d(int i) {
        switch (i) {
            case C_DIALOG_UTIL:
                return new int[]{4, 3};
            case C_FACADE:
                return new int[]{6, 3};
            case C_FLASH_PIC_HELPER:
                return new int[]{1, 3};
            case C_BASE_PIC_DL_PROC:
                return new int[]{7, 2};
            case C_ITEM_BUILDER_FAC:
                return new int[]{6, 1};
            case C_AIO_UTILS:
                return new int[]{6};
            case C_ABS_GAL_SCENE:
                return new int[]{1};
            case C_FAV_EMO_CONST:
                return new int[]{4, 5};
            case C_MSG_REC_FAC:
                return new int[]{4};
            case C_CONTACT_UTILS:
                return new int[]{4};
        }
        return null;
    }

    private static Class a(int i, Class[] classes) {
        switch (i) {
            case C_DIALOG_UTIL:
            case C_FACADE:
            case C_FLASH_PIC_HELPER:
            case C_AIO_UTILS:
            case C_CONTACT_UTILS:
            case C_MSG_REC_FAC:
                a:
                for (Class clz : classes) {
                    if (Modifier.isAbstract(clz.getModifiers())) continue;
                    for (Field f : clz.getDeclaredFields()) {
                        if (!Modifier.isStatic(f.getModifiers())) continue a;
                    }
                    return clz;
                }
                break;
            case C_BASE_PIC_DL_PROC:
                for (Class clz : classes) {
                    for (Field f : clz.getDeclaredFields()) {
                        int m = f.getModifiers();
                        if (Modifier.isStatic(m) && Modifier.isFinal(m) && f.getType().equals(Pattern.class))
                            return clz;
                    }
                }
                break;
            case C_ITEM_BUILDER_FAC:
                for (Class clz : classes) {
                    if (clz.getDeclaredFields().length > 30) return clz;
                }
                break;
            case C_ABS_GAL_SCENE:
                for (Class clz : classes) {
                    if (!Modifier.isAbstract(clz.getModifiers())) continue;
                    for (Field f : clz.getDeclaredFields()) {
                        if (f.getType().equals(View.class))
                            return clz;
                    }
                }
                break;
            case C_FAV_EMO_CONST:
                a:
                for (Class clz : classes) {
                    if (Modifier.isAbstract(clz.getModifiers())) continue;
                    for (Field f : clz.getDeclaredFields()) {
                        if (!Modifier.isStatic(f.getModifiers())) continue a;
                    }
                    if (clz.getDeclaredMethods().length > 3) continue;
                    return clz;
                }
                break;
        }
        return null;
    }

    private static ArrayList<String> e(int i) {
        ClassLoader loader = Initiator.getClassLoader();
        int record = 0;
        int[] qf = d(i);
        byte[] key = b(i);
        if (qf != null) for (int dexi : qf) {
            record |= 1 << dexi;
            try {
                ArrayList<String> ret = a(key, dexi, loader);
                if (ret != null) return ret;
            } catch (FileNotFoundException ignored) {
            }
        }
        int dexi = 1;
        while (true) {
            if ((record & (1 << dexi)) != 0) {
                dexi++;
                continue;
            }
            try {
                ArrayList<String> ret = a(key, dexi, loader);
                if (ret != null) return ret;
            } catch (FileNotFoundException ignored) {
                return null;
            }
            dexi++;
        }
    }

    /**
     * get ALL the possible class names
     *
     * @param key    the pattern
     * @param i      C_XXXX
     * @param loader to get dex file
     * @return ["abc","ab"]
     * @throws FileNotFoundException apk has no classesN.dex
     */
    public static ArrayList<String> a(byte[] key, int i, ClassLoader loader) throws FileNotFoundException {
        String name;
        byte[] buf = new byte[4096];
        byte[] content;
        if (i == 1) name = "classes.dex";
        else name = "classes" + i + ".dex";
        URL url = null;
        try {
            url = (URL) Utils.invoke_virtual(loader, "findResource", name, String.class);
        } catch (Throwable ignored) {
        }
        if (i == 1) {
            log("dex1:" + url);
        }
        if (url == null) throw new FileNotFoundException(name);
        InputStream in;
        try {
            in = url.openStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int ii;
            while ((ii = in.read(buf)) != -1) {
                baos.write(buf, 0, ii);
            }
            in.close();
            content = baos.toByteArray();
            if (i == 1) {
                log("dex1.len: " + content.length);
            }
            ArrayList<Integer> opcodeOffsets = a(content, key);
            ArrayList<String> rets = new ArrayList<String>();
            for (int j = 0; j < opcodeOffsets.size(); j++) {
                try {
                    String desc = a(content, opcodeOffsets.get(j));
                    rets.add(desc.substring(1, desc.length() - 1));
                } catch (InternalError ignored) {
                }
            }
            return rets;
        } catch (IOException e) {
            log(e);
            return null;
        }
    }

    public static ArrayList<Integer> a(byte[] buf, byte[] target) {
        ArrayList<Integer> rets = new ArrayList<>();
        int[] ret = new int[1];
        final float f[] = new float[1];
        ret[0] = arrayIndexOf(buf, target, 0, buf.length, f);
        ret[0] = arrayIndexOf(buf, int2u4le(ret[0]), 0, buf.length, f);
        //System.out.println(ret[0]);
        int off = (ret[0] - readLe32(buf, 0x3c)) / 4;
        if (off > 0xFFFF) {
            target = int2u4le(off);
        } else target = int2u2le(off);
        off = 0;
        while (true) {
            off = arrayIndexOf(buf, target, off + 1, buf.length, f);
            if (off == -1) break;
            if (buf[off - 2] == (byte) 26/*Opcodes.OP_CONST_STRING*/
                    || buf[off - 2] == (byte) 27)/* Opcodes.OP_CONST_STRING_JUMBO*/ {
                ret[0] = off - 2;
                int opcodeOffset = ret[0];
                rets.add(opcodeOffset);
            }
        }
        return rets;
    }

    public static String a(byte[] buf, int opcodeoff) {
        int classDefsSize = readLe32(buf, 0x60);
        int classDefsOff = readLe32(buf, 0x64);
        int p[] = new int[1];
        int[] ret = new int[1];
        int[] co = new int[1];
        for (int cn = 0; cn < classDefsSize; cn++) {
            int classIdx = readLe32(buf, classDefsOff + cn * 32);
            int classDataOff = readLe32(buf, classDefsOff + cn * 32 + 24);
            p[0] = classDataOff;
            if (classDataOff == 0) continue;
            int staticFieldsSize = readUleb128(buf, p),
                    instanceFieldsSize = readUleb128(buf, p),
                    directMethodsSize = readUleb128(buf, p),
                    virtualMethodsSize = readUleb128(buf, p);
            for (int fn = 0; fn < staticFieldsSize + instanceFieldsSize; fn++) {
                int fieldIdx = readUleb128(buf, p);
                int accessFlags = readUleb128(buf, p);
            }
            for (int mn = 0; mn < directMethodsSize; mn++) {
                int methodIdx = readUleb128(buf, p);
                int accessFlags = readUleb128(buf, p);
                int codeOff = co[0] = readUleb128(buf, p);
                if (codeOff == 0) continue;
                int insnsSize = readLe32(buf, codeOff + 12);
                if (codeOff + 16 <= opcodeoff && opcodeoff <= codeOff + 16 + insnsSize * 2) {
                    return readType(buf, classIdx);
                }
            }
            for (int mn = 0; mn < virtualMethodsSize; mn++) {
                int methodIdx = readUleb128(buf, p);
                int accessFlags = readUleb128(buf, p);
                int codeOff = co[0] = readUleb128(buf, p);
                if (codeOff == 0) continue;
                int insnsSize = readLe32(buf, codeOff + 12);
                if (codeOff + 16 <= opcodeoff && opcodeoff <= codeOff + 16 + insnsSize * 2) {
                    return readType(buf, classIdx);
                }
            }
        }
        throw new InternalError();
    }

    public static int readUleb128(byte[] src, int[] offset) {
        int result = 0;
        int count = 0;
        int cur;
        do {
            cur = src[offset[0]];
            cur &= 0xff;
            result |= (cur & 0x7f) << count * 7;
            count++;
            offset[0]++;
        } while ((cur & 0x80) == 128 && count < 5);
        return result;
    }

    public static String readString(byte[] buf, int idx) {
        int stringIdsOff = readLe32(buf, 0x3c);
        int strOff = readLe32(buf, stringIdsOff + 4 * idx);
        int len = (byte) buf[strOff];//hack,just assume it no longer than 127
        return new String(buf, strOff + 1, len);
    }

    public static String readType(byte[] buf, int idx) {
        int typeIdsOff = readLe32(buf, 0x44);
        int strIdx = readLe32(buf, typeIdsOff + 4 * idx);
        return readString(buf, strIdx);
    }

    public static int arrayIndexOf(byte[] arr, byte[] subarr, int startindex, int endindex, float[] progress) {
        byte a = subarr[0];
        float d = endindex - startindex;
        int b = endindex - subarr.length;
        int i = startindex;
        int ii;
        a:
        while (i <= b) {
            if (arr[i] != a) {
                progress[0] = (i++ - startindex) / d;
                continue;
            } else {
                for (ii = 0; ii < subarr.length; ii++) {
                    if (arr[i++] != subarr[ii]) {
                        i = i - ii;
                        continue a;
                    }
                }
                return i - ii;
            }
        }
        return -1;
    }

    public static byte[] int2u4le(int i) {
        return new byte[]{(byte) i, (byte) (i >> 8), (byte) (i >> 16), (byte) (i >> 24)};
    }

    public static byte[] int2u2le(int i) {
        return new byte[]{(byte) i, (byte) (i >> 8)};
    }

    public static int readLe32(byte[] buf, int index) {
        int i = buf[index] & 0xFF | (buf[index + 1] << 8) & 0xff00 | (buf[index + 2] << 16) & 0xff0000 | (buf[index + 3] << 24) & 0xff000000;
        return i;
    }
}
