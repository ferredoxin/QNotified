/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
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
package nil.nadph.qnotified;

import static cc.ioctl.util.DateTimeUtil.getRelTimeStrSec;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_virtual;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_virtual_any;
import static nil.nadph.qnotified.util.Utils.ContactDescriptor;
import static nil.nadph.qnotified.util.Utils.log;
import static nil.nadph.qnotified.util.Utils.logi;
import static nil.nadph.qnotified.util.Utils.logw;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import cc.ioctl.activity.ExfriendListActivity;
import cc.ioctl.hook.DelDetectorHook;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import me.singleneuron.qn_kernel.data.HostInfo;
import nil.nadph.qnotified.bridge.FriendChunk;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.config.EventRecord;
import nil.nadph.qnotified.config.FriendRecord;
import nil.nadph.qnotified.config.Table;
import nil.nadph.qnotified.lifecycle.ActProxyMgr;
import nil.nadph.qnotified.lifecycle.Parasitics;
import nil.nadph.qnotified.util.Toasts;
import nil.nadph.qnotified.util.Utils;

/**
 * @deprecated 设计不合理 将陆续弃用并在重构中移除
 */
@Deprecated
public class ExfriendManager {

    public static final int ID_EX_NOTIFY = 65537;
    public static final int CHANGED_UNSPECIFIED = 0;
    public static final int CHANGED_GENERAL_SETTING = 16;
    public static final int CHANGED_PERSONS = 17;
    public static final int CHANGED_EX_EVENTS = 18;
    public static final int CHANGED_EVERYTHING = 64;
    private static final String KET_LEGACY_FRIENDS = "friends";
    private static final String KET_FRIENDS = "friends_impl";
    private static final String KET_LEGACY_EVENTS = "events";
    private static final String KET_EVENTS = "events_impl";
    private static final int FL_UPDATE_INT_MIN = 10 * 60;//sec
    private static final HashMap<Long, ExfriendManager> instances = new HashMap<>();
    private static ExecutorService tp;
    private volatile long lastUpdateTimeSec;
    private long mUin;
    private ConcurrentHashMap<Long, FriendRecord> persons;
    private ConcurrentHashMap<Integer, EventRecord> events;
    private ConfigManager mConfig;
    private ConcurrentHashMap mStdRemarks;
    private ArrayList<FriendChunk> cachedFriendChunks;
    private boolean dirtySerializedFlag = true;

    private ExfriendManager(long uin) {
        persons = new ConcurrentHashMap<>();
        events = new ConcurrentHashMap<>();
        dirtySerializedFlag = true;
        if (tp == null) {
            int pt = SyncUtils.getProcessType();
            if (pt != 0 && (pt & (SyncUtils.PROC_MAIN | SyncUtils.PROC_MSF)) != 0) {
                tp = Executors.newCachedThreadPool();
            }
        }
        initForUin(uin);
    }

    public static ExfriendManager getCurrent() {
        return get(Utils.getLongAccountUin());
    }

    public static ExfriendManager get(long uin) {
        if (uin < 10000) {
            throw new IllegalArgumentException("uin must >= 10000 ");
        }
        synchronized (instances) {
            ExfriendManager ret = instances.get(uin);
            if (ret != null) {
                return ret;
            }
            ret = new ExfriendManager(uin);
            instances.put(uin, ret);
            return ret;
        }
    }

    public static ExfriendManager getOrNull(long uin) {
        if (uin < 10000) {
            throw new IllegalArgumentException("uin must >= 10000 ");
        }
        synchronized (instances) {
            return instances.get(uin);
        }
    }

    public static Object getFriendsManager() throws Exception {
        Object qqAppInterface = Utils.getAppRuntime();
        return invoke_virtual(qqAppInterface, "getManager", 50, int.class);
    }

