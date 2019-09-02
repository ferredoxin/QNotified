package nil.nadph.qnotified;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static android.view.View.GONE;
import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.Initiator.load;
import static nil.nadph.qnotified.Utils.*;


public class TroopSelectAdapter extends BaseAdapter implements View.OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener, Comparator<TroopSelectAdapter.TroopInfo> {

    public static int HIGHLIGHT_COLOR = 0xFF3030C0;

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
            searchMode=false;
            search.setFocusable(false);
            search.setText("");
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View v2 = mActivity.getWindow().peekDecorView();
            if (null != v) {
                imm.hideSoftInputFromWindow(v2.getWindowToken(), 0);
            }
            cancel.setVisibility(GONE);
            selectAll.setVisibility(View.VISIBLE);
            reverse.setVisibility(View.VISIBLE);
        } else if (v == search) {
			/*try{
				Utils.showToastShort(mActivity,"setFocusable");
			}catch(Throwable e){}*/
			searchMode=true;
            search.setFocusable(true);
            search.setFocusableInTouchMode(true);
            search.requestFocus();
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
            cancel.setVisibility(View.VISIBLE);
            selectAll.setVisibility(GONE);
            reverse.setVisibility(GONE);
        }
    }

    private ArrayList<TroopInfo> mTroopInfoList;
    //private ArrayList<TroopInfo> hits = null;
    private int hits;
    private boolean searchMode = false;

    @Override
    public int getCount() {
        if (searchMode && hits > 0) {
            return hits;
        } else return mTroopInfoList == null ? 0 : mTroopInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        if (searchMode && hits > 0) {
            return mTroopInfoList.get(position);
        } else return mTroopInfoList == null ? null : mTroopInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        TroopInfo info = (TroopInfo) getItem(position);
        return info == null ? -1 : Long.parseLong(info.troopuin);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)convertView=createItemView();
        TroopInfo info=mTroopInfoList.get(position);
        convertView.setTag(info.troopuin);
        TextView title = convertView.findViewById(R_ID_TRP_TITLE);
        title.setText(info._troopname);
        TextView subtitle = convertView.findViewById(R_ID_TRP_SUBTITLE);
        subtitle.setText(info._troopuin);
        ImageView imgview = convertView.findViewById(R_ID_TRP_FACE);
        Bitmap bm = face.getBitmapFromCache(FaceImpl.TYPE_TROOP, info.troopuin);
        if (bm == null) {
            imgview.setImageDrawable(QThemeKit.loadDrawableFromAsset("face.png", mActivity));
            face.registerView(FaceImpl.TYPE_USER, info.troopuin, imgview);
        } else {
            imgview.setImageBitmap(bm);
        }
        return convertView;
    }


    private ViewGroup mListView;
    private Activity mActivity;

    private static final int R_ID_TRP_LAYOUT = 0x300AFF30;
    private static final int R_ID_TRP_TITLE = 0x300AFF31;
    private static final int R_ID_TRP_SUBTITLE = 0x300AFF32;
    private static final int R_ID_TRP_FACE = 0x300AFF33;
    private static final int R_ID_TRP_CHECKBOX = 0x300AFF34;
    private static final int R_ID_TRP_CANCEL = 0x300AFF35;
    private static final int R_ID_TRP_SEARCH_EDIT = 0x300AFF36;
    private static final int R_ID_TRP_REVERSE = 0x300AFF37;
    private static final int R_ID_TRP_SELECT_ALL = 0x300AFF38;


    private int mActionInt;
    private FaceImpl face;

    EditText search;
    TextView rightBtn, cancel, reverse, selectAll;

    public TroopSelectAdapter(Activity act, int action) {
        mActivity = act;
        mActionInt = action;
        muted=new HashSet<>();
        try {
            face = FaceImpl.getInstance();
        } catch (Throwable e) {
            log(e);
        }
    }

    public void doOnPostCreate() throws Throwable {
        int bar_hi = WRAP_CONTENT;//dip2px(mActivity,30);
        ColorStateList cTitle = QThemeKit.skin_black;
        LinearLayout main = new LinearLayout(mActivity);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setGravity(Gravity.CENTER_HORIZONTAL);
        main.setBackgroundDrawable(QThemeKit.skin_background);
        LinearLayout bar = new LinearLayout(mActivity);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        search = new EditText(mActivity);
        search.setHint("搜索...");
        search.setPadding(3, 3, 3, 3);
        search.setFocusable(false);
        search.setOnClickListener(this);
        search.setId(R_ID_TRP_SEARCH_EDIT);
        search.addTextChangedListener(this);
        search.setTextColor(cTitle);
        search.setBackgroundDrawable(null);
        LinearLayout.LayoutParams btnlp = new LinearLayout.LayoutParams(WRAP_CONTENT, bar_hi);
        LinearLayout.LayoutParams searchlp = new LinearLayout.LayoutParams(WRAP_CONTENT, bar_hi);
        searchlp.weight = 1;
        reverse = new Button(mActivity);
        reverse.setText("反选");
        reverse.setId(R_ID_TRP_REVERSE);
        reverse.setTextColor(cTitle);
        reverse.setBackgroundDrawable(QThemeKit.getListItemBackground());
        reverse.setOnClickListener(this);
        selectAll = new Button(mActivity);
        selectAll.setText("全选");
        selectAll.setId(R_ID_TRP_SELECT_ALL);
        selectAll.setTextColor(cTitle);
        selectAll.setBackgroundDrawable(QThemeKit.getListItemBackground());
        selectAll.setOnClickListener(this);
        cancel = new Button(mActivity);
        cancel.setText("取消");
        cancel.setTextColor(cTitle);
        cancel.setId(R_ID_TRP_CANCEL);
        cancel.setBackgroundDrawable(QThemeKit.getListItemBackground());
        cancel.setOnClickListener(this);
        cancel.setVisibility(GONE);
        bar.addView(search, searchlp);
        bar.addView(reverse, btnlp);
        bar.addView(selectAll, btnlp);
        bar.addView(cancel, btnlp);
        main.addView(bar, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        ViewGroup sdlv = (ViewGroup) load("com.tencent.widget.SwipListView").getConstructor(Context.class, AttributeSet.class).newInstance(mActivity, null);
        //sdlv.setFocusable(true);
        //sdlv.setBackgroundDrawable(QThemeKit.skin_background);
        FrameLayout f = new FrameLayout(mActivity);
        TextView tv = new TextView(mActivity);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(QThemeKit.skin_gray3);
        tv.setTextSize(Utils.dip2sp(mActivity, 14));
        try {
            mTroopInfoList = getTroopInfoList();
            tv.setText("若此处群列表显示不完整,请返回后在QQ的联系人的群列表下拉刷新后再回到此处重试");
        } catch (Exception e) {
            tv.setText("" + e);
        }
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        flp.gravity = Gravity.CENTER_HORIZONTAL;
        f.addView(tv, flp);
        f.addView(sdlv, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        main.addView(f, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        mActivity.setContentView(main);
        //sdlv.setBackgroundColor(0xFFAA0000)
        String title = "Fatal error!";
        if (mActionInt == QQMainHook.ACTION_MUTE_AT_ALL)
            title = "屏蔽@全体成员";
        else if (mActionInt == QQMainHook.ACTION_MUTE_RED_PACKET)
            title = "屏蔽群红包";
        invoke_virtual(mActivity, "setTitle", title, CharSequence.class);
        invoke_virtual(mActivity, "setImmersiveStatus");
        invoke_virtual(mActivity, "enableLeftBtn", true, boolean.class);
        rightBtn = (TextView) invoke_virtual(mActivity, "getRightTextView");
        //log("Title:"+invoke_virtual(mActivity,"getTextTitle"));
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText("完成(%d)");
        rightBtn.setEnabled(true);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //mActivity.onBackPressed();
                    //ExfriendManager.getCurrent().doRequestFlRefresh();
                    Utils.showToastShort(v.getContext(), "即将开放(没啥好完成的)...");
                    //startProxyActivity(mActivity,ACTION_ADV_SETTINGS);
                    //Intent intent=new Intent(v.getContext(),load(ActProxyMgr.DUMMY_ACTIVITY));
                    //int id=ActProxyMgr.next();
                    //intent.putExtra(ACTIVITY_PROXY_ID_TAG,id);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						/*v.getContext().startActivity(intent);/*
						 new Thread(new Runnable(){
						 @Override
						 public void run(){
						 /*try{
						 Thread.sleep(10000);
						 }catch(InterruptedException e){}
						 EventRecord ev=new EventRecord();
						 ev.operator=10000;
						 ev._remark=ev._nick="麻花藤";
						 *
						 ExfriendManager.getCurrent().doNotifyDelFl(new Object[]{1,"ticker","title","content"});
						 }
						 }).start();*/
                } catch (Throwable e) {
                    log(e);
                }
            }
        });
        //.addView(sdlv,lp);
        invoke_virtual(sdlv, "setDragEnable", true, boolean.class);
        invoke_virtual(sdlv, "setDivider", null, Drawable.class);
        QQViewBuilder.listView_setAdapter(sdlv, this);
        //invoke_virtual(sdlv,"setOnScrollGroupFloatingListener",true,load("com/tencent/widget/AbsListView$OnScrollListener"));

    }

    private LinearLayout createItemView() {
        int std_mg = dip2px(mActivity, 20), tmp;
        LinearLayout llayout = new LinearLayout(mActivity);
        //RelativeLayout rlayout = new RelativeLayout(mActivity);
        llayout.setGravity(Gravity.CENTER_VERTICAL);
        llayout.setOrientation(LinearLayout.HORIZONTAL);
        llayout.setPadding(std_mg, std_mg / 2, 0, std_mg / 2);
        llayout.setBackgroundDrawable(QThemeKit.getListItemBackground());
        llayout.setOnClickListener(this);
        CheckBox checkBox = new CheckBox(mActivity);
        checkBox.setId(R_ID_TRP_CHECKBOX);
        checkBox.setOnCheckedChangeListener(this);
        LinearLayout.LayoutParams imglp = new LinearLayout.LayoutParams(Utils.dip2px(mActivity, 50), Utils.dip2px(mActivity, 50));
        imglp.setMargins(tmp = Utils.dip2px(mActivity, 6), tmp, tmp, tmp);
        ImageView imgview = new ImageView(mActivity);
        imgview.setFocusable(false);
        imgview.setClickable(false);
        imgview.setId(R_ID_TRP_FACE);
        imgview.setScaleType(ImageView.ScaleType.FIT_XY);
        llayout.addView(checkBox, WRAP_CONTENT, WRAP_CONTENT);
        llayout.addView(imgview, imglp);
        LinearLayout textlayout = new LinearLayout(mActivity);
        textlayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams ltxtlp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        LinearLayout.LayoutParams textlp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        ltxtlp.setMargins(tmp = Utils.dip2px(mActivity, 2), tmp, tmp, tmp);
        textlp.setMargins(tmp = Utils.dip2px(mActivity, 1), tmp, tmp, tmp);
        llayout.addView(textlayout, ltxtlp);

        TextView title = new TextView(mActivity);
        title.setId(R_ID_TRP_TITLE);
        title.setSingleLine();
        //title.setText(ev.getShowStr());
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTextColor(new ColorStateList(QThemeKit.skin_black.getStates(), QThemeKit.skin_black.getColors()));
        title.setTextSize(Utils.px2sp(mActivity, Utils.dip2px(mActivity, 16)));
        //title.setPadding(tmp=Utils.dip2px(ctx,8),tmp,0,tmp);

        TextView subtitle = new TextView(mActivity);
        subtitle.setId(R_ID_TRP_SUBTITLE);
        subtitle.setSingleLine();
        subtitle.setGravity(Gravity.CENTER_VERTICAL);
        subtitle.setTextColor(new ColorStateList(QThemeKit.skin_gray3.getStates(), QThemeKit.skin_gray3.getColors()));
        subtitle.setTextSize(Utils.px2sp(mActivity, Utils.dip2px(mActivity, 14)));
        //subtitle.setPadding(tmp,0,0,tmp);

        textlayout.addView(title, textlp);
        textlayout.addView(subtitle, textlp);

        llayout.addView(textlayout, WRAP_CONTENT, WRAP_CONTENT);
        return llayout;
    }

    public static ArrayList<TroopInfo> getTroopInfoList() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object mTroopManager = XposedHelpers.callMethod(getQQAppInterface(), "getManager", 51);
        ArrayList tx = (ArrayList) invoke_virtual(mTroopManager, "a", "ArrayList");
        ArrayList<TroopInfo> ret = new ArrayList<TroopInfo>();
        for (Object info : tx) {
            ret.add(new TroopInfo(info));
        }
        return ret;
    }

    public void parseKeyword(String keyword) {
        //if(hits == null)hits=new ArrayList<>();
        hits = 0;
        if (false && keyword.contains(" ")) {
            String[] words = keyword.split(" ");

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
        Collections.sort(mTroopInfoList, this);
    }

    private Set<String> muted;

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        LinearLayout ll = (LinearLayout) buttonView.getParent();
        String guin = (String) ll.getTag();
        if (guin != null) muted.add(guin);
    }

    @Override
    public int compare(TroopInfo t1, TroopInfo t2) {
        return t2.hit - t1.hit;
    }

    public static class TroopInfo {
        public TroopInfo(Object obj) {
            _troopname = troopname = (String) iget_object(obj, "troopname");
            _troopuin = troopuin = (String) iget_object(obj, "troopuin");
            hit = 0;
        }

        public String troopuin;
        public String troopname;
        public CharSequence _troopuin;
        public CharSequence _troopname;
        public int hit;
    }

}
