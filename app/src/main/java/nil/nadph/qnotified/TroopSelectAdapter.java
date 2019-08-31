package nil.nadph.qnotified;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.Initiator.load;
import static nil.nadph.qnotified.Utils.invoke_virtual;
import static nil.nadph.qnotified.Utils.log;


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

    Button cancel;
    EditText search;
    TextView rightBtn;

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
        } else if (v == search) {
			/*try{
				Utils.showToastShort(mActivity,"setFocusable");
			}catch(Throwable e){}*/
            search.setFocusable(true);
            search.setFocusableInTouchMode(true);
            search.requestFocus();
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);

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


    private int mActionInt;
    private FaceImpl face;


    public TroopSelectAdapter(Activity act, int action) {
        mActivity = act;
        mActionInt = action;
        try {
            face = FaceImpl.getInstance();
        } catch (Throwable e) {
            log(e);
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void doOnPostCreate() throws Throwable {
        LinearLayout main = new LinearLayout(mActivity);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setGravity(Gravity.CENTER_HORIZONTAL);
        main.setBackground(QThemeKit.skin_background);
        search = new EditText(mActivity);
        search.setHint("搜索...名称或群号");
        search.setPadding(3, 3, 3, 3);
        search.setFocusable(false);
        search.setOnClickListener(this);
        search.setId(R_ID_TRP_SEARCH_EDIT);
        search.addTextChangedListener(this);
        cancel = new Button(mActivity);
        cancel.setText("取消");
        cancel.setOnClickListener(this);
        LinearLayout ll = new LinearLayout(mActivity);
        LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lllp.weight = 1f;
        ll.addView(search, lllp);
        lllp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        ll.addView(cancel, lllp);
        main.addView(ll, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        ViewGroup sdlv = (ViewGroup) load("com.tencent.widget.SwipListView").getConstructor(Context.class, AttributeSet.class).newInstance(mActivity, null);
        //sdlv.setFocusable(true);
        sdlv.setBackground(QThemeKit.skin_background);
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
        if (mActionInt == QQMainHook.ACTION_SHUTUP_AT_ALL)
            title = "屏蔽@全体成员";
        else if (mActionInt == QQMainHook.ACTION_SHUTUP_RED_PACKET)
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