    public static ConcurrentHashMap getFriendsConcurrentHashMap(Object friendsManager)
        throws IllegalAccessException, NoSuchFieldException {
        for (Field field : load("com.tencent.mobileqq.app.FriendsManager").getDeclaredFields()) {
            if (ConcurrentHashMap.class == field.getType()) {
                field.setAccessible(true);
                ConcurrentHashMap concurrentHashMap = (ConcurrentHashMap) field.get(friendsManager);
                if (concurrentHashMap != null && concurrentHashMap.size() > 0) {
                    if (concurrentHashMap.get(concurrentHashMap.keySet().toArray()[0]).getClass()
                        == load("com.tencent.mobileqq.data.Friends")) {
                        return concurrentHashMap;
                    }
                }
            }
        }
        throw new NoSuchFieldException();
    }

    public static void onGetFriendListResp(FriendChunk fc) {
        get(fc.uin).recordFriendChunk(fc);
    }

    public long getUin() {
        return mUin;
    }

    public void reinit() {
        persons = new ConcurrentHashMap<>();
        events = new ConcurrentHashMap<>();
        dirtySerializedFlag = true;
        initForUin(mUin);
    }

    /**
     * Do not use this method for uin-isolated config anymore.<br/>
     * <p>
     * Use {@link ConfigManager#forCurrentAccount()} or {@link ConfigManager#forAccount(long)}
     * directly instead.
     *
     * @return See {@link ConfigManager#forAccount(long)}
     */
    @NonNull
    public ConfigManager getConfig() {
        return ConfigManager.forAccount(mUin);
    }

    private void initForUin(long uin) {
        cachedFriendChunks = new ArrayList<>();
        synchronized (this) {
            mUin = uin;
            try {
                loadAndParseConfigData();
                try {
                    mStdRemarks = getFriendsConcurrentHashMap(getFriendsManager());
                } catch (Throwable e) {
                }
                if (persons.size() == 0 && mStdRemarks != null) {
                    logw("WARNING:INIT FROM THE INTERNAL");
                    try {
                        //Here we try to copy friendlist
                        Object fr;
                        Field fuin, fremark, fnick;
                        Class clz_fr = load("com/tencent/mobileqq/data/Friends");
                        fuin = clz_fr.getField("uin");//long!!!
                        fuin.setAccessible(true);
                        fremark = clz_fr.getField("remark");
                        fremark.setAccessible(true);
                        fnick = clz_fr.getField("name");
                        fnick.setAccessible(true);
                        Iterator<Map.Entry> it = mStdRemarks.entrySet().iterator();
                        while (it.hasNext()) {
                            long t = System.currentTimeMillis() / 1000;
                            fr = it.next().getValue();
                            if (fr == null) {
                                continue;
                            }
                            FriendRecord f = new FriendRecord();
                            f.uin = Long.parseLong((String) fuin.get(fr));
                            f.remark = (String) fremark.get(fr);
                            f.nick = (String) fnick.get(fr);
                            f.friendStatus = FriendRecord.STATUS_RESERVED;
                            f.serverTime = t;
                            if (!persons.containsKey(f.uin)) {
                                persons.put(f.uin, f);
                                dirtySerializedFlag = true;
                            }
                        }
                        saveConfigure();
                    } catch (Exception e) {
                        log(e);
                    }
                }
            } catch (Exception e) {
                log(e);
            }
        }
    }

    //TODO: Rename it
    private void loadAndParseConfigData() {
        synchronized (this) {
            try {
                if (mConfig == null) {
                    mConfig = ConfigManager.forAccount(mUin);
                }
                loadFriendsData();
                loadEventsData();
                lastUpdateTimeSec = mConfig.getLong("lastUpdateFl", 0L);
            } catch (Exception e) {
                log(e);
            }
        }
    }

