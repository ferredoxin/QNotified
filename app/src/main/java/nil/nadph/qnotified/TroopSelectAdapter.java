package nil.nadph.qnotified;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import static android.view.View.GONE;
import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.Initiator.load;
import static nil.nadph.qnotified.Utils.*;


public class TroopSelectAdapter extends BaseAdapter implements View.OnClickListener, TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // TODO: Implement this method
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO: Implement this method
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO: Implement this method
    }

    @Override
    public void onClick(View v) {
        if (v == cancel) {
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


    @Override
    public int getCount() {
        // TODO: Implement this method
        return 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO: Implement this method
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO: Implement this method
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO: Implement this method
        return null;
    }



    private ViewGroup mListView;
    private Activity mActivity;

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
    TextView rightBtn,cancel,reverse,selectAll;

    public TroopSelectAdapter(Activity act, int action) {
        mActivity = act;
        mActionInt = action;
        try {
            face = FaceImpl.getInstance();
        } catch (Throwable e) {
            log(e);
        }
    }

    public void doOnPostCreate() throws Throwable {
        int bar_hi=WRAP_CONTENT;//dip2px(mActivity,30);
        ColorStateList cTitle=QThemeKit.skin_black;
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
        LinearLayout.LayoutParams btnlp=new LinearLayout.LayoutParams(WRAP_CONTENT,bar_hi);
        LinearLayout.LayoutParams searchlp=new LinearLayout.LayoutParams(WRAP_CONTENT,bar_hi);
        searchlp.weight=1;
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
        bar.addView(search,searchlp);
        bar.addView(reverse,btnlp);
        bar.addView(selectAll,btnlp);
        bar.addView(cancel,btnlp);
        main.addView(bar, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        ViewGroup sdlv = (ViewGroup) load("com.tencent.widget.SwipListView").getConstructor(Context.class, AttributeSet.class).newInstance(mActivity, null);
        //sdlv.setFocusable(true);
        //sdlv.setBackgroundDrawable(QThemeKit.skin_background);
        FrameLayout f = new FrameLayout(mActivity);
        TextView tv = new TextView(mActivity);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(QThemeKit.skin_gray3);
        tv.setTextSize(Utils.dip2sp(mActivity, 14));
        tv.setText("若此处群列表显示不完整,请返回后在QQ的联系人的群列表下拉刷新后再回到此处重试");
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


}
