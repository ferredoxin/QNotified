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
package nil.nadph.qnotified.activity;

import static android.view.View.GONE;
import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.util.ReflexUtil.iget_object_or_null;
import static nil.nadph.qnotified.util.Utils.dip2px;
import static nil.nadph.qnotified.util.Utils.getTroopManager;
import static nil.nadph.qnotified.util.Utils.log;
import static nil.nadph.qnotified.util.Utils.strcmp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import com.tencent.widget.XListView;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.FaceImpl;
import nil.nadph.qnotified.util.Toasts;
import nil.nadph.qnotified.util.Utils;


@SuppressLint("Registered")
public class TroopSelectActivity extends IphoneTitleBarActivityCompat implements
    View.OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener {

    private static final String TRP_SELECT_EXFMGR_KEY_NAME = "TRP_SELECT_EXFMGR_KEY_NAME";
    private static final String TRP_SELECT_TITLE = "TRP_SELECT_TITLE";

    private static final int R_ID_TRP_LAYOUT = 0x300AFF30;
    private static final int R_ID_TRP_TITLE = 0x300AFF31;
    private static final int R_ID_TRP_SUBTITLE = 0x300AFF32;
    private static final int R_ID_TRP_FACE = 0x300AFF33;
    private static final int R_ID_TRP_CHECKBOX = 0x300AFF34;
    private static final int R_ID_TRP_CANCEL = 0x300AFF35;
    private static final int R_ID_TRP_SEARCH_EDIT = 0x300AFF36;
    private static final int R_ID_TRP_REVERSE = 0x300AFF37;
    private static final int R_ID_TRP_SELECT_ALL = 0x300AFF38;
    public static int HIGHLIGHT_COLOR = 0xFF20B0FF;
    String targetDataSaveKey, lpwTitle;
    private ArrayList<TroopInfo> mTroopInfoList;
    private int hits;
    private boolean searchMode = false;
    private FaceImpl face;
    private EditText search;
    private TextView rightBtn, cancel, reverse, selectAll;
    private HashSet<String> muted;
    private final BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return TroopSelectActivity.this.getCount();
        }

        @Override
        public Object getItem(int position) {
            return TroopSelectActivity.this.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return TroopSelectActivity.this.getItemId(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return TroopSelectActivity.this.getView(position, convertView, parent);
        }
    };

    public static ArrayList<TroopInfo> getTroopInfoList() throws Exception {
        Object mTroopManager = getTroopManager();
        ArrayList<?> tx = getTroopInfoListRaw();
        ArrayList<TroopInfo> ret = new ArrayList<TroopInfo>();
        for (Object info : tx) {
            ret.add(new TroopInfo(info));
        }
        return ret;
    }

    public static ArrayList<?> getTroopInfoListRaw() throws Exception {
        Object mTroopManager = getTroopManager();
        ArrayList<?> tx;
        Method m0a = null, m0b = null;
        for (Method m : mTroopManager.getClass().getMethods()) {
            if (m.getReturnType().equals(ArrayList.class) && Modifier.isPublic(m.getModifiers())
                && m.getParameterTypes().length == 0) {
                if (m.getName().equals("a")) {
                    m0a = m;
                    break;
                } else {
                    if (m0a == null) {
                        m0a = m;
                    } else {
                        m0b = m;
                        break;
                    }
                }
            }
        }
        if (m0b == null) {
            tx = (ArrayList<?>) m0a.invoke(mTroopManager);
        } else {
            tx = (ArrayList<?>) ((strcmp(m0a.getName(), m0b.getName()) > 0) ? m0b : m0a)
                .invoke(mTroopManager);
        }
        return tx;
    }

    public static void startToSelectTroopsAndSaveToExfMgr(@NonNull Context ctx,
        @NonNull String keyName) {
        startToSelectTroopsAndSaveToExfMgr(ctx, keyName, null);
    }

    public static void startToSelectTroopsAndSaveToExfMgr(@NonNull Context ctx,
        @NonNull String keyName, @Nullable String title) {
        Objects.requireNonNull(keyName, "keyName == null");
        Intent intent = new Intent(ctx, TroopSelectActivity.class);
        intent.putExtra(TRP_SELECT_EXFMGR_KEY_NAME, keyName);
        if (title != null) {
            intent.putExtra(TRP_SELECT_TITLE, title);
        }
        ctx.startActivity(intent);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String str = s.toString();
        int i = str.indexOf('\n');
        if (i != -1) {
            s.delete(i, i + 1);
        }
        str = s.toString();
        if (s.length() == 0) {
            return;
        }
        searchMode = true;
        parseKeyword(str);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R_ID_TRP_LAYOUT) {
            CheckBox c = v.findViewById(R_ID_TRP_CHECKBOX);
            c.toggle();
            return;
        }
        if (v == cancel) {
            searchMode = false;
            search.setFocusable(false);
            search.setText("");
            InputMethodManager imm = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
            View v2 = this.getWindow().peekDecorView();
            if (null != v) {
                imm.hideSoftInputFromWindow(v2.getWindowToken(), 0);
            }
            cancel.setVisibility(GONE);
            selectAll.setVisibility(View.VISIBLE);
            reverse.setVisibility(View.VISIBLE);
            mAdapter.notifyDataSetChanged();
        } else if (v == search) {
            searchMode = true;
            search.setFocusable(true);
            search.setFocusableInTouchMode(true);
            search.requestFocus();
            InputMethodManager imm = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
            cancel.setVisibility(View.VISIBLE);
            selectAll.setVisibility(GONE);
            reverse.setVisibility(GONE);
        } else if (v == rightBtn) {
            StringBuilder sb = new StringBuilder();
            for (String s : muted) {
                sb.append(',');
                sb.append(s);
            }
            String ret;
            if (sb.length() < 4) {
                ret = "";
            } else {
                ret = sb.substring(1);
            }
            try {
                ConfigManager cfg = ExfriendManager.getCurrent().getConfig();
                cfg.putString(targetDataSaveKey, ret);
                cfg.save();
                this.finish();
            } catch (Exception e) {
                try {
                    log(e);
                    Toasts.error(this, e.toString());
                } catch (Throwable ignored) {
                }
            }
        } else if (v == selectAll) {
            for (TroopInfo info : mTroopInfoList) {
                muted.add(info.troopuin);
            }
            mAdapter.notifyDataSetInvalidated();
        } else if (v == reverse) {
            for (TroopInfo info : mTroopInfoList) {
                HashSet<String> ref = (HashSet<String>) muted.clone();
                if (ref.contains(info.troopuin)) {
                    muted.remove(info.troopuin);
                } else {
                    muted.add(info.troopuin);
                }
            }
            mAdapter.notifyDataSetInvalidated();
        }
    }

    public int getCount() {
        if (searchMode && hits > 0) {
            return hits;
        } else {
            return mTroopInfoList == null ? 0 : mTroopInfoList.size();
        }
    }

    public Object getItem(int position) {
        if (searchMode && hits > 0) {
            return mTroopInfoList.get(position);
        } else {
            return mTroopInfoList == null ? null : mTroopInfoList.get(position);
        }
    }

    public long getItemId(int position) {
        TroopInfo info = (TroopInfo) getItem(position);
        return info == null ? -1 : Long.parseLong(info.troopuin);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = createItemView();
        }
        TroopInfo info = mTroopInfoList.get(position);
        convertView.setTag(info.troopuin);
        if (searchMode) {
            TextView title = convertView.findViewById(R_ID_TRP_TITLE);
            title.setText(info._troopname);
            TextView subtitle = convertView.findViewById(R_ID_TRP_SUBTITLE);
            subtitle.setText(info._troopuin);
        } else {
            TextView title = convertView.findViewById(R_ID_TRP_TITLE);
            title.setText(info.troopname);
            TextView subtitle = convertView.findViewById(R_ID_TRP_SUBTITLE);
            subtitle.setText(info.troopuin);
        }
        ImageView imgview = convertView.findViewById(R_ID_TRP_FACE);
        Bitmap bm = face.getBitmapFromCache(FaceImpl.TYPE_TROOP, info.troopuin);
        if (bm == null) {
            imgview.setImageDrawable(ResUtils.loadDrawableFromAsset("face.png", this));
            face.registerView(FaceImpl.TYPE_TROOP, info.troopuin, imgview);
        } else {
            imgview.setImageBitmap(bm);
        }
        boolean selected = muted.contains(info.troopuin);
        CheckBox check = convertView.findViewById(R_ID_TRP_CHECKBOX);
        check.setChecked(selected);
        return convertView;
    }

    @Override
    public boolean doOnCreate(Bundle savedInstanceState) {
        super.doOnCreate(savedInstanceState);
        targetDataSaveKey = getIntent().getStringExtra(TRP_SELECT_EXFMGR_KEY_NAME);
        if (targetDataSaveKey == null) {
            Toasts.error(this, "TRP_SELECT_EXFMGR_KEY_NAME is null!");
            finish();
            return true;
        }
        lpwTitle = getIntent().getStringExtra(TRP_SELECT_TITLE);
        if (lpwTitle == null) {
            lpwTitle = "选择群";
        }
        try {
            face = FaceImpl.getInstance();
        } catch (Throwable e) {
            log(e);
        }
        int bar_hi = WRAP_CONTENT;//dip2px(this,30);
        ColorStateList cTitle = ResUtils.skin_black;
        LinearLayout main = new LinearLayout(this);
        main.setId(R.id.rootMainLayout);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout bar = new LinearLayout(this);
        bar.setId(R.id.root_content_toolbarLayout);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        search = new EditText(this);
        search.setHint("搜索...群名或群号");
        search.setPadding(3, 3, 3, 3);
        search.setFocusable(false);
        search.setOnClickListener(this);
        search.setId(R_ID_TRP_SEARCH_EDIT);
        search.addTextChangedListener(this);
        search.setTextColor(cTitle);
        ViewCompat.setBackground(search, null);
        LinearLayout.LayoutParams btnlp = new LinearLayout.LayoutParams(WRAP_CONTENT, bar_hi);
        LinearLayout.LayoutParams searchlp = new LinearLayout.LayoutParams(WRAP_CONTENT, bar_hi);
        searchlp.weight = 1;
        reverse = new Button(this);
        reverse.setText("反选");
        reverse.setId(R_ID_TRP_REVERSE);
        reverse.setTextColor(cTitle);
        ViewCompat.setBackground(reverse, null);
        reverse.setOnClickListener(this);
        selectAll = new Button(this);
        selectAll.setText("全选");
        selectAll.setId(R_ID_TRP_SELECT_ALL);
        selectAll.setTextColor(cTitle);
        ViewCompat.setBackground(selectAll, null);
        selectAll.setOnClickListener(this);
        cancel = new Button(this);
        cancel.setText("取消");
        cancel.setTextColor(cTitle);
        cancel.setId(R_ID_TRP_CANCEL);
        ViewCompat.setBackground(cancel, null);
        cancel.setOnClickListener(this);
        cancel.setVisibility(GONE);
        bar.addView(search, searchlp);
        bar.addView(reverse, btnlp);
        bar.addView(selectAll, btnlp);
        bar.addView(cancel, btnlp);
        main.addView(bar, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        XListView sdlv = new XListView(this, null);
        FrameLayout f = new FrameLayout(this);
        TextView tv = new TextView(this);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(ResUtils.skin_gray3);
        tv.setTextSize(Utils.dip2sp(this, 14));
        try {
            mTroopInfoList = getTroopInfoList();
            tv.setText("若此处群列表显示不完整,请返回后在QQ的联系人的群列表下拉刷新后再回到此处重试");
        } catch (Exception e) {
            tv.setText(Log.getStackTraceString(e));
            log(e);
        }
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        flp.gravity = Gravity.CENTER_HORIZONTAL;
        f.addView(tv, flp);
        f.addView(sdlv, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        main.addView(f, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        this.setContentView(main);
        setTitle(lpwTitle);
        rightBtn = (TextView) getRightTextView();
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText("完成");
        rightBtn.setEnabled(true);
        rightBtn.setOnClickListener(this);
        sdlv.setDivider(null);
        sdlv.setAdapter(mAdapter);
        muted = new HashSet<>();
        String list = null;
        list = ExfriendManager.getCurrent().getConfig().getString(targetDataSaveKey);
        if (list != null) {
            for (String s : list.split(",")) {
                if (s.length() > 4) {
                    muted.add(s);
                }
            }
        }
        if (muted != null && muted.size() > 0) {
            rightBtn.setText("完成(" + muted.size() + ")");
        }
        setContentBackgroundDrawable(ResUtils.skin_background);
        this.getWindow().getDecorView().setTag(this);
        return true;
    }

    private LinearLayout createItemView() {
        int std_mg = dip2px(this, 16), tmp;
        LinearLayout llayout = new LinearLayout(this);
        llayout.setGravity(Gravity.CENTER_VERTICAL);
        llayout.setOrientation(LinearLayout.HORIZONTAL);
        llayout.setPadding(std_mg, std_mg / 2, 0, std_mg / 2);
        ViewCompat.setBackground(llayout, ResUtils.getListItemBackground());
        llayout.setOnClickListener(this);
        llayout.setId(R_ID_TRP_LAYOUT);
        CheckBox checkBox = new CheckBox(this);
        checkBox.setId(R_ID_TRP_CHECKBOX);
        checkBox.setOnCheckedChangeListener(this);
        checkBox.setButtonDrawable(null);
        ViewCompat.setBackground(checkBox, ResUtils.getCheckBoxBackground());
        LinearLayout.LayoutParams imglp = new LinearLayout.LayoutParams(Utils.dip2px(this, 50),
            Utils.dip2px(this, 50));
        imglp.setMargins(tmp = Utils.dip2px(this, 12), tmp / 2, tmp / 2, tmp / 2);
        ImageView imgview = new ImageView(this);
        imgview.setFocusable(false);
        imgview.setClickable(false);
        imgview.setId(R_ID_TRP_FACE);
        imgview.setScaleType(ImageView.ScaleType.FIT_XY);
        llayout.addView(checkBox, tmp = dip2px(this, 20), tmp);
        llayout.addView(imgview, imglp);
        LinearLayout textlayout = new LinearLayout(this);
        textlayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams ltxtlp = new LinearLayout.LayoutParams(MATCH_PARENT,
            WRAP_CONTENT);
        LinearLayout.LayoutParams textlp = new LinearLayout.LayoutParams(MATCH_PARENT,
            WRAP_CONTENT);
        ltxtlp.setMargins(tmp = Utils.dip2px(this, 2), tmp, tmp, tmp);
        textlp.setMargins(tmp = Utils.dip2px(this, 1), tmp, tmp, tmp);
        llayout.addView(textlayout, ltxtlp);

        TextView title = new TextView(this);
        title.setId(R_ID_TRP_TITLE);
        title.setSingleLine();
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTextColor(ResUtils.cloneColor(ResUtils.skin_black));
        title.setTextSize(Utils.px2sp(this, Utils.dip2px(this, 16)));

        TextView subtitle = new TextView(this);
        subtitle.setId(R_ID_TRP_SUBTITLE);
        subtitle.setSingleLine();
        subtitle.setGravity(Gravity.CENTER_VERTICAL);
        subtitle.setTextColor(ResUtils.cloneColor(ResUtils.skin_gray3));
        subtitle.setTextSize(Utils.px2sp(this, Utils.dip2px(this, 14)));

        textlayout.addView(title, textlp);
        textlayout.addView(subtitle, textlp);

        return llayout;
    }

    public void parseKeyword(String keyword) {
        hits = 0;
        if (false && keyword.contains(" ")) {
            String[] words = keyword.split(" ");
            //FIXME: temporary workaround for multi-keyword search
        } else {
            int start, len = keyword.length();
            for (TroopInfo info : mTroopInfoList) {
                info.hit = 0;
                start = info.troopuin.indexOf(keyword);
                boolean y = false;
                if (start != -1) {
                    SpannableString ret = new SpannableString(info.troopuin);
                    ret.setSpan(new ForegroundColorSpan(HIGHLIGHT_COLOR), start, start + len,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    info._troopuin = ret;
                    info.hit += 10;
                    y = true;
                } else {
                    info._troopuin = info.troopuin;
                }
                start = info.troopname.indexOf(keyword);
                if (start != -1) {
                    SpannableString ret = new SpannableString(info.troopname);
                    ret.setSpan(new ForegroundColorSpan(HIGHLIGHT_COLOR), start, start + len,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    info._troopname = ret;
                    info.hit += 10;
                    y = true;
                } else {
                    info._troopname = info.troopname;
                }
                if (y) {
                    hits++;
                }
            }

        }
        Collections.sort(mTroopInfoList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        LinearLayout ll = (LinearLayout) buttonView.getParent();
        String guin = (String) ll.getTag();
        if (guin == null) {
            return;
        }
        if (isChecked) {
            muted.add(guin);
        } else {
            muted.remove(guin);
        }
        rightBtn.setText("完成(" + muted.size() + ")");
    }

    public static class TroopInfo implements Comparable {

        public String troopuin;
        public String troopname;
        public CharSequence _troopuin;
        public CharSequence _troopname;
        public int hit;

        public TroopInfo(Object obj) {
            _troopname = troopname = (String) iget_object_or_null(obj, "troopname");
            _troopuin = troopuin = (String) iget_object_or_null(obj, "troopuin");
            hit = 0;
        }

        @Override
        public int compareTo(Object o) {
            TroopInfo t = (TroopInfo) o;
            return t.hit - hit;
        }
    }
}