    private void loadFriendsData() {
        // step.1 load table
        Table<Long> fr = null;
        byte[] friendsDat = mConfig.getBytes(KET_FRIENDS);
        if (friendsDat != null) {
            try {
                fr = Table.fromBytes(friendsDat);
            } catch (IOException e) {
                Utils.log(e);
            }
        }
        if (fr == null) {
            // try to load from legacy
            fr = (Table<Long>) mConfig.getObject(KET_LEGACY_FRIENDS);
        }
        if (fr == null) {
            Utils.loge("E/loadFriendsData table is null");
            fr = new Table<>();
        }
        /* uin+"" is key */
        fr.keyName = "uin";
        fr.keyType = Table.TYPE_LONG;
        fr.addField("nick", Table.TYPE_IUTF8);
        fr.addField("remark", Table.TYPE_IUTF8);
        fr.addField("friendStatus", Table.TYPE_INT);
        fr.addField("serverTime", Table.TYPE_LONG);
        // step.2 fill map
        Table<Long> t = fr;
        if (persons == null) {
            persons = new ConcurrentHashMap<>();
        }
        dirtySerializedFlag = true;
        Iterator<Map.Entry<Long, Object[]>> it = t.records.entrySet().iterator();
        Map.Entry<Long, Object[]> entry;
        int _nick, _remark, _fs, _time;
        _nick = t.getFieldId("nick");
        _remark = t.getFieldId("remark");
        _fs = t.getFieldId("friendStatus");
        _time = t.getFieldId("serverTime");
        Object[] rec;
        while (it.hasNext()) {
            entry = it.next();
            FriendRecord f = new FriendRecord();
            f.uin = entry.getKey();
            rec = entry.getValue();
            f.remark = (String) rec[_remark];
            f.nick = (String) rec[_nick];
            f.friendStatus = (Integer) rec[_fs];
            f.serverTime = (Long) rec[_time];
            persons.put(f.uin, f);
            dirtySerializedFlag = true;
        }
    }

    private void saveFriendsData() {
        if (persons == null) {
            return;
        }
        // step.1 create table
        Table<Long> fr = new Table<>();
        /* uin+"" is key */
        fr.keyName = "uin";
        fr.keyType = Table.TYPE_LONG;
        fr.addField("nick", Table.TYPE_IUTF8);
        fr.addField("remark", Table.TYPE_IUTF8);
        fr.addField("friendStatus", Table.TYPE_INT);
        fr.addField("serverTime", Table.TYPE_LONG);
        // step.2 fill table
        Iterator<Map.Entry<Long, FriendRecord>> it = persons.entrySet().iterator();
        Map.Entry<Long, FriendRecord> ent;
        FriendRecord f;
        Long k;
        while (it.hasNext()) {
            ent = it.next();
            f = ent.getValue();
            fr.insert(ent.getKey());
            k = ent.getKey();
            try {
                fr.set(k, "nick", f.nick);
                fr.set(k, "remark", f.remark);
                fr.set(k, "serverTime", f.serverTime);
                fr.set(k, "friendStatus", f.friendStatus);
            } catch (NoSuchFieldException e) {
                log(e);
                //shouldn't happen
            }
        }
        // step.3 write out table
        try {
            mConfig.putBytes(KET_FRIENDS, fr.toBytes());
        } catch (IOException e) {
            log(e);
            //shouldn't happen
        }
    }

