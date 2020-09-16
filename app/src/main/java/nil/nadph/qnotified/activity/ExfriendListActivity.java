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
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

import com.tencent.widget.XListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.MainHook;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.config.EventRecord;
import nil.nadph.qnotified.config.FriendRecord;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.ui.ViewBuilder;
import nil.nadph.qnotified.util.ActProxyMgr;
import nil.nadph.qnotified.util.FaceImpl;
import nil.nadph.qnotified.util.Utils;

import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.util.Utils.log;

@SuppressLint("Registered")
public class ExfriendListActivity extends IphoneTitleBarActivityCompat {

    private static final int R_ID_EXL_TITLE = 0x300AFF01;
    private static final int R_ID_EXL_SUBTITLE = 0x300AFF02;
    private static final int R_ID_EXL_FACE = 0x300AFF03;
    private static final int R_ID_EXL_STATUS = 0x300AFF04;

    //private View mListView;
    private FaceImpl face;
    private ExfriendManager exm;
    private ArrayList<EventRecord> evs;
    private final BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return ExfriendListActivity.this.getCount();
        }

        @Override
        public Object getItem(int position) {
            return ExfriendListActivity.this.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return ExfriendListActivity.this.getItemId(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return ExfriendListActivity.this.getView(position, convertView, parent);
        }
    };

    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        try {
            face = FaceImpl.getInstance();
        } catch (Throwable e) {
            log(e);
        }
        exm = ExfriendManager.getCurrent();
        reload();

        XListView sdlv = new XListView(this, null);
        sdlv.setFocusable(true);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        RelativeLayout.LayoutParams mwllp = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        RelativeLayout rl = new RelativeLayout(ExfriendListActivity.this);//)new_instance(load("com/tencent/mobileqq/activity/fling/TopGestureLayout"),ExfriendListActivity.this,Context.class);
        rl.setId(R.id.rootMainLayout);
        sdlv.setId(R.id.rootMainList);
        //invoke_virtual(rl,"setInterceptScrollLRFlag",true,boolean.class);
        //invoke_virtual(rl,"setInterceptTouchFlag",true,boolean.class);
        //iput_object(rl,"
        mwllp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mwllp.addRule(RelativeLayout.CENTER_VERTICAL);

        TextView tv = new TextView(ExfriendListActivity.this);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(ResUtils.skin_gray3);
        tv.setTextSize(Utils.dip2sp(ExfriendListActivity.this, 14));
        rl.addView(tv, mwllp);
        rl.addView(sdlv, mmlp);
        setContentView(rl);
        setTitle("历史好友");

        TextView rightBtn = (TextView) getRightTextView();
        //log("Title:"+invoke_virtual(ExfriendListActivity.this,"getTextTitle"));
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText("高级");
        rightBtn.setEnabled(true);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //ExfriendListActivity.this.onBackPressed();
                    //ExfriendManager.getCurrent().doRequestFlRefresh();
                    //Utils.showToastShort(v.getContext(),"即将开放(没啥好设置的)...");
                    MainHook.startProxyActivity(ExfriendListActivity.this, ActProxyMgr.ACTION_ADV_SETTINGS);
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
        sdlv.setDivider(null);
        long uin = Utils.getLongAccountUin();
        ExfriendManager exm = ExfriendManager.get(uin);
        exm.clearUnreadFlag();
        tv.setText("最后更新: " + Utils.getRelTimeStrSec(exm.lastUpdateTimeSec) + "\n长按列表可删除");
        ViewBuilder.listView_setAdapter(sdlv, adapter);
        setContentBackgroundDrawable(ResUtils.skin_background);
        //invoke_virtual(sdlv,"setOnScrollGroupFloatingListener",true,load("com/tencent/widget/AbsListView$OnScrollListener"));
        ExfriendListActivity.this.getWindow().getDecorView().setTag(this);
        return true;
    }

    public void reload() {
        ConcurrentHashMap<Integer, EventRecord> eventsMap = exm.getEvents();
        if (evs == null) evs = new ArrayList<>();
        else evs.clear();
        if (eventsMap == null) return;
        Iterator<Map.Entry<Integer, EventRecord>> it = eventsMap.entrySet().iterator();
        EventRecord ev;
        while (it.hasNext()) {
            ev = it.next().getValue();
            evs.add(ev);
        }
        Collections.sort(evs);
        //log("ev size="+evs.size());
		/*try{
		 theme=ResUtils.getCurrentTheme((Activity)ctx);
		 }catch(Throwable e){
		 theme=ResUtils.getDefaultTheme();
		 log(e);
		 }*/
    }

    public int getCount() {
        return evs.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        EventRecord ev = evs.get(position);
        if (convertView == null) {
			/*TextView tv=new TextView(ctx);
			 //tv.setText("李王凯(1084515740)");
			 tv.setText("这是第"+position+"个");
			 tv.setPadding(32,32,32,32);
			 tv.setGravity(Gravity.CENTER_VERTICAL);
			 tv.setBackground(ResUtils.getListItemBackground());
			 //tv.setBackgroundResource(0x7f020435);
			 //Utils.log("Decoded:"+Integer.toHexString(theme.skin_text_black.getDefaultColor()));
			 //tv.setBackgroundTintList(theme.qq_setting_item_bg_nor);
			 tv.setTextColor((position%2==1)?ResUtils.skin_black:ResUtils.skin_gray3);
			 */
            convertView = inflateItemView(ev);//tv;
        }
        //log(position+"/"+getCount());
        convertView.setTag(ev);
        TextView title = convertView.findViewById(R_ID_EXL_TITLE);
        title.setText(ev.getShowStr());
        boolean isfri = false;

        TextView stat = convertView.findViewById(R_ID_EXL_STATUS);
        try {
            if (exm.getPersons().get(ev.operand).friendStatus == FriendRecord.STATUS_FRIEND_MUTUAL)
                isfri = true;
        } catch (Exception e) {
        }

        if (isfri) {
            stat.setTextColor(ResUtils.cloneColor(ResUtils.skin_gray3));
            stat.setText("已恢复");
        } else {
            stat.setTextColor(ResUtils.cloneColor(ResUtils.skin_red));
            stat.setText("已删除");
        }
        TextView subtitle = convertView.findViewById(R_ID_EXL_SUBTITLE);
        subtitle.setText(Utils.getIntervalDspMs(ev.timeRangeBegin * 1000, ev.timeRangeEnd * 1000));
        ImageView imgview = convertView.findViewById(R_ID_EXL_FACE);
        Bitmap bm = face.getBitmapFromCache(FaceImpl.TYPE_USER, "" + ev.operand);
        if (bm == null) {
            imgview.setImageDrawable(ResUtils.loadDrawableFromAsset("face.png", ExfriendListActivity.this));
            face.registerView(FaceImpl.TYPE_USER, "" + ev.operand, imgview);
        } else {
            imgview.setImageBitmap(bm);
        }

        return convertView;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private View inflateItemView(EventRecord ev) {
        int tmp;
        RelativeLayout rlayout = new RelativeLayout(ExfriendListActivity.this);
        LinearLayout llayout = new LinearLayout(ExfriendListActivity.this);
        llayout.setGravity(Gravity.CENTER_VERTICAL);
        llayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout textlayout = new LinearLayout(ExfriendListActivity.this);
        textlayout.setOrientation(LinearLayout.VERTICAL);
        rlayout.setBackground(ResUtils.getListItemBackground());

        LinearLayout.LayoutParams imglp = new LinearLayout.LayoutParams(Utils.dip2px(ExfriendListActivity.this, 50), Utils.dip2px(ExfriendListActivity.this, 50));
        imglp.setMargins(tmp = Utils.dip2px(ExfriendListActivity.this, 6), tmp, tmp, tmp);
        ImageView imgview = new ImageView(ExfriendListActivity.this);
        imgview.setFocusable(false);
        imgview.setClickable(false);
        imgview.setId(R_ID_EXL_FACE);


        imgview.setScaleType(ImageView.ScaleType.FIT_XY);
        llayout.addView(imgview, imglp);
        LinearLayout.LayoutParams ltxtlp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        LinearLayout.LayoutParams textlp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        ltxtlp.setMargins(tmp = Utils.dip2px(ExfriendListActivity.this, 2), tmp, tmp, tmp);
        textlp.setMargins(tmp = Utils.dip2px(ExfriendListActivity.this, 1), tmp, tmp, tmp);
        llayout.addView(textlayout, ltxtlp);


        TextView title = new TextView(ExfriendListActivity.this);
        title.setId(R_ID_EXL_TITLE);
        title.setSingleLine();
        //title.setText(ev.getShowStr());
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTextColor(ResUtils.cloneColor(ResUtils.skin_black));
        title.setTextSize(Utils.px2sp(ExfriendListActivity.this, Utils.dip2px(ExfriendListActivity.this, 16)));
        //title.setPadding(tmp=Utils.dip2px(ctx,8),tmp,0,tmp);

        TextView subtitle = new TextView(ExfriendListActivity.this);
        subtitle.setId(R_ID_EXL_SUBTITLE);
        subtitle.setSingleLine();
        subtitle.setGravity(Gravity.CENTER_VERTICAL);
        subtitle.setTextColor(ResUtils.cloneColor(ResUtils.skin_gray3));
        subtitle.setTextSize(Utils.px2sp(ExfriendListActivity.this, Utils.dip2px(ExfriendListActivity.this, 14)));
        //subtitle.setPadding(tmp,0,0,tmp);

        textlayout.addView(title, textlp);
        textlayout.addView(subtitle, textlp);

        RelativeLayout.LayoutParams statlp = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

        TextView stat = new TextView(ExfriendListActivity.this);
        stat.setId(R_ID_EXL_STATUS);
        stat.setSingleLine();
        stat.setGravity(Gravity.CENTER);
        stat.setTextSize(Utils.px2sp(ExfriendListActivity.this, Utils.dip2px(ExfriendListActivity.this, 16)));
        statlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        statlp.addRule(RelativeLayout.CENTER_VERTICAL);
        statlp.rightMargin = Utils.dip2px(ExfriendListActivity.this, 16);


        rlayout.addView(llayout);
        rlayout.addView(stat, statlp);


        rlayout.setClickable(true);
        rlayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                long uin = ((EventRecord) v.getTag()).operand;
                MainHook.openProfileCard(v.getContext(), uin);
            }
        });
        rlayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                try {
                    CustomDialog dialog = CustomDialog.create(ExfriendListActivity.this);
                    dialog.setTitle("删除记录");
                    dialog.setMessage("确认删除历史记录(" + ((EventRecord) v.getTag())._remark + ")");
                    dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            exm.getEvents().values().remove(v.getTag());
                            exm.saveConfigure();
                            reload();
                            adapter.notifyDataSetChanged();
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } catch (Exception e) {
                    log(e);
                }
                return true;
            }
        });
        return rlayout;
    }

}
