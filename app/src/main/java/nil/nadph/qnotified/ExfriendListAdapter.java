package nil.nadph.qnotified;
import android.widget.*;
import android.view.*;
import android.content.*;
import android.app.*;
import de.robv.android.xposed.*;
import android.view.View.*;
import android.content.res.*;
import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import android.graphics.*;
import java.util.*;
import java.lang.reflect.*;

public class ExfriendListAdapter extends BaseAdapter implements InvocationHandler{

	@Override
	public Object invoke(Object p1,Method p2,Object[] p3) throws Throwable{
		// TODO: Implement this method
		return null;
	}


	private Context ctx;
	private View mListView;
	private FaceImpl face;
	private ExfriendManager exm;
	private HashMap <Integer,EventRecord> eventsMap;
	private ArrayList<EventRecord> evs;
	
	private static final int R_ID_TITLE=0x300AFF01;
	private static final int R_ID_SUBTITLE=0x300AFF02;
	private static final int R_ID_FACE=0x300AFF03;
	private static final int R_ID_STATUS=0x300AFF04;
	
	
	
	public ExfriendListAdapter(View listView,ExfriendManager m){
		mListView=listView;
		exm=m;
		ctx=mListView.getContext();
		try{
			face=FaceImpl.getInstance();
		}catch(Throwable e){
			XposedBridge.log(e);
		}
		eventsMap=exm.getEvents();
		evs=new ArrayList();
		Iterator<Map.Entry<Integer,EventRecord>> it=eventsMap.entrySet().iterator();
		EventRecord ev;
		while(it.hasNext()){
			ev=(EventRecord)it.next().getValue();
			evs.add(ev);
		}
		Collections.sort(evs);
		/*try{
			theme=QThemeKit.getCurrentTheme((Activity)ctx);
		}catch(Throwable e){
			theme=QThemeKit.getDefaultTheme();
			XposedBridge.log(e);
		}*/
	}
	
	@Override
	public int getCount(){
		return evs.size();
	}

	@Override
	public Object getItem(int position){
		// TODO: Implement this method
		return null;
	}

	@Override
	public long getItemId(int position){
		// TODO: Implement this method
		return 0;
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent){
		if(convertView==null){
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
			convertView=inflateItemView((EventRecord)evs.get(position));//tv;
		}
		return convertView;
	}
	
	
	private View inflateItemView(EventRecord ev){
		int tmp;
		RelativeLayout rlayout=new RelativeLayout(ctx);
		LinearLayout llayout=new LinearLayout(ctx);
		llayout.setGravity(Gravity.CENTER_VERTICAL);
		llayout.setOrientation(LinearLayout.HORIZONTAL);
		
		LinearLayout textlayout=new LinearLayout(ctx);
		textlayout.setOrientation(LinearLayout.VERTICAL);
		rlayout.setBackground(QThemeKit.getListItemBackground());
		
		LinearLayout.LayoutParams imglp=new LinearLayout.LayoutParams(Utils.dip2px(ctx,50),Utils.dip2px(ctx,50));
		imglp.setMargins(tmp=Utils.dip2px(ctx,6),tmp,tmp,tmp);
		ImageView imgview=new ImageView(ctx);
		imgview.setFocusable(false);
		imgview.setClickable(false);
		imgview.setId(R_ID_FACE);
		Bitmap bm=face.getBitmapFromCache(FaceImpl.TYPE_USER,""+ev.operator);
		if(bm==null){
			imgview.setImageDrawable(QThemeKit.loadDrawableFromAsset("face.png"));
			face.registerView(FaceImpl.TYPE_USER,""+ev.operator,imgview);
		}else{
			imgview.setImageBitmap(bm);
		}
		
		imgview.setScaleType(ImageView.ScaleType.FIT_XY);
		llayout.addView(imgview,imglp);
		LinearLayout.LayoutParams ltxtlp=new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
		LinearLayout.LayoutParams textlp=new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
		ltxtlp.setMargins(tmp=Utils.dip2px(ctx,2),tmp,tmp,tmp);
		textlp.setMargins(tmp=Utils.dip2px(ctx,1),tmp,tmp,tmp);
		llayout.addView(textlayout,ltxtlp);
		
		
		
		TextView title=new TextView(ctx);
		title.setId(R_ID_TITLE);
		title.setSingleLine();
		title.setText(ev.getShowStr());
		title.setGravity(Gravity.CENTER_VERTICAL);
		title.setTextColor(new ColorStateList(QThemeKit.skin_black.getStates(),QThemeKit.skin_black.getColors()));
		title.setTextSize(Utils.px2sp(ctx,Utils.dip2px(ctx,16)));
		//title.setPadding(tmp=Utils.dip2px(ctx,8),tmp,0,tmp);
		
		TextView subtitle=new TextView(ctx);
		subtitle.setId(R_ID_SUBTITLE);
		subtitle.setSingleLine();
		subtitle.setText("["+ev.timeRangeBegin+","+ev.timeRangeEnd+"]");
		subtitle.setGravity(Gravity.CENTER_VERTICAL);
		subtitle.setTextColor(new ColorStateList(QThemeKit.skin_gray3.getStates(),QThemeKit.skin_gray3.getColors()));
		subtitle.setTextSize(Utils.px2sp(ctx,Utils.dip2px(ctx,14)));
		//subtitle.setPadding(tmp,0,0,tmp);
		
		textlayout.addView(title,textlp);
		textlayout.addView(subtitle,textlp);
		
		RelativeLayout.LayoutParams statlp=new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
		
		TextView stat=new TextView(ctx);
		stat.setId(R_ID_STATUS);
		stat.setSingleLine();
		stat.setGravity(Gravity.CENTER);
		stat.setTextSize(Utils.px2sp(ctx,Utils.dip2px(ctx,16)));
		statlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		statlp.addRule(RelativeLayout.CENTER_VERTICAL);
		statlp.rightMargin=Utils.dip2px(ctx,16);
		
		boolean isfri=false;
		try{
			if(exm.getPersons().get(ev.operator).friendStatus==FriendRecord.STATUS_FRIEND_MUTUAL)
				isfri=true;
		}catch(Exception e){}
		
		if(isfri){
			stat.setTextColor(new ColorStateList(QThemeKit.skin_red.getStates(),QThemeKit.skin_gray3.getColors()));
			stat.setText("已恢复");
		}else{
			stat.setTextColor(new ColorStateList(QThemeKit.skin_red.getStates(),QThemeKit.skin_red.getColors()));
			stat.setText("已删除");
		}
		rlayout.addView(llayout);
		rlayout.addView(stat,statlp);
		
		return rlayout;
	}

}
