package nil.nadph.qnotified.util;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static nil.nadph.qnotified.util.Utils.*;

public class SendBatchMsg {

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
                                } catch (Exception e) {
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

    private static View getView(Context context,String sendMsg) throws InvocationTargetException, IllegalAccessException {
        final EditText editText = new EditText(context);
        editText.setBackgroundColor(0x00000000);
        editText.setHint("搜索");
        editText.setTextSize(18.0f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(context, 30.0f));
        layoutParams.setMargins(dip2px(context, 30.0f), 0, dip2px(context, 30.0f), 10);
        editText.setLayoutParams(layoutParams);
        final ListView listView = new ListView(context);
        troopAndFriendSelectAdpter = new TroopAndFriendSelectAdpter(context);
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
        friend.setId(R.id.select_friend);
        RadioButton group = new RadioButton(context);
        group.setText("群聊");
        group.setTextColor(Color.BLACK);
        group.setId(R.id.select_group);
        radioGroup.addView(friend);
        radioGroup.addView(group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.select_friend) {
                    ((RadioButton) group.getChildAt(0)).setChecked(true);
                    ((RadioButton) group.getChildAt(1)).setChecked(false);
                    troopAndFriendSelectAdpter.setmFriendInfo();

                } else if (checkedId == R.id.select_group) {
                    ((RadioButton) group.getChildAt(1)).setChecked(true);
                    ((RadioButton) group.getChildAt(0)).setChecked(false);
                    troopAndFriendSelectAdpter.setmGroupInfo();
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

    public class TroopAndFriendSelectAdpter extends BaseAdapter {
        private ArrayList<ContactInfo> mFriendInfo=new ArrayList<>();
        private ArrayList<ContactInfo>mGroupInfo=new ArrayList<>();
        private ArrayList<ContactInfo>mCurrentInfo=new ArrayList<>();
        private Map<String,Boolean> mIsSelect=new HashMap<>();
        private int mCurrentNum=0;//0为好友，1为群聊
        private String searchMsg="";
        private Context context;

        public TroopAndFriendSelectAdpter(Context context) throws InvocationTargetException, IllegalAccessException {
            this.context=context;
            init();
        }

        private void init() throws InvocationTargetException, IllegalAccessException {
            ArrayList mFriendName = QQHelper.getFriendNick();
            ArrayList mFriendUin = QQHelper.getFriendUin();
            ArrayList mFriendDrawable = QQHelper.getFriendDawable();
            ArrayList mTroopName = QQHelper.getTroopName();
            ArrayList mTroopUin = QQHelper.getTroopUin();
            ArrayList mTroopDrawable = QQHelper.getTroopDrawable();
            if (mFriendName!=null){
                for (int i=0;i<mFriendName.size();i++){
                    ContactInfo contactInfo=new ContactInfo();
                    contactInfo.setHead((Drawable) mFriendDrawable.get(i));
                    contactInfo.setUin((String) mFriendUin.get(i));
                    contactInfo.setName((String)mFriendName.get(i));
                    contactInfo.setIstroop(0);
                    mFriendInfo.add(contactInfo);
                    mIsSelect.put(contactInfo.getId(),false);
                }
            }
            if (mTroopName!=null){
                for (int i=0;i<mTroopName.size();i++){
                    ContactInfo contactInfo=new ContactInfo();
                    contactInfo.setHead((Drawable) mTroopDrawable.get(i));
                    contactInfo.setUin((String) mTroopUin.get(i));
                    contactInfo.setName((String)mTroopName.get(i));
                    contactInfo.setIstroop(1);
                    mGroupInfo.add(contactInfo);
                    mIsSelect.put(contactInfo.getId(),false);
                }
            }
            mCurrentInfo.addAll(mFriendInfo);
        }
        @Override
        public int getCount() {
            return mCurrentInfo.size();
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
            ViewHolder viewHolder=null;
            if (convertView==null){
                viewHolder=new ViewHolder();
                LinearLayout linearLayout=getListItem(context);
                convertView=linearLayout;
                viewHolder.cBox=(CheckBox) linearLayout.getChildAt(0);
                viewHolder.img=(ImageView)linearLayout.getChildAt(1);
                viewHolder.title=(TextView)linearLayout.getChildAt(2);
                convertView.setTag(viewHolder);
            }else {
                viewHolder=(ViewHolder)convertView.getTag();
            }
            viewHolder.title.setText(mCurrentInfo.get(position).getName());
            viewHolder.img.setBackground(mCurrentInfo.get(position).getHead());
            viewHolder.cBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mIsSelect.put(mCurrentInfo.get(position).getId(),isChecked);
                }
            });
            viewHolder.cBox.setChecked(mIsSelect.get(mCurrentInfo.get(position).getId()));
            return convertView;
        }

        private LinearLayout getListItem(Context context){
            int padding= dip2px(context,20.0f);
            int imgPadding= dip2px(context,10.0f);
            int imgHeight= dip2px(context,40.0f);
            LinearLayout linearLayout=new LinearLayout(context);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setPadding(padding,15,padding,25);
            LinearLayout.LayoutParams layoutParams1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.gravity= Gravity.CENTER_VERTICAL;
            CheckBox check=new CheckBox(context);
            check.setFocusable(false);
            check.setClickable(false);
            ImageView imageView=new ImageView(context);
            LinearLayout.LayoutParams layoutParams2=new LinearLayout.LayoutParams(imgHeight,imgHeight);
            layoutParams2.gravity=Gravity.CENTER_VERTICAL;
            layoutParams2.setMargins(imgPadding,0,imgPadding,0);
            imageView.setLayoutParams(layoutParams2);
            TextView textView=new TextView(context);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(18.0f);
            linearLayout.addView(check,layoutParams1);
            linearLayout.addView(imageView);
            linearLayout.addView(textView,layoutParams1);
            return linearLayout;

        }

        public void setmFriendInfo(){
            if (mCurrentNum==1){
                mCurrentNum=0;
                mCurrentInfo.clear();
                mCurrentInfo.addAll(mFriendInfo);
                notifyDataSetChanged();
            }

        }

        public void setmGroupInfo(){
            if (mCurrentNum==0){
                mCurrentNum=1;
                mCurrentInfo.clear();
                mCurrentInfo.addAll(mGroupInfo);
                notifyDataSetChanged();
            }
        }

        public void setData(String searchMsg){
            this.searchMsg=searchMsg;
            mCurrentInfo.clear();
            if (searchMsg.equals("")||searchMsg.isEmpty()){
                if (mCurrentNum==0){
                    mCurrentInfo.addAll(mFriendInfo);
                }else if (mCurrentNum==1){
                    mCurrentInfo.addAll(mGroupInfo);
                }
            }else {
                if (mCurrentNum==0){
                    for (int i=0;i<mFriendInfo.size();i++){
                        if (mFriendInfo.get(i).getName().contains(searchMsg)){
                            mCurrentInfo.add(mFriendInfo.get(i));
                        }
                    }
                }else if (mCurrentNum==1){
                    for (int i=0;i<mGroupInfo.size();i++){
                        if (mGroupInfo.get(i).getName().contains(searchMsg)){
                            mCurrentInfo.add(mGroupInfo.get(i));
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }

        public void setAllSelect(){
            for (int i=0;i<mCurrentInfo.size();i++){
                mIsSelect.put(mCurrentInfo.get(i).getId(),true);
            }
            if (mCurrentNum==0){
                if (searchMsg.equals("")){
                    for (int i=0;i<mFriendInfo.size();i++){
                        mIsSelect.put(mFriendInfo.get(i).getId(),true);
                    }
                }else {
                    for (int i=0;i<mFriendInfo.size();i++){
                        if (mFriendInfo.get(i).getName().contains(searchMsg)) {
                            mIsSelect.put(mFriendInfo.get(i).getId(),true);
                        }
                    }
                }
            }else if (mCurrentNum==1){
                if (searchMsg.equals("")){
                    for (int i=0;i<mGroupInfo.size();i++){
                        mIsSelect.put(mGroupInfo.get(i).getId(),true);
                    }
                }else {
                    for (int i=0;i<mGroupInfo.size();i++){
                        if (mGroupInfo.get(i).getName().contains(searchMsg)) {
                            mIsSelect.put(mGroupInfo.get(i).getId(),true);
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }

        public ArrayList getSelectInfo(){
            ArrayList arrayList=new ArrayList();
            for (int i=0;i<mFriendInfo.size();i++){
                if (mIsSelect.get(mFriendInfo.get(i).getId())){
                    arrayList.add(mFriendInfo.get(i));
                }
            }
            for (int i=0;i<mGroupInfo.size();i++){
                if (mIsSelect.get(mGroupInfo.get(i).getId())){
                    arrayList.add(mGroupInfo.get(i));
                }
            }
            return arrayList;
        }

        public class ViewHolder {
            public ImageView img;
            public TextView title;
            public CheckBox cBox;
        }
}