    private void loadEventsData() {
        // step.1 load table
        Table<Integer> t = null;
        byte[] eventDat = mConfig.getBytes(KET_EVENTS);
        if (eventDat != null) {
            try {
                t = Table.fromBytes(eventDat);
            } catch (IOException e) {
                Utils.log(e);
            }
        }
        if (t == null) {
            // try to load from legacy
            t = (Table<Integer>) mConfig.getObject(KET_LEGACY_EVENTS);
        }
        if (t == null) {
            Utils.logd("damn! initEvT in null");
            return;
        }
        /* `uin as string` is key */
        t.keyName = "id";
        t.keyType = Table.TYPE_INT;
        t.addField("timeRangeEnd", Table.TYPE_LONG);
        t.addField("timeRangeBegin", Table.TYPE_LONG);
        t.addField("event", Table.TYPE_INT);
        t.addField("operand", Table.TYPE_LONG);
        t.addField("operator", Table.TYPE_LONG);
        t.addField("executor", Table.TYPE_LONG);
        t.addField("before", Table.TYPE_IUTF8);
        t.addField("after", Table.TYPE_IUTF8);
        t.addField("extra", Table.TYPE_IUTF8);
        t.addField("_nick", Table.TYPE_IUTF8);
        t.addField("_remark", Table.TYPE_IUTF8);
        t.addField("_friendStatus", Table.TYPE_INT);
        // step.2 fill map
        Iterator<Map.Entry<Integer, Object[]>> it = t.records.entrySet().iterator();
        Map.Entry<Integer, Object[]> entry;
        int __nick, __remark, __fs, _te, _tb, _ev, _op, _b, _a, _extra, _op_old, _exec;
        __nick = t.getFieldId("_nick");
        __remark = t.getFieldId("_remark");
        __fs = t.getFieldId("_friendStatus");
        _te = t.getFieldId("timeRangeEnd");
        _tb = t.getFieldId("timeRangeBegin");
        _ev = t.getFieldId("event");
        _op = t.getFieldId("operand");
        _exec = t.getFieldId("executor");
        _op_old = t.getFieldId("operator");
        _b = t.getFieldId("before");
        _a = t.getFieldId("after");
        _extra = t.getFieldId("extra");
        Object[] rec;
        long tmp;
        while (it.hasNext()) {
            try {
                entry = it.next();
                EventRecord ev = new EventRecord();
                rec = entry.getValue();
                ev._nick = (String) rec[__nick];
                ev._remark = (String) rec[__remark];
                ev._friendStatus = (Integer) rec[__fs];
                ev.timeRangeBegin = (Long) rec[_tb];
                ev.timeRangeEnd = (Long) rec[_te];
                ev.event = (Integer) rec[_ev];
                if (_op == -1) {
                    // all don't have
                    ev.operand = (Long) rec[_op_old];
                } else {
                    try {
                        tmp = (Long) rec[_op];
                    } catch (NullPointerException e) {
                        tmp = -1;
                    }
                    if (tmp > 9999) {
                        ev.operand = tmp;
                    } else {
                        ev.operand = (Long) rec[_op_old];
                    }
                }
                if (_exec != -1) {
                    try {
                        ev.executor = (Long) rec[_exec];
                    } catch (NullPointerException e) {
                        ev.executor = -1;
                    }
                } else {
                    ev.executor = -1;
                }
                ev.before = (String) rec[_b];
                ev.after = (String) rec[_a];
                ev.extra = (String) rec[_extra];
                events.put(entry.getKey(), ev);
                dirtySerializedFlag = true;
            } catch (Exception e) {
                log(e);
            }
        }
    }

    private void saveEventsData() {
        if (events == null) {
            return;
        }
        // 1. create table
        Table<Integer> t = new Table<>();
        t.keyName = "id";
        t.keyType = Table.TYPE_INT;
        t.addField("timeRangeEnd", Table.TYPE_LONG);
        t.addField("timeRangeBegin", Table.TYPE_LONG);
        t.addField("event", Table.TYPE_INT);
        t.addField("operand", Table.TYPE_LONG);
        t.addField("operator", Table.TYPE_LONG);
        t.addField("executor", Table.TYPE_LONG);
        t.addField("before", Table.TYPE_IUTF8);
        t.addField("after", Table.TYPE_IUTF8);
        t.addField("extra", Table.TYPE_IUTF8);
        t.addField("_nick", Table.TYPE_IUTF8);
        t.addField("_remark", Table.TYPE_IUTF8);
        t.addField("_friendStatus", Table.TYPE_INT);
        // 2. fill table
        Iterator<Map.Entry<Integer, EventRecord>> it =/*(Iterator<Map.Entry<Long, FriendRecord>>)*/events
            .entrySet().iterator();
        Map.Entry<Integer, EventRecord> ent;
        EventRecord ev;
        int k;
        while (it.hasNext()) {
            ent = it.next();
            ev = ent.getValue();
            t.insert(ent.getKey());
            k = ent.getKey();
            try {
                t.set(k, "timeRangeEnd", ev.timeRangeEnd);
                t.set(k, "timeRangeBegin", ev.timeRangeBegin);
                t.set(k, "event", ev.event);
                t.set(k, "operand", ev.operand);
                //fallback
                t.set(k, "operator", ev.operand);
                t.set(k, "executor", ev.executor);
                t.set(k, "before", ev.before);
                t.set(k, "after", ev.after);
                t.set(k, "extra", ev.extra);
                t.set(k, "_nick", ev._nick);
                t.set(k, "_remark", ev._remark);
                t.set(k, "_friendStatus", ev._friendStatus);
            } catch (Exception e) {
                log(e);
                //shouldn't happen
            }
        }
        // 3. write out
        try {
            mConfig.putBytes(KET_EVENTS, t.toBytes());
        } catch (IOException e) {
            Utils.log(e);
        }
    }

