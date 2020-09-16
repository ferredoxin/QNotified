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
package nil.nadph.qnotified.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.tencent.mobileqq.app.QQAppInterface;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;

import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.activity.TroopSelectActivity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.util.Utils.*;

public class SendBatchMsg {

    public static final int R_ID_SELECT_FRIEND = 0x300AFF51;
    public static final int R_ID_SELECT_GROUP = 0x300AFF52;

    private static LinearLayout getEditView(Context context) {
        int padding = dip2px(context, 20.0f);
        //去除editView焦点
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setFocusable(true);
        linearLayout.setFocusableInTouchMode(true);
        EditText editText = new EditText(context);
        editText.setTextColor(Color.BLACK);
        editText.setSingleLine(false);
        editText.setMinLines(4);
        editText.setGravity(Gravity.TOP);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        layoutParams.setMargins(padding, dip2px(context, 10.0f), padding, 10);
        editText.setLayoutParams(layoutParams);
        linearLayout.addView(editText);
        return linearLayout;
    }

    private static void setEditDialogStyle(AlertDialog alertDialog) {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(16.0f);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(16.0f);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xff4284f3);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xff4284f3);
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(alertDialog);
            //通过反射修改title字体大小和颜色
            Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
            mTitle.setAccessible(true);
            TextView mTitleView = (TextView) mTitle.get(mAlertController);
            mTitleView.setTextSize(20.0f);
        } catch (Exception e) {
            log(e);
        }
    }

    @SuppressWarnings("deprecation")
    public static View.OnClickListener clickToBatchMsg() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LicenseStatus.sDisableCommonHooks) return;
                try {
                    final Context exactCtx = v.getContext();
                    LinearLayout linearLayout = getEditView(exactCtx);
                    final EditText editText = (EditText) linearLayout.getChildAt(0);
                    final AlertDialog alertDialog = new AlertDialog.Builder(exactCtx, Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 ? android.R.style.Theme_DeviceDefault_Light_Dialog_Alert : AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                            .setTitle("输入群发文本")
                            .setView(linearLayout)
                            .setPositiveButton("选择群发对象", null)
                            .setNegativeButton("取消", null)
                            .create();
                    alertDialog.show();
                    setEditDialogStyle(alertDialog);
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String msg = editText.getText().toString();
                            if (msg.isEmpty() || msg.equals("")) {
                                showToast(exactCtx, TOAST_TYPE_ERROR, "请输入文本消息", Toast.LENGTH_SHORT);
                            } else {
                                if (msg.length() > 6 && !LicenseStatus.isAsserted()) {
                                    showToast(exactCtx, TOAST_TYPE_ERROR, "超出字数限制：输入被限制在五个字以内", Toast.LENGTH_SHORT);
                                } else {
                                    try {
                                        showSelectDialog(exactCtx, msg);
                                    } catch (Throwable e) {
                                        log(e);
                                    }
                                }
                            }
                        }
                    });
                } catch (Throwable e) {
                    log(e);
                }
            }
        };
    }


    @SuppressWarnings("deprecation")
    private static void showSelectDialog(final Context context, final String msg) throws Throwable {
        final TroopAndFriendSelectAdpter troopAndFriendSelectAdpter = new TroopAndFriendSelectAdpter(context);
        final AlertDialog alertDialog = new AlertDialog.Builder(context, Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 ? android.R.style.Theme_DeviceDefault_Light_Dialog_Alert : AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("发送到")
                .setView(getListView(context, msg, troopAndFriendSelectAdpter))
                .setPositiveButton("发送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashSet<ContactDescriptor> arrayList = troopAndFriendSelectAdpter.mTargets;
                        if (!arrayList.isEmpty()) {
                            int size = arrayList.size();
                            int[] type = new int[size];
                            long[] uins = new long[size];
                            int i = 0;
                            for (ContactDescriptor target : arrayList) {
                                if (i < size) {
                                    type[i] = target.uinType;
                                    uins[i] = Long.parseLong(target.uin);
                                }
                                i++;
                            }
                            boolean isSuccess = ntSendBatchMessages(getQQAppInterface(), context, msg, type, uins);
                            CliOper.batchSendMsg(Utils.getLongAccountUin(), msg, arrayList.size());
                            try {
                                showToast(context, TOAST_TYPE_INFO, "发送" + (isSuccess ? "成功" : "失败"), Toast.LENGTH_SHORT);
                            } catch (Throwable throwable) {
                                Toast.makeText(context, "发送" + (isSuccess ? "成功" : "失败"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .setNeutralButton("全选", null)
                .create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(16.0f);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(16.0f);
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextSize(16.0f);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xff4284f3);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xff4284f3);
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xff4284f3);
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                troopAndFriendSelectAdpter.setAllSelect();
            }
        });
        troopAndFriendSelectAdpter.sendBtn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                troopAndFriendSelectAdpter.notifyDataSetChanged();
            }
        }, 1000);
    }

    static native boolean ntSendBatchMessages(QQAppInterface rt, Context ctx, String msg, int[] type, long[] uin);

    private static View getListView(Context context, String sendMsg,
                                    final TroopAndFriendSelectAdpter troopAndFriendSelectAdpter) {
        final EditText editText = new EditText(context);
        editText.setBackgroundColor(0x00000000);
        editText.setHint("搜索");
        editText.setTextSize(18.0f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, dip2px(context, 30.0f));
        layoutParams.setMargins(dip2px(context, 30.0f), 0, dip2px(context, 30.0f), 10);
        editText.setLayoutParams(layoutParams);
        final ListView listView = new ListView(context);
        listView.setAdapter(troopAndFriendSelectAdpter);
        listView.setDivider(new ColorDrawable(0x00000000));
        listView.setSelector(new ColorDrawable(0x00000000));
        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        radioGroup.setGravity(Gravity.CENTER);
        RadioButton friend = new RadioButton(context);
        friend.setChecked(true);
        friend.setText("好友");
        friend.setTextColor(Color.BLACK);
        friend.setId(R_ID_SELECT_FRIEND);
        RadioButton group = new RadioButton(context);
        group.setText("群聊");
        group.setTextColor(Color.BLACK);
        group.setId(R_ID_SELECT_GROUP);
        radioGroup.addView(friend);
        radioGroup.addView(group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R_ID_SELECT_FRIEND) {
                    ((RadioButton) group.getChildAt(0)).setChecked(true);
                    ((RadioButton) group.getChildAt(1)).setChecked(false);
                    troopAndFriendSelectAdpter.toggleFriends();
                } else if (checkedId == R_ID_SELECT_GROUP) {
                    ((RadioButton) group.getChildAt(0)).setChecked(false);
                    ((RadioButton) group.getChildAt(1)).setChecked(true);
                    troopAndFriendSelectAdpter.toggleGroups();
                }
                editText.setText("");
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                troopAndFriendSelectAdpter.setData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TroopAndFriendSelectAdpter.ViewHolder viewHolder = (TroopAndFriendSelectAdpter.ViewHolder) view.getTag();
                viewHolder.cBox.toggle();
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(radioGroup);
        linearLayout.addView(editText);
        linearLayout.addView(listView);
        return linearLayout;
    }

    /**
     * Created by Deng on 2018/8/1.
     */

    public static class TroopAndFriendSelectAdpter extends BaseAdapter {
        public Button sendBtn = null;
        private ArrayList<ContactDescriptor> mFriends;
        private ArrayList<ContactDescriptor> mGroups;
        private final ArrayList<ContactDescriptor> mHits = new ArrayList<>();
        final HashSet<ContactDescriptor> mTargets = new HashSet<>();
        private boolean showFriends = true, showGtoups = false;
        private String searchMsg = "";
        private final Context context;
        private final FaceImpl face = FaceImpl.getInstance();

        public TroopAndFriendSelectAdpter(Context context) throws Throwable {
            this.context = context;
            init();
        }

        private void init() throws Exception {
            mFriends = ExfriendManager.getCurrent().getFriendsRemark();
            ArrayList tx = TroopSelectActivity.getTroopInfoListRaw();
            mGroups = new ArrayList<>();
            for (Object info : tx) {
                ContactDescriptor cd = new ContactDescriptor();
                cd.nick = (String) iget_object_or_null(info, "troopname");
                cd.uin = (String) iget_object_or_null(info, "troopuin");
                cd.uinType = 1;
                mGroups.add(cd);
            }
            mHits.addAll(mFriends);
        }

        @Override
        public int getCount() {
            return mHits.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LinearLayout linearLayout = getListItem(context);
                convertView = linearLayout;
                viewHolder.cBox = (CheckBox) linearLayout.getChildAt(0);
                viewHolder.img = (ImageView) linearLayout.getChildAt(1);
                viewHolder.title = (TextView) linearLayout.getChildAt(2);
                convertView.setTag(viewHolder);
                viewHolder.cBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        ContactDescriptor cd = ((ViewHolder) ((View) buttonView.getParent()).getTag()).mUin;
                        if (isChecked) mTargets.add(cd);
                        else mTargets.remove(cd);
                        if (sendBtn != null) {
                            int size = mTargets.size();
                            if (size != 0) sendBtn.setText("发送(" + size + ")");
                            else sendBtn.setText("发送");
                        }
                    }
                });
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ContactDescriptor cd = mHits.get(position);
            viewHolder.mUin = cd;
            viewHolder.title.setText(cd.nick);
            face.setImageOrRegister(mHits.get(position), viewHolder.img);
            viewHolder.cBox.setChecked(mTargets.contains(mHits.get(position)));
            return convertView;
        }

        private LinearLayout getListItem(Context context) {
            int padding = dip2px(context, 20.0f);
            int imgPadding = dip2px(context, 10.0f);
            int imgHeight = dip2px(context, 40.0f);
            LinearLayout linearLayout = new LinearLayout(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setPadding(padding, 15, padding, 25);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER_VERTICAL;
            CheckBox check = new CheckBox(context);
            check.setFocusable(false);
            check.setClickable(false);
            ImageView imageView = new ImageView(context);
            @SuppressWarnings("SuspiciousNameCombination") LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(imgHeight, imgHeight);
            layoutParams2.gravity = Gravity.CENTER_VERTICAL;
            layoutParams2.setMargins(imgPadding, 0, imgPadding, 0);
            imageView.setLayoutParams(layoutParams2);
            TextView textView = new TextView(context);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(18.0f);
            linearLayout.addView(check, layoutParams1);
            linearLayout.addView(imageView);
            linearLayout.addView(textView, layoutParams1);
            return linearLayout;
        }

        public void setData(String searchMsg) {
            this.searchMsg = searchMsg;
            mHits.clear();
            if (searchMsg.equals("") || searchMsg.isEmpty()) {
                if (showGtoups) mHits.addAll(mGroups);
                if (showFriends) mHits.addAll(mFriends);
            } else {
                if (showFriends) {
                    for (ContactDescriptor cd : mFriends) {
                        if (cd.nick.contains(searchMsg) || cd.uin.contains(searchMsg))
                            mHits.add(cd);
                    }
                }
                if (showGtoups) {
                    for (ContactDescriptor cd : mGroups) {
                        if (cd.nick.contains(searchMsg) || cd.uin.contains(searchMsg))
                            mHits.add(cd);
                    }
                }
            }
            notifyDataSetChanged();
        }

        public void setAllSelect() {
            mTargets.addAll(mHits);
            notifyDataSetChanged();
        }

        public void toggleFriends() {
            showFriends = true;
            showGtoups = false;
            setData("");
            notifyDataSetChanged();
        }

        public void toggleGroups() {
            showGtoups = true;
            showFriends = false;
            setData("");
            notifyDataSetChanged();
        }

        public static class ViewHolder {
            public ImageView img;
            public TextView title;
            public CheckBox cBox;
            public ContactDescriptor mUin;
        }
    }
}
