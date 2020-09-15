/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.activity;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.*;

import androidx.core.view.ViewCompat;

import com.tencent.widget.XListView;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.FaceImpl;
import nil.nadph.qnotified.util.Utils;

import static android.view.View.GONE;
import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.util.ActProxyMgr.*;
import static nil.nadph.qnotified.util.Utils.*;


@SuppressLint("Registered")
public class TroopSelectActivity extends IphoneTitleBarActivityCompat implements View.OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener {

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
    private ArrayList<TroopInfo> mTroopInfoList;
    private int hits;
    private boolean searchMode = false;
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
    private int mActionInt;
    private FaceImpl face;
    private EditText search;
    private TextView rightBtn, cancel, reverse, selectAll;
    private HashSet<String> muted;

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
            if (m.getReturnType().equals(ArrayList.class) && Modifier.isPublic(m.getModifiers()) && m.getParameterTypes().length == 0) {
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
            tx = (ArrayList<?>) ((strcmp(m0a.getName(), m0b.getName()) > 0) ? m0b : m0a).invoke(mTroopManager);
        }
        return tx;
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
        if (s.length() == 0) return;
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
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            View v2 = this.getWindow().peekDecorView();
            if (null != v) {
                imm.hideSoftInputFromWindow(v2.getWindowToken(), 0);
            }
            cancel.setVisibility(GONE);
            selectAll.setVisibility(View.VISIBLE);
            reverse.setVisibility(View.VISIBLE);
            mAdapter.notifyDataSetChanged();
        } else if (v == search) {
			/*try{
				Utils.showToastShort(this,"setFocusable");
			}catch(Throwable e){}*/
            searchMode = true;
            search.setFocusable(true);
            search.setFocusableInTouchMode(true);
            search.requestFocus();
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
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
            } else ret = sb.substring(1);
            try {
                ConfigManager cfg = ExfriendManager.getCurrent().getConfig();
                if (mActionInt == ACTION_MUTE_AT_ALL) {
                    cfg.putString(ConfigItems.qn_muted_at_all, ret);
                    cfg.save();
                }
                if (mActionInt == ACTION_MUTE_RED_PACKET) {
                    cfg.putString(ConfigItems.qn_muted_red_packet, ret);
                    cfg.save();
                }
                if (mActionInt == ACTION_CHAT_TAIL_TROOPS_ACTIVITY) {
                    cfg.putString(ConfigItems.qn_chat_tail_troops, ret);
                    cfg.save();
                }
                this.finish();
            } catch (Exception e) {
                try {
                    log(e);
                    Utils.showToast(this, Utils.TOAST_TYPE_ERROR, e.toString(), Toast.LENGTH_SHORT);
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
        } else return mTroopInfoList == null ? 0 : mTroopInfoList.size();
    }

    public Object getItem(int position) {
        if (searchMode && hits > 0) {
            return mTroopInfoList.get(position);
        } else return mTroopInfoList == null ? null : mTroopInfoList.get(position);
    }

    public long getItemId(int position) {
        TroopInfo info = (TroopInfo) getItem(position);
        return info == null ? -1 : Long.parseLong(info.troopuin);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = createItemView();
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
        mActionInt = getIntent().getIntExtra(ACTIVITY_PROXY_ACTION, -1);
        if (mActionInt == -1) {
            finish();
            return true;
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
        //search.setBackgroundDrawable(null);
        ViewCompat.setBackground(search,null);
        LinearLayout.LayoutParams btnlp = new LinearLayout.LayoutParams(WRAP_CONTENT, bar_hi);
        LinearLayout.LayoutParams searchlp = new LinearLayout.LayoutParams(WRAP_CONTENT, bar_hi);
        searchlp.weight = 1;
        reverse = new Button(this);
        reverse.setText("反选");
        reverse.setId(R_ID_TRP_REVERSE);
        reverse.setTextColor(cTitle);
        //reverse.setBackgroundDrawable(null);
        ViewCompat.setBackground(reverse,null);
        reverse.setOnClickListener(this);
        selectAll = new Button(this);
        selectAll.setText("全选");
        selectAll.setId(R_ID_TRP_SELECT_ALL);
        selectAll.setTextColor(cTitle);
        //selectAll.setBackgroundDrawable(null);
        ViewCompat.setBackground(selectAll,null);
        selectAll.setOnClickListener(this);
        cancel = new Button(this);
        cancel.setText("取消");
        cancel.setTextColor(cTitle);
        cancel.setId(R_ID_TRP_CANCEL);
        //cancel.setBackgroundDrawable(null);
        ViewCompat.setBackground(cancel,null);
        cancel.setOnClickListener(this);
        cancel.setVisibility(GONE);
        bar.addView(search, searchlp);
        bar.addView(reverse, btnlp);
        bar.addView(selectAll, btnlp);
        bar.addView(cancel, btnlp);
        main.addView(bar, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        XListView sdlv = new XListView(this, null);
        //sdlv.setFocusable(true);
        //sdlv.setBackgroundDrawable(ResUtils.skin_background);
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
        String title = "Fatal error!";
        if (mActionInt == ACTION_MUTE_AT_ALL)
            title = "屏蔽@全体成员";
        else if (mActionInt == ACTION_MUTE_RED_PACKET)
            title = "屏蔽群红包";
        else if (mActionInt == ACTION_CHAT_TAIL_TROOPS_ACTIVITY)
            title = "选择小尾巴生效群";
        setTitle(title);
        rightBtn = (TextView) getRightTextView();
        //log("Title:"+invoke_virtual(this,"getTextTitle"));
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText("完成");
        rightBtn.setEnabled(true);
        rightBtn.setOnClickListener(this);
        //.addView(sdlv,lp);
        sdlv.setDivider(null);
        sdlv.setAdapter(mAdapter);
        //invoke_virtual(sdlv,"setOnScrollGroupFloatingListener",true,load("com/tencent/widget/AbsListView$OnScrollListener"));
        muted = new HashSet<>();
        String list = null;
        if (mActionInt == ACTION_MUTE_AT_ALL)
            list = ExfriendManager.getCurrent().getConfig().getString(ConfigItems.qn_muted_at_all);
        if (mActionInt == ACTION_MUTE_RED_PACKET)
            list = ExfriendManager.getCurrent().getConfig().getString(ConfigItems.qn_muted_red_packet);
        if (mActionInt == ACTION_CHAT_TAIL_TROOPS_ACTIVITY)
            list = ExfriendManager.getCurrent().getConfig().getString(ConfigItems.qn_chat_tail_troops);
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
        //RelativeLayout rlayout = new RelativeLayout(this);
        llayout.setGravity(Gravity.CENTER_VERTICAL);
        llayout.setOrientation(LinearLayout.HORIZONTAL);
        llayout.setPadding(std_mg, std_mg / 2, 0, std_mg / 2);
        //llayout.setBackgroundDrawable(ResUtils.getListItemBackground());
        ViewCompat.setBackground(llayout,ResUtils.getListItemBackground());
        llayout.setOnClickListener(this);
        llayout.setId(R_ID_TRP_LAYOUT);
        CheckBox checkBox = new CheckBox(this);
        checkBox.setId(R_ID_TRP_CHECKBOX);
        checkBox.setOnCheckedChangeListener(this);
        checkBox.setButtonDrawable(null);
        //checkBox.setBackgroundDrawable(ResUtils.getCheckBoxBackground());
        ViewCompat.setBackground(checkBox,ResUtils.getCheckBoxBackground());
        LinearLayout.LayoutParams imglp = new LinearLayout.LayoutParams(Utils.dip2px(this, 50), Utils.dip2px(this, 50));
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
        LinearLayout.LayoutParams ltxtlp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        LinearLayout.LayoutParams textlp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        ltxtlp.setMargins(tmp = Utils.dip2px(this, 2), tmp, tmp, tmp);
        textlp.setMargins(tmp = Utils.dip2px(this, 1), tmp, tmp, tmp);
        llayout.addView(textlayout, ltxtlp);

        TextView title = new TextView(this);
        title.setId(R_ID_TRP_TITLE);
        title.setSingleLine();
        //title.setText(ev.getShowStr());
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTextColor(ResUtils.cloneColor(ResUtils.skin_black));
        title.setTextSize(Utils.px2sp(this, Utils.dip2px(this, 16)));
        //title.setPadding(tmp=Utils.dip2px(ctx,8),tmp,0,tmp);

        TextView subtitle = new TextView(this);
        subtitle.setId(R_ID_TRP_SUBTITLE);
        subtitle.setSingleLine();
        subtitle.setGravity(Gravity.CENTER_VERTICAL);
        subtitle.setTextColor(ResUtils.cloneColor(ResUtils.skin_gray3));
        subtitle.setTextSize(Utils.px2sp(this, Utils.dip2px(this, 14)));
        //subtitle.setPadding(tmp,0,0,tmp);

        textlayout.addView(title, textlp);
        textlayout.addView(subtitle, textlp);

        return llayout;
    }

    public void parseKeyword(String keyword) {
        //if(hits == null)hits=new ArrayList<>();
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
                    ret.setSpan(new ForegroundColorSpan(HIGHLIGHT_COLOR), start, start + len, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    info._troopuin = ret;
                    info.hit += 10;
                    y = true;
                } else info._troopuin = info.troopuin;
                start = info.troopname.indexOf(keyword);
                if (start != -1) {
                    SpannableString ret = new SpannableString(info.troopname);
                    ret.setSpan(new ForegroundColorSpan(HIGHLIGHT_COLOR), start, start + len, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    info._troopname = ret;
                    info.hit += 10;
                    y = true;
                } else info._troopname = info.troopname;
                if (y) hits++;
            }

        }
        Collections.sort(mTroopInfoList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        LinearLayout ll = (LinearLayout) buttonView.getParent();
        String guin = (String) ll.getTag();
        if (guin == null) return;
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
