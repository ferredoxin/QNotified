package nil.nadph.qnotified.util;

import nil.nadph.qnotified.record.ConfigManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class DexKit {

    public static final int C_DIALOG_UTIL = 1;
    public static final int C_FACADE = 2;
    public static final int C_FLASH_PIC_HELPER = 3;
    public static final int C_BASE_PIC_DL_PROC = 4;
    public static final int C_ITEM_BUILDER_FAC = 5;

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
            String name;
            ConfigManager cfg = ConfigManager.getDefault();
            name = a(c(i), b(i));
            if (name == null) {
                log("Unable to deobf: " + c(i));
                return null;
            }
            ret = load(name);
            cfg.putString("cache_" + a(i) + "_class", name);
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
        }
        return null;
    }

    private static String a(String clz, byte[] key) {
        ClassLoader loader = Initiator.getClassLoader();
        Class clret = load(clz);
        if (clret != null) return clret.getName();
        byte[] buf = new byte[4096];
        byte[] content;
        int i = 1;
        String name;
        try {
            loop_a:
            while (true) {
                if (i == 1) name = "classes.dex";
                else name = "classes" + i + ".dex";
                URL url = (URL) Utils.invoke_virtual(loader, "findResource", name, String.class);
                if (url == null) return null;
                InputStream in = url.openStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int ii;
                while ((ii = in.read(buf)) != -1) {
                    baos.write(buf, 0, ii);
                }
                in.close();
                content = baos.toByteArray();
                ArrayList<Integer> opcodeOffsets = a(content, key);
                if (opcodeOffsets.size() > 0) {
                    for (int opoff : opcodeOffsets) {
                        name = a(content, opoff);
                        if (!name.contains("/")) break loop_a;
                    }
                }
                i++;
            }
            if (!name.startsWith("L")) return null;
            return name.substring(1, name.length() - 1);
        } catch (Exception e) {
            Utils.log(e);
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
			/*pStaticFields=readUleb128(buf, p),
			 pInstanceFields=readUleb128(buf, p),
			 pDirectMethods=readUleb128(buf, p),
			 pVirtualMethods=readUleb128(buf, p);*/
            //p[0] = pDirectMethods;
            for (int fn = 0; fn < staticFieldsSize + instanceFieldsSize; fn++) {
                int fieldIdx = readUleb128(buf, p);
                int accessFlags = readUleb128(buf, p);
            }
            for (int mn = 0; mn < directMethodsSize; mn++) {
                int methodIdx = readUleb128(buf, p);
                int accessFlags = readUleb128(buf, p);
                int codeOff = co[0] = readUleb128(buf, p);
                int insnsSize = readLe32(buf, codeOff + 12);
                if (codeOff + 16 <= opcodeoff && opcodeoff <= codeOff + 16 + insnsSize * 2) {
                    return readType(buf, classIdx);
                }
            }
			/*for (int mn=0;mn < directMethodsSize;mn++) {
			 int codeOff=readLe32(buf, p[0] + 12 * mn);
			 int insnsSize=readLe32(buf, codeOff + 12);
			 if (codeOff + 16 < opcodeoff && opcodeoff < codeOff + 16 + insnsSize) {
			 System.out.println(readType(buf, classIdx));
			 return;
			 }
			 }*/
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

    public static final short
            kDexTypeHeaderItem = 0x0000,
            kDexTypeStringIdItem = 0x0001,
            kDexTypeTypeIdItem = 0x0002,
            kDexTypeProtoIdItem = 0x0003,
            kDexTypeFieldIdItem = 0x0004,
            kDexTypeMethodIdItem = 0x0005,
            kDexTypeClassDefItem = 0x0006,
            kDexTypeMapList = 0x1000,
            kDexTypeTypeList = 0x1001,
            kDexTypeAnnotationSetRefList = 0x1002,
            kDexTypeAnnotationSetItem = 0x1003,
            kDexTypeClassDataItem = 0x2000,
            kDexTypeCodeItem = 0x2001,
            kDexTypeStringDataItem = 0x2002,
            kDexTypeDebugInfoItem = 0x2003,
            kDexTypeAnnotationItem = 0x2004,
            kDexTypeEncodedArrayItem = 0x2005,
            kDexTypeAnnotationsDirectoryItem = 0x2006;
}