    public void saveConfigure() {
        synchronized (this) {
            try {
                if (persons == null) {
                    persons = new ConcurrentHashMap<>();
                }
                if (dirtySerializedFlag) {
                    saveEventsData();
                    saveFriendsData();
                    dirtySerializedFlag = false;
                }
                mConfig.putLong("uin", mUin);
                mConfig.save();
            } catch (IOException e) {
                log(e);
            }
        }
    }

    public ArrayList<ContactDescriptor> getFriendsRemark() {
        ArrayList<ContactDescriptor> ret = new ArrayList<>();
        if (persons != null) {
            for (Map.Entry<Long, FriendRecord> f : persons.entrySet()) {
                if (f.getValue().friendStatus == FriendRecord.STATUS_EXFRIEND) {
                    continue;
                }
                ContactDescriptor cd = new ContactDescriptor();
                cd.uinType = 0;
                cd.uin = f.getKey() + "";
                cd.nick = f.getValue().remark;
                if (cd.nick == null) {
                    cd.nick = f.getValue().remark;
                }
                ret.add(cd);
            }
        }
        return ret;
    }

    /**
     * @hide
     */
    //@Deprecated
    public ConcurrentHashMap<Long, FriendRecord> getPersons() {
        dirtySerializedFlag = true;
        return persons;
    }

    /**
     * @hide
     */
    //@Deprecated
    public ConcurrentHashMap<Integer, EventRecord> getEvents() {
        dirtySerializedFlag = true;
        return events;
    }

    /**
     * @method getRemark: return remark if it's a friend,or one's nickname if not
     */
    public String getRemark(long uin) {
        return (String) mStdRemarks.get("" + uin);
    }

    public synchronized void recordFriendChunk(FriendChunk fc) {
        if (fc.getfriendCount == 0) {
            //ignore it
        } else {
            if (fc.startIndex == 0) {
                cachedFriendChunks.clear();
            }
            cachedFriendChunks.add(fc);
            if (fc.friend_count + fc.startIndex == fc.totoal_friend_count) {
                final FriendChunk[] update = new FriendChunk[cachedFriendChunks.size()];
                cachedFriendChunks.toArray(update);
                cachedFriendChunks.clear();
                tp.execute(new Runnable() {
                    @Override
                    public void run() {
                        asyncUpdateFriendListTask(update);
                    }
                });
            }
        }
    }

    public void setRedDot() {
        WeakReference redDotRef = DelDetectorHook.INSTANCE.redDotRef;
        if (redDotRef == null) {
            return;
        }
        final TextView rd = (TextView) redDotRef.get();
        if (rd == null) {
            logi("Red dot missing!");
            return;
        }
        int m = mConfig.getInt("unread", 0);
        final int n = m;
        ((Activity) Utils.getContext(rd)).runOnUiThread(() -> {
            if (n < 1) {
                rd.setVisibility(View.INVISIBLE);
            } else {
                rd.setText("" + n);
                rd.setVisibility(View.VISIBLE);
            }
        });
    }

    public void reportEventWithoutSave(EventRecord ev, Object[] out) {
        int k = events.size();
        while (events.containsKey(k)) {
            k++;
        }
        events.put(k, ev);
        dirtySerializedFlag = true;
        if (out == null) {
            return;
        }
        int unread = mConfig.getInt("unread", 0);
        unread++;
        mConfig.putInt("unread", unread);
        String title, ticker, tag, c;
        if (ev._remark != null && ev._remark.length() > 0) {
            tag = ev._remark + "(" + ev.operand + ")";
        } else if (ev._nick != null && ev._nick.length() > 0) {
            tag = ev._nick + "(" + ev.operand + ")";
        } else {
            tag = "" + ev.operand;
        }
        out[0] = unread;
        ticker = "检测到" + unread + "位好友删除了你";
        if (unread > 1) {
            title = "你被" + unread + "位好友删除";
            c = tag + "等" + unread + "位好友";
        } else {
            title = tag;
            c = "在约 " + getRelTimeStrSec(ev.timeRangeBegin) + " 删除了你";
        }
        out[1] = ticker;
        out[2] = title;
        out[3] = c;
    }

