package nil.nadph.qnotified.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.FaceImpl;
import nil.nadph.qnotified.StartupHook;
import nil.nadph.qnotified.record.EventRecord;
import nil.nadph.qnotified.record.FriendRecord;
import nil.nadph.qnotified.util.QQViewBuilder;
import nil.nadph.qnotified.util.QThemeKit;
import nil.nadph.qnotified.util.Utils;

import java.util.*;

import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

//import de.robv.android.xposed.*;

public class ExfriendListAdapter extends BaseAdapter implements ActivityAdapter {

    private Activity self;
    //private View mListView;
    private FaceImpl face;
    private ExfriendManager exm;
    private HashMap<Integer, EventRecord> eventsMap;
    private ArrayList<EventRecord> evs;

    private static final int R_ID_EXL_TITLE = 0x300AFF01;
    private static final int R_ID_EXL_SUBTITLE = 0x300AFF02;
    private static final int R_ID_EXL_FACE = 0x300AFF03;
    private static final int R_ID_EXL_STATUS = 0x300AFF04;

    @Override
    public void doOnPostCreate(Bundle savedInstanceState) throws Throwable {
        ViewGroup sdlv = (ViewGroup) load("com.tencent.widget.SwipListView").getConstructor(Context.class, AttributeSet.class).newInstance(self, null);
        sdlv.setFocusable(true);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        RelativeLayout.LayoutParams mwllp = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        RelativeLayout rl = new RelativeLayout(self);//)new_instance(load("com/tencent/mobileqq/activity/fling/TopGestureLayout"),self,Context.class);
        //invoke_virtual(rl,"setInterceptScrollLRFlag",true,boolean.class);
        //invoke_virtual(rl,"setInterceptTouchFlag",true,boolean.class);
        //iput_object(rl,"
        rl.setBackgroundDrawable(QThemeKit.qq_setting_item_bg_nor);
        mwllp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mwllp.addRule(RelativeLayout.CENTER_VERTICAL);

        TextView tv = new TextView(self);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(QThemeKit.skin_gray3);
        tv.setTextSize(Utils.dip2sp(self, 14));
        rl.addView(tv, mwllp);
        rl.addView(sdlv, mmlp);
        self.setContentView(rl);
        invoke_virtual(self, "setTitle", "历史好友", CharSequence.class);
        invoke_virtual(self, "setImmersiveStatus");
        invoke_virtual(self, "enableLeftBtn", true, boolean.class);
        TextView rightBtn = (TextView) invoke_virtual(self, "getRightTextView");
        //log("Title:"+invoke_virtual(self,"getTextTitle"));
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText("高级");
        rightBtn.setEnabled(true);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //self.onBackPressed();
                    //ExfriendManager.getCurrent().doRequestFlRefresh();
                    //Utils.showToastShort(v.getContext(),"即将开放(没啥好设置的)...");
                    StartupHook.startProxyActivity(self, ActProxyMgr.ACTION_ADV_SETTINGS);
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
						 ev.operand=10000;
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
        long uin = Utils.getLongAccountUin();
        ExfriendManager exm = ExfriendManager.get(uin);
        exm.clearUnreadFlag();
        tv.setText("最后更新: " + Utils.getRelTimeStrSec(exm.lastUpdateTimeSec) + "\n长按列表可删除");
        QQViewBuilder.listView_setAdapter(sdlv, this);
        ActProxyMgr.setContentBackgroundDrawable(self, QThemeKit.skin_background);
        //invoke_virtual(sdlv,"setOnScrollGroupFloatingListener",true,load("com/tencent/widget/AbsListView$OnScrollListener"));
        self.getWindow().getDecorView().setTag(this);
    }


    public ExfriendListAdapter(Activity context) {
        self = context;
        try {
            face = FaceImpl.getInstance();
        } catch (Throwable e) {
            log(e);
        }
        exm = ExfriendManager.getCurrent();
        reload();
    }

    public void reload() {
        eventsMap = exm.getEvents();
        if (evs == null) evs = new ArrayList<>();
        else evs.clear();
        if (eventsMap == null) return;
        Iterator<Map.Entry<Integer, EventRecord>> it = eventsMap.entrySet().iterator();
        EventRecord ev;
        while (it.hasNext()) {
            ev = (EventRecord) it.next().getValue();
            evs.add(ev);
        }
        Collections.sort(evs);
        //log("ev size="+evs.size());
		/*try{
		 theme=QThemeKit.getCurrentTheme((Activity)ctx);
		 }catch(Throwable e){
		 theme=QThemeKit.getDefaultTheme();
		 log(e);
		 }*/
    }

