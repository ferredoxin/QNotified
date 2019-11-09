package nil.nadph.qnotified.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.FaceImpl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;

import static nil.nadph.qnotified.util.Initiator._SessionInfo;
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
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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

    public static View.OnClickListener clickToBatchMsg() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final Context ctx = v.getContext();
                    LinearLayout linearLayout = getEditView(ctx);
                    final EditText editText = (EditText) linearLayout.getChildAt(0);
                    AlertDialog alertDialog = new AlertDialog.Builder(ctx, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
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
                                try {
                                    showToast(ctx, TOAST_TYPE_ERROR, "请输入文本消息", Toast.LENGTH_SHORT);
                                } catch (Throwable e) {
                                    log(e);
                                }
                            } else {
                                try {
                                    showSelectDialog(ctx, msg);
                                } catch (Throwable e) {
                                    log(e);
                                }
                            }
                        }
                    });
                } catch (Throwable e) {
                }
            }
        };
    }


    private static void showSelectDialog(final Context context, final String msg) throws Throwable {
        final TroopAndFriendSelectAdpter troopAndFriendSelectAdpter = new TroopAndFriendSelectAdpter(context);
        final AlertDialog alertDialog = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("发送到")
                .setView(getListView(context, msg, troopAndFriendSelectAdpter))
                .setPositiveButton("发送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList arrayList = troopAndFriendSelectAdpter.getSelectInfo();
                        if (!arrayList.isEmpty()) {
                            boolean isSuccess = true;
                            Class facade = DexKit.doFindClass(DexKit.C_FACADE);
                            Class SendMsgParams = null;
                            Method m = null;
                            for (Method mi : facade.getDeclaredMethods()) {
                                if (!mi.getReturnType().equals(long[].class)) continue;
                                Class[] argt = mi.getParameterTypes();
                                if (argt.length != 6) continue;
                                if (argt[1].equals(Context.class) && argt[2].equals(_SessionInfo())
                                        && argt[3].equals(String.class) && argt[4].equals(ArrayList.class)) {
                                    m = mi;
                                    m.setAccessible(true);
                                    SendMsgParams = argt[5];
                                    break;
                                }
                            }
                            for (int i = 0; i < arrayList.size(); i++) {
                                ContactDescriptor contactInfo = (ContactDescriptor) arrayList.get(i);
                                try {
                                    if (null == m.invoke(null, getQQAppInterface(), context, Utils.createSessionInfo(contactInfo.uin, contactInfo.uinType), msg, new ArrayList<>(), SendMsgParams.newInstance())) {
                                        isSuccess = false;
                                    }
                                } catch (Exception e) {
                                    isSuccess = false;
                                    log(e);
                                }
                            }
                            Toast.makeText(context, "发送" + (isSuccess ? "成功" : "失败"), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .setNeutralButton("全选", null)
                .create();
        //alertdialog延迟一毫秒显示，防止头像不显示
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
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
            }
        }, 100);
    }

    private static View getListView(Context context, String sendMsg, final TroopAndFriendSelectAdpter troopAndFriendSelectAdpter) throws InvocationTargetException, IllegalAccessException {
        final EditText editText = new EditText(context);
        editText.setBackgroundColor(0x00000000);
        editText.setHint("搜索");
        editText.setTextSize(18.0f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(context, 30.0f));
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
        group.setId(R_ID_SELECT_FRIEND);
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
                    ((RadioButton) group.getChildAt(1)).setChecked(true);
                    ((RadioButton) group.getChildAt(0)).setChecked(false);
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
        private ArrayList<ContactDescriptor> mAllContacts = new ArrayList<>();
        private ArrayList<ContactDescriptor> mHits = new ArrayList<>();
        private HashSet<ContactDescriptor> mTargets = new HashSet<>();
        private String searchMsg = "";
        private Context context;
        private FaceImpl face = FaceImpl.getInstance();

        public TroopAndFriendSelectAdpter(Context context) throws Throwable {
            this.context = context;
            init();
        }

        private void init() throws InvocationTargetException, IllegalAccessException {
            ArrayList<ContactDescriptor> friends = ExfriendManager.getCurrent().getFriends();
            ArrayList<ContactDescriptor> groups = null;
            mAllContacts.addAll(friends);
            mAllContacts.addAll(groups);
            mHits.addAll(mAllContacts);
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
                        if (isChecked) mTargets.add((ContactDescriptor) ((View) buttonView.getParent()).getTag(2));
                        else mTargets.remove((ContactDescriptor) ((View) buttonView.getParent()).getTag(2));
                    }
                });
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ContactDescriptor cd = mHits.get(position);
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
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setPadding(padding, 15, padding, 25);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER_VERTICAL;
            CheckBox check = new CheckBox(context);
            check.setFocusable(false);
            check.setClickable(false);
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(imgHeight, imgHeight);
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
            if (searchMsg.equals("") || searchMsg.isEmpty()) {
                mHits.clear();
                mHits.addAll(mAllContacts);
            } else {
                if (mCurrentNum == 0) {
                    for (int i = 0; i < mFriendInfo.size(); i++) {
                        if (mFriendInfo.get(i).nick.contains(searchMsg)) {
                            mCurrentInfo.add(mFriendInfo.get(i));
                        }
                    }
                } else if (mCurrentNum == 1) {
                    for (int i = 0; i < mGroupInfo.size(); i++) {
                        if (mGroupInfo.get(i).nick.contains(searchMsg)) {
                            mCurrentInfo.add(mGroupInfo.get(i));
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }

        public void setAllSelect() {
            mTargets.addAll(mHits);
            notifyDataSetChanged();
        }

        public class ViewHolder {
            public ImageView img;
            public TextView title;
            public CheckBox cBox;
        }
    }
}