    public void clearUnreadFlag() {
        mConfig.putInt("unread", 0);
        try {
            NotificationManager nm = (NotificationManager) HostInfo.getHostInfo()
                .getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(ID_EX_NOTIFY);
        } catch (Exception e) {
            log(e);
        }
        dirtySerializedFlag = true;
        setRedDot();
        saveConfigure();
    }

    private void asyncUpdateFriendListTask(FriendChunk[] fcs) {
        Object[] ptr = new Object[4];
        synchronized (this) {
            //check integrity
            boolean integrity;
            int tmp = fcs[fcs.length - 1].totoal_friend_count;
            int len = fcs.length;
            if (tmp < 2) {
                return;
            }
            for (int i = 0; i < fcs.length; i++) {
                tmp -= fcs[len - i - 1].friend_count;
            }
            integrity = tmp == 0;
            if (!integrity) {
                logi("Inconsistent friendlist chunk data!Aborting!total=" + tmp);
                return;
            }
            HashMap<Long, FriendRecord> del = new HashMap<>(persons);
            FriendRecord fr;
            for (FriendChunk fc : fcs) {
                for (int ii = 0; ii < fc.friend_count; ii++) {
                    fr = del.remove(fc.arrUin[ii]);
                    if (fr != null) {
                        fr.friendStatus = FriendRecord.STATUS_FRIEND_MUTUAL;
                        fr.nick = fc.arrNick[ii];
                        fr.remark = fc.arrRemark[ii];
                        fr.serverTime = fc.serverTime;
                    } else {
                        fr = new FriendRecord();
                        fr.uin = fc.arrUin[ii];
                        fr.friendStatus = FriendRecord.STATUS_FRIEND_MUTUAL;
                        fr.nick = fc.arrNick[ii];
                        fr.remark = fc.arrRemark[ii];
                        fr.serverTime = fc.serverTime;
                        persons.put(fc.arrUin[ii], fr);
                        dirtySerializedFlag = true;
                    }
                }
            }
            Iterator<Map.Entry<Long, FriendRecord>> it = del.entrySet().iterator();
            Map.Entry<Long, FriendRecord> ent;
            EventRecord ev;
            ptr[0] = 0;//num,ticker,title,content
            while (it.hasNext()) {
                ent = it.next();
                fr = ent.getValue();
                if (fr.friendStatus == FriendRecord.STATUS_FRIEND_MUTUAL) {
                    ev = new EventRecord();
                    ev._friendStatus = fr.friendStatus;
                    ev._nick = fr.nick;
                    ev._remark = fr.remark;
                    ev.event = EventRecord.EVENT_FRIEND_DELETE;
                    ev.operand = fr.uin;
                    ev.executor = -1;
                    ev.timeRangeBegin = fr.serverTime;
                    ev.timeRangeEnd = fcs[fcs.length - 1].serverTime;
                    reportEventWithoutSave(ev, ptr);
                    fr.friendStatus = FriendRecord.STATUS_EXFRIEND;
                }
            }
        }
        lastUpdateTimeSec = fcs[0].serverTime;
        if (lastUpdateTimeSec == 0) {
            lastUpdateTimeSec = System.currentTimeMillis() / 1000L;
        }
        doNotifyDelFlAndSave(ptr);
    }

