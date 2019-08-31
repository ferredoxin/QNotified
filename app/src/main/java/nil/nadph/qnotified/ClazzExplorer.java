package nil.nadph.qnotified;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.PixelFormat;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

import static android.view.WindowManager.LayoutParams.*;
import static android.widget.LinearLayout.*;
import static nil.nadph.qnotified.Utils.log;

public class ClazzExplorer {
    public Application app;
    public WindowManager winMgr;
    public Activity act;
    public LinearLayout _view;
    public WindowManager.LayoutParams _wlp;
    public Object rootEle;
    public Object currEle;
    public Stack track;
    public ArrayList<TextView> tvs;
    public static final int magic = 65530;
    public TextView tvclazz, tvupper;
    public ScrollView scrv;
    public LinearLayout lists;
    public LinearLayout.LayoutParams lpmm;
    public LinearLayout.LayoutParams lpmw;

    public int endx, endy;

    public boolean smallMode = false;

    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static ClazzExplorer self;

    private ClazzExplorer() {
        track = new Stack<>();
        tvs = new ArrayList<>();
    }

    public static ClazzExplorer get() {
        if (self == null)
            self = new ClazzExplorer();
        return self;
    }

    public void postRemoveView() {
        if (act != null && winMgr != null)
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //Lcom/tencent/mobileqq/activity/contact/newfriend/NewFriendActivity
                        winMgr.removeView(_view);
                    } catch (Exception e) {
                    }
                }
            });
    }


    public void init(Activity curract) {
        if (curract == null) return;
        postRemoveView();
        act = curract;

        winMgr = act.getWindowManager();
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (app == null) {
                        app = act.getApplication();
                        _view = new LinearLayout(app);
                        _view.setBackgroundColor(0xD0000000);
                        _wlp = new WindowManager.LayoutParams(1000, 1500, TYPE_APPLICATION, FLAG_NOT_FOCUSABLE, PixelFormat.RGBA_8888);
                        _view.setOrientation(VERTICAL);
                        _wlp.windowAnimations = 0;
                        lpmm = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
                        lpmw = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                        View vdiv = new View(app);
                        vdiv.setBackgroundColor(0xD01010FF);
                        vdiv.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, 2));
                        tvclazz = new TextView(app);
                        tvclazz.setLayoutParams(lpmw);
                        tvclazz.setId(magic - 2);
                        tvclazz.setClickable(true);
                        tvclazz.setOnLongClickListener(new OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View p1) {
                                if (smallMode) init(getCurrentActivity());
                                else {
                                    smallMode = true;
                                    _wlp.width = 100;
                                    _wlp.height = 100;
                                    _wlp.x = endx = 300;
                                    _wlp.y = endy;
                                    _wlp.gravity = Gravity.TOP | Gravity.LEFT;
                                    winMgr.updateViewLayout(_view, _wlp);
                                }
                                return true;
                            }
                        });
                        tvclazz.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View p1) {
                                if (!smallMode) {

                                    rootEle = null;
                                    init(getCurrentActivity());
                                    refresh();
                                } else {
                                    smallMode = false;
                                    _wlp.x = 0;
                                    _wlp.y = 0;
                                    _wlp.width = 1000;
                                    _wlp.height = 1500;
                                    _wlp.gravity = Gravity.CENTER;
                                    winMgr.updateViewLayout(_view, _wlp);
                                }

                            }
                        });
                        tvupper = new TextView(app);
                        tvupper.setLayoutParams(lpmw);
                        tvupper.setId(magic - 1);
                        tvupper.setOnClickListener(tv_onclick);
                        scrv = new ScrollView(app);
                        scrv.setLayoutParams(lpmm);
                        lists = new LinearLayout(app);
                        lists.setOrientation(VERTICAL);
                        lists.setLayoutParams(lpmw);
                        scrv.addView(lists);
                        _view.addView(tvclazz);
                        _view.addView(vdiv);
                        _view.addView(scrv);
                    }
                    winMgr.addView(_view, _wlp);
                    refresh();
                } catch (Throwable e) {
                    log(e);
                }
            }
        });
    }

    public TextView newTv() {
        TextView tv = new TextView(app);
        tv.setLayoutParams(lpmw);
        tv.setClickable(true);
        tv.setId(magic + tvs.size());
        tv.setOnClickListener(tv_onclick);
        tv.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((android.text.ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE)).setText(((TextView) v).getText());
                Toast.makeText(v.getContext(), "已复制内容", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        tv.setPadding(1, 0, 1, 0);
        tvs.add(tv);
        if (tvs.size() % 2 == 1) tv.setBackgroundColor(0x10000000);
        else tv.setBackgroundColor(0x10FFFFFF);
        return tv;
    }

    public OnClickListener tv_onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId() - magic;
            if (id < 0) {
                track.pop();
            } else {
                track.push(v.getTag());
            }
            refresh();
        }
    };

    public void refresh() {
        try {
            String tmp = "$";
            String fd;
            int num = 0;
            TextView tv;
            Object t_tmp;
            Object tmpobj = rootEle;
            int i, s = track.size();
            lists.removeAllViews();
            Object step;
            if (!track.isEmpty()) {
                if (s == 1) {
                    tvupper.setText(".. = " + rootEle);
                    lists.addView(tvupper);
                }
                for (i = 0; i < s; i++) {
                    step = track.get(i);
                    if ((step instanceof String) && ((String) step).charAt(0) == '[') {
                        tmp += step;
                        tmpobj = Array.get(tmpobj, Integer.parseInt(((String) step).replace("[", "").replace("]", "")));
                    } else if (step instanceof Method) {
                        ((Method) step).setAccessible(true);
                        if (((Method) step).getParameterTypes().length == 0) {
                            tmpobj = ((Method) step).invoke(tmpobj, EMPTY_OBJECT_ARRAY);
                            tmp += ".{" + ((Method) step).getName() + "()-" + ((Method) step).getReturnType().getName() + "}";
                        } else {
                            track.pop();
                            refresh();
                            return;
                        }
                    } else {
                        tmp += ".{" + ((Field) step).getName() + "-" + ((Field) step).getType() + "}";
                        ((Field) step).setAccessible(true);
                        tmpobj = ((Field) step).get(tmpobj);
                    }
                    if (i == s - 2) {
                        tvupper.setText(" .. = " + tmpobj);
                        lists.addView(tvupper);
                    }
                }
            }
            currEle = tmpobj;
            if (currEle == null) {
                tvclazz.setText(tmp + "\nnull");
                return;
            }
            tvclazz.setText(tmp + "\n" + currEle.getClass().getName());
            boolean first = true;
            Class clazz = currEle.getClass();
            if (clazz.isArray()) {
                int len = Array.getLength(currEle);
                for (i = 0; i < len; i++) {
                    if (num++ < tvs.size()) {
                        tv = tvs.get(num - 1);
                    } else tv = newTv();
                    tv.setTag("[" + i + "]");
                    tv.setText("[" + i + "] = " + nomorethan100(Array.get(currEle, i)));
                    lists.addView(tv);
                }
            } else do {
                Field[] fs = clazz.getDeclaredFields();
                Method[] ms = clazz.getDeclaredMethods();
                Class[] args;
                int ii;
                for (i = 0; i < fs.length; i++) {
                    fd = "--";
                    fd += Modifier.isStatic(fs[i].getModifiers()) ? "S" : "";
                    fd += Modifier.isPublic(fs[i].getModifiers()) ? "P" : "";
                    fd += Modifier.isNative(fs[i].getModifiers()) ? "N" : "";
                    fd += Modifier.isAbstract(fs[i].getModifiers()) ? "A" : "";
                    fd += Modifier.isInterface(fs[i].getModifiers()) ? "I" : "";
                    fd += "- " + fs[i].getType().getName();
                    fd = Html.escapeHtml(fd);
                    try {
                        fs[i].setAccessible(true);
                        t_tmp = fs[i].get(tmpobj);
                    } catch (Exception e) {
                        t_tmp = e;
                    }
                    if (first)
                        tmp = "<small><font color='#A0A0FF'>" + fd + "</small></font><br/><b> " + Html.escapeHtml(fs[i].getName()) + " = " + Html.escapeHtml(t_tmp == null ? "null" : nomorethan100(t_tmp)) + "</b>";
                    else
                        tmp = "<small><font color='#A0A0FF'>" + fd + "</small></font><br/> " + Html.escapeHtml(fs[i].getName()) + " = " + Html.escapeHtml(t_tmp == null ? "null" : nomorethan100(t_tmp));
                    if (num++ < tvs.size()) {
                        tv = tvs.get(num - 1);
                    } else tv = newTv();
                    tv.setTag(fs[i]);
                    tv.setText(Html.fromHtml(tmp));
                    lists.addView(tv);
                }
                for (i = 0; i < ms.length; i++) {
                    fd = "--";
                    fd += Modifier.isStatic(ms[i].getModifiers()) ? "S" : "";
                    fd += Modifier.isPublic(ms[i].getModifiers()) ? "P" : "";
                    fd += Modifier.isNative(ms[i].getModifiers()) ? "N" : "";
                    fd += Modifier.isAbstract(ms[i].getModifiers()) ? "A" : "";
                    fd += Modifier.isInterface(ms[i].getModifiers()) ? "I" : "";
                    fd += "- " + ms[i].getReturnType().getName();
                    fd = Html.escapeHtml(fd);
                    try {
                        ms[i].setAccessible(true);
                        args = ms[i].getParameterTypes();
                        tmp = "";
                        for (ii = 0; ii < args.length; ii++) {
                            tmp += args[ii].getName() + ",";
                        }
                        if (tmp.length() > 1) tmp = tmp.substring(0, tmp.length() - 1);
                    } catch (Exception e) {
                        t_tmp = e;
                    }
                    if (first)
                        tmp = "<small><font color='#FFA0A0'>" + fd + "</small></font><br/><b> " + Html.escapeHtml(ms[i].getName()) + "(" + Html.escapeHtml(tmp) + ")</b>";
                    else
                        tmp = "<small><font color='#FFA0A0'>" + fd + "</small></font><br/> " + Html.escapeHtml(ms[i].getName()) + "(" + Html.escapeHtml(tmp) + ")";
                    if (num++ < tvs.size()) {
                        tv = tvs.get(num - 1);
                    } else tv = newTv();
                    tv.setTag(ms[i]);
                    tv.setText(Html.fromHtml(tmp));
                    lists.addView(tv);
                }
                first = false;
                if (clazz == Object.class) break;
                clazz = clazz.getSuperclass();
            } while (clazz != null);
        } catch (Throwable e) {
        }
    }

	/*public static Object getTheField(Object obj,String name){
	 Class clazz=obj.getClass();
	 Field f=null;
	 do{
	 try{
	 f=clazz.getDeclaredField(name);
	 if(f!=null)break;
	 }catch(NoSuchFieldException e){
	 f=null;
	 }
	 if(clazz==Object.class)break;
	 clazz=clazz.getSuperclass();
	 }while(clazz!=null);
	 if(f==null)return null;
	 f.setAccessible(true);
	 try{
	 return f.get(obj);
	 }catch(IllegalAccessException e){}catch(IllegalArgumentException e){}
	 return null;
	 }*/

    public static String en_toStr(Object obj) {
        if (obj == null) return null;
        String str;
        if (obj instanceof CharSequence) str = Utils.en(obj.toString());
        else str = "" + obj;
        return str;
    }

    public static String nomorethan100(Object obj) {
        if (obj == null) return null;
        String str;
        if (obj instanceof CharSequence) str = "\"" + obj + "\"";
        else str = "" + obj;
        if (str.length() > 110) return str.substring(0, 100);
        return str;
    }

    public static Activity getCurrentActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities = (Map) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
