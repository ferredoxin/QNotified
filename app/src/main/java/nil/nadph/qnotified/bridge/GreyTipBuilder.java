package nil.nadph.qnotified.bridge;

import android.os.Bundle;

import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Utils;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.setObjectField;
import static nil.nadph.qnotified.util.Utils.*;

public class GreyTipBuilder implements Appendable, CharSequence {

    public static final int MSG_TYPE_TROOP_GAP_GRAY_TIPS = -2030;
    public static final int MSG_TYPE_REVOKE_GRAY_TIPS = -2031;

    private int type;
    private final StringBuilder msg = new StringBuilder();

    private GreyTipBuilder() {
    }

    private static class HighlightItemHolder {
        public HighlightItemHolder(Bundle i, int s, int e) {
            item = i;
            start = s;
            end = e;
        }

        public int start;
        public int end;
        public Bundle item;
    }

    private ArrayList<HighlightItemHolder> items = null;

    public static GreyTipBuilder create(int _type) {
        GreyTipBuilder builder = new GreyTipBuilder();
        builder.type = _type;
        return builder;
    }

    @Deprecated
    public GreyTipBuilder appendTroopMember(String memberUin) {
        return appendTroopMember(memberUin, memberUin);
    }

    public GreyTipBuilder appendTroopMember(String memberUin, String name) {
        return appendTroopMember(memberUin, name, true);
    }

    public GreyTipBuilder appendTroopMember(String memberUin, String name, boolean update) {
        if (items == null) items = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putInt("key_action", 5);
        bundle.putString("troop_mem_uin", memberUin);
        bundle.putBoolean("need_update_nick", update);
        int len = name.length();
        items.add(new HighlightItemHolder(bundle, msg.length(), msg.length() + len));
        msg.append(name);
        return this;
    }

    public Object build(String uin, int istroop, String fromUin, long time, long msgUid, long msgseq, long shmsgseq) {
        Object messageRecord = null;
        try {
            messageRecord = invoke_static_declared_ordinal_modifier(DexKit.doFindClass(DexKit.C_MSG_REC_FAC), 0, 1, true, Modifier.PUBLIC, 0, type, int.class);
            callMethod(messageRecord, "init", Utils.getAccount(), uin, fromUin, msg.toString(), time, type, istroop, msgseq);
            setObjectField(messageRecord, "msgUid", msgUid);
            setObjectField(messageRecord, "shmsgseq", shmsgseq);
            setObjectField(messageRecord, "isread", true);
            if (items != null) {
                for (HighlightItemHolder h : items) {
                    invoke_virtual(messageRecord, "addHightlightItem", h.start, h.end, h.item, int.class, int.class, Bundle.class);
                }
            }
        } catch (Exception e) {
            log(e);
        }
        return messageRecord;
    }

    public Object build(String uin, int istroop, String fromUin, long time, long msgseq) {
        Object messageRecord = null;
        try {
            messageRecord = invoke_static_declared_ordinal_modifier(DexKit.doFindClass(DexKit.C_MSG_REC_FAC), 0, 1, true, Modifier.PUBLIC, 0, type, int.class);
            callMethod(messageRecord, "init", Utils.getAccount(), uin, fromUin, msg.toString(), time, type, istroop, msgseq);
            setObjectField(messageRecord, "isread", true);
            if (items != null) {
                for (HighlightItemHolder h : items) {
                    invoke_virtual(messageRecord, "addHightlightItem", h.start, h.end, h.item, int.class, int.class, Bundle.class);
                }
            }
        } catch (Exception e) {
            log(e);
        }
        return messageRecord;
    }

    @Override
    public GreyTipBuilder append(CharSequence csq) {
        msg.append(csq);
        return this;
    }

    @Override
    public GreyTipBuilder append(CharSequence csq, int start, int end) {
        msg.append(csq, start, end);
        return this;
    }

    @Override
    public GreyTipBuilder append(char c) {
        msg.append(c);
        return this;
    }

    @Override
    public int length() {
        return msg.length();
    }

    @Override
    public char charAt(int index) {
        return msg.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return msg.subSequence(start, end);
    }
}