    @Override
    public int getCount() {
        return evs.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EventRecord ev = evs.get(position);
        if (convertView == null) {
			/*TextView tv=new TextView(ctx);
			 //tv.setText("李王凯(1084515740)");
			 tv.setText("这是第"+position+"个");
			 tv.setPadding(32,32,32,32);
			 tv.setGravity(Gravity.CENTER_VERTICAL);
			 tv.setBackground(QThemeKit.getListItemBackground());
			 //tv.setBackgroundResource(0x7f020435);
			 //Utils.log("Decoded:"+Integer.toHexString(theme.skin_text_black.getDefaultColor()));
			 //tv.setBackgroundTintList(theme.qq_setting_item_bg_nor);
			 tv.setTextColor((position%2==1)?QThemeKit.skin_black:QThemeKit.skin_gray3);
			 */
            convertView = inflateItemView(ev);//tv;
        }
        //log(position+"/"+getCount());
        convertView.setTag(ev);
        TextView title = (TextView) convertView.findViewById(R_ID_EXL_TITLE);
        title.setText(ev.getShowStr());
        boolean isfri = false;

        TextView stat = (TextView) convertView.findViewById(R_ID_EXL_STATUS);
        try {
            if (exm.getPersons().get(ev.operand).friendStatus == FriendRecord.STATUS_FRIEND_MUTUAL)
                isfri = true;
        } catch (Exception e) {
        }

        if (isfri) {
            stat.setTextColor(QThemeKit.cloneColor(QThemeKit.skin_gray3));
            stat.setText("已恢复");
        } else {
            stat.setTextColor(QThemeKit.cloneColor(QThemeKit.skin_red));
            stat.setText("已删除");
        }
        TextView subtitle = (TextView) convertView.findViewById(R_ID_EXL_SUBTITLE);
        subtitle.setText(Utils.getIntervalDspMs(ev.timeRangeBegin * 1000, ev.timeRangeEnd * 1000));
        ImageView imgview = (ImageView) convertView.findViewById(R_ID_EXL_FACE);
        Bitmap bm = face.getBitmapFromCache(FaceImpl.TYPE_USER, "" + ev.operand);
        if (bm == null) {
            imgview.setImageDrawable(QThemeKit.loadDrawableFromAsset("face.png", self));
            face.registerView(FaceImpl.TYPE_USER, "" + ev.operand, imgview);
        } else {
            imgview.setImageBitmap(bm);
        }

        return convertView;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private View inflateItemView(EventRecord ev) {
        int tmp;
        RelativeLayout rlayout = new RelativeLayout(self);
        LinearLayout llayout = new LinearLayout(self);
        llayout.setGravity(Gravity.CENTER_VERTICAL);
        llayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout textlayout = new LinearLayout(self);
        textlayout.setOrientation(LinearLayout.VERTICAL);
        rlayout.setBackground(QThemeKit.getListItemBackground());

        LinearLayout.LayoutParams imglp = new LinearLayout.LayoutParams(Utils.dip2px(self, 50), Utils.dip2px(self, 50));
        imglp.setMargins(tmp = Utils.dip2px(self, 6), tmp, tmp, tmp);
        ImageView imgview = new ImageView(self);
        imgview.setFocusable(false);
        imgview.setClickable(false);
        imgview.setId(R_ID_EXL_FACE);


        imgview.setScaleType(ImageView.ScaleType.FIT_XY);
        llayout.addView(imgview, imglp);
        LinearLayout.LayoutParams ltxtlp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        LinearLayout.LayoutParams textlp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        ltxtlp.setMargins(tmp = Utils.dip2px(self, 2), tmp, tmp, tmp);
        textlp.setMargins(tmp = Utils.dip2px(self, 1), tmp, tmp, tmp);
        llayout.addView(textlayout, ltxtlp);


        TextView title = new TextView(self);
        title.setId(R_ID_EXL_TITLE);
        title.setSingleLine();
        //title.setText(ev.getShowStr());
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTextColor(QThemeKit.cloneColor(QThemeKit.skin_black));
        title.setTextSize(Utils.px2sp(self, Utils.dip2px(self, 16)));
        //title.setPadding(tmp=Utils.dip2px(ctx,8),tmp,0,tmp);

        TextView subtitle = new TextView(self);
        subtitle.setId(R_ID_EXL_SUBTITLE);
        subtitle.setSingleLine();
        subtitle.setGravity(Gravity.CENTER_VERTICAL);
        subtitle.setTextColor(QThemeKit.cloneColor(QThemeKit.skin_gray3));
        subtitle.setTextSize(Utils.px2sp(self, Utils.dip2px(self, 14)));
        //subtitle.setPadding(tmp,0,0,tmp);

        textlayout.addView(title, textlp);
        textlayout.addView(subtitle, textlp);

        RelativeLayout.LayoutParams statlp = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

        TextView stat = new TextView(self);
        stat.setId(R_ID_EXL_STATUS);
        stat.setSingleLine();
        stat.setGravity(Gravity.CENTER);
        stat.setTextSize(Utils.px2sp(self, Utils.dip2px(self, 16)));
        statlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        statlp.addRule(RelativeLayout.CENTER_VERTICAL);
        statlp.rightMargin = Utils.dip2px(self, 16);


        rlayout.addView(llayout);
        rlayout.addView(stat, statlp);


        rlayout.setClickable(true);
        rlayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                long uin = ((EventRecord) v.getTag()).operand;
                StartupHook.openProfileCard(v.getContext(), uin);
            }
        });
        rlayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                try {
                    Dialog qQCustomDialog = createDialog(v.getContext());
                    invoke_virtual(qQCustomDialog, "setTitle", "删除记录", String.class);
                    invoke_virtual(qQCustomDialog, "setMessage", "确认删除历史记录(" + ((EventRecord) v.getTag())._remark + ")", CharSequence.class);
                    invoke_virtual(qQCustomDialog, "setPositiveButton", "确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            exm.getEvents().values().remove(((EventRecord) v.getTag()));
                            exm.saveConfigure();
                            reload();
                            notifyDataSetChanged();
                        }
                    }, String.class, DialogInterface.OnClickListener.class);
                    invoke_virtual(qQCustomDialog, "setNegativeButton", "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, String.class, DialogInterface.OnClickListener.class);
                    invoke_virtual(qQCustomDialog, "show");
                } catch (Exception e) {
                    log(e);
                }
                return true;
            }
        });
        return rlayout;


    }

    @Override
    public void doOnPostDestory() throws Throwable {
    }

    @Override
    public void doOnPostPause() throws Throwable {
    }

    @Override
    public void doOnPostResume() throws Throwable {
    }

    @Override
    public boolean isWrapContent() throws Throwable {
        return true;
    }

    @Override
    public void doOnPostActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