    public void markActiveDelete(long uin) {
        synchronized (this) {
            FriendRecord fr = persons.get(uin);
            if (fr == null) {
                Toasts.error(null, "onActDelResp: get(" + uin + ")==null");
                return;
            }
            EventRecord ev = new EventRecord();
            ev._friendStatus = fr.friendStatus;
            ev._nick = fr.nick;
            ev._remark = fr.remark;
            ev.timeRangeBegin = fr.serverTime;
            ev.timeRangeEnd = fr.serverTime = System.currentTimeMillis() / 1000;
            fr.friendStatus = FriendRecord.STATUS_EXFRIEND;
            ev.executor = this.getUin();
            ev.operand = uin;
            ev.event = EventRecord.EVENT_FRIEND_DELETE;
            reportEventWithoutSave(ev, null);
            saveConfigure();
        }
    }

    @SuppressLint("MissingPermission")
    public void doNotifyDelFlAndSave(Object[] ptr) {
        dirtySerializedFlag = true;
        mConfig.putLong("lastUpdateFl", lastUpdateTimeSec);
        saveConfigure();
        try {
            if (isNotifyWhenDeleted() && ((int) ptr[0]) > 0) {
                Intent inner = new Intent(HostInfo.getHostInfo().getApplication(),
                    ExfriendListActivity.class);
                Intent wrapper = new Intent();
                wrapper.setClassName(
                    HostInfo.getHostInfo().getApplication().getPackageName(),
                    ActProxyMgr.STUB_DEFAULT_ACTIVITY);
                wrapper.putExtra(ActProxyMgr.ACTIVITY_PROXY_INTENT, inner);
                PendingIntent pi = PendingIntent
                    .getActivity(HostInfo.getHostInfo().getApplication(), 0,
                        wrapper, 0);
                NotificationManager nm = (NotificationManager) HostInfo
                    .getHostInfo().getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
                Notification n = createNotiComp(nm, (String) ptr[1], (String) ptr[2],
                    (String) ptr[3], new long[]{100, 200, 200, 100}, pi);
                nm.notify(ID_EX_NOTIFY, n);
                setRedDot();
            }
        } catch (Exception e) {
            log(e);
        }
    }

    //TODO: IPC notify
    public boolean isNotifyWhenDeleted() {
        return mConfig.getBoolean("qn_notify_when_del", true);
    }

    public void setNotifyWhenDeleted(boolean z) {
        mConfig.putBoolean("qn_notify_when_del", z);
        saveConfigure();
    }

    @SuppressWarnings("deprecation")
    public Notification createNotiComp(NotificationManager nm, String ticker, String title,
        String content, long[] vibration, PendingIntent pi) {
        Application app = HostInfo.getHostInfo().getApplication();
        //Do not use NotificationCompat, NotificationCompat does NOT support setSmallIcon with Bitmap.
        Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("qn_del_notify", "删好友通知",
                NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null);
            channel.setVibrationPattern(vibration);
            nm.createNotificationChannel(channel);
            builder = new Notification.Builder(app, channel.getId());
        } else {
            builder = new Notification.Builder(app);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Parasitics.injectModuleResources(app.getResources());
            //We have to createWithBitmap rather than with a ResId, otherwise RemoteServiceException
            builder.setSmallIcon(Icon.createWithBitmap(
                BitmapFactory.decodeResource(app.getResources(), R.drawable.ic_del_friend_top)));
        } else {
            //2020 now, still using <23?
            builder.setSmallIcon(android.R.drawable.ic_delete);
        }
        builder.setTicker(ticker);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setContentIntent(pi);
        builder.setVibrate(vibration);
        return builder.build();
    }


    public void doRequestFlRefresh() {
        boolean inLogin;
        inLogin = (Utils.getLongAccountUin() == mUin);
        if (!inLogin) {
            logi("doRequestFlRefresh but uin(" + mUin + ") isn't logged in.");
            return;
        }
        try {
            invoke_virtual_any(Utils.getFriendListHandler(), true, true, boolean.class,
                boolean.class, void.class);
        } catch (Exception e) {
            log(e);
        }
    }

    public long getLastUpdateTimeSec() {
        return lastUpdateTimeSec;
    }

    public void timeToUpdateFl() {
        long t = System.currentTimeMillis() / 1000;
        if (t - lastUpdateTimeSec > FL_UPDATE_INT_MIN) {
            tp.execute(this::doRequestFlRefresh);
        }
    }
}
