package nil.nadph.qnotified;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import nil.nadph.qnotified.pk.FriendChunk;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static nil.nadph.qnotified.Initiator.load;
import static nil.nadph.qnotified.Table.*;
import static nil.nadph.qnotified.Utils.*;
import android.annotation.*;

public class ExfriendManager{
	static private final int ID_EX_NOTIFY=65537;

	static private final int FL_UPDATE_INT_MIN=10*60;//sec
	static private final int FL_UPDATE_INT_MAX=1*60*60;//sec

	static private HashMap<Long,ExfriendManager> instances=new HashMap<>();
	static private ExecutorService tp;
	private long mUin;
	private int mTotalFriendCount;
	private HashMap<Long,FriendRecord> persons;
	private HashMap<Integer,EventRecord> events;

	public long lastUpdateTimeSec;

	private HashMap<String,Object> fileData;//Back compatibility

	private ConcurrentHashMap mStdRemarks;
	private ArrayList<FriendChunk> cachedFriendChunks=new ArrayList<>();


	private boolean dirtyFlag;

	public static final int _VERSION_CURRENT=1;


	public long getUin(){
		return mUin;
	}

	private void dbg(){
		//log(Utils.getLineNo(2)+"=>"+(persons!=null?(persons.get(3211711411l)+""):"<null>"));
	}

	private static Runnable asyncUpdateAwaitingTask=new Runnable(){
		@Override
		public void run(){
			long cuin;
			try{
				while(true){
					Thread.sleep(1000l*FL_UPDATE_INT_MAX);
					cuin=Utils.getLongAccountUin();
					if(cuin>1000){
						log("try post task for "+cuin);
						getCurrent().timeToUpdateFl();
					}
				}
			}catch(Exception e){
				log(e);
			}
		}
	};

	private ExfriendManager(long uin){
		mUin=uin;
		if(tp==null){
			tp=Executors.newCachedThreadPool();
			tp.execute(asyncUpdateAwaitingTask);
		}
		//persons=loadSavedPersonsInfo();
		try{
			loadSavedPersonsInfo();
			dbg();
			try{
				mStdRemarks= getFriendsConcurrentHashMap(getFriendsManager());
			}catch(Throwable e){}
			if(persons.size()==0&&mStdRemarks!=null){
				dbg();
				log("WARNING:INIT FROM THE INTERNAL");
				//Here we try to copy friendlist
				Object fr;
				Field fuin,fremark,fnick;
				Class clz_fr=load("com/tencent/mobileqq/data/Friends");
				fuin=clz_fr.getField("uin");//long!!!
				fuin.setAccessible(true);
				fremark=clz_fr.getField("remark");
				fremark.setAccessible(true);
				fnick=clz_fr.getField("name");
				fnick.setAccessible(true);
				persons=new HashMap<>();
				Iterator<Map.Entry> it=mStdRemarks.entrySet().iterator();
				while(it.hasNext()){
					long t=System.currentTimeMillis()/1000;
					fr=it.next().getValue();
					if(fr==null)continue;
					try{
					}catch(Exception e){
						continue;
					}
					FriendRecord f=new FriendRecord();
					f.uin=Long.parseLong((String)fuin.get(fr));
					f.remark=(String)fremark.get(fr);
					f.nick=(String)fnick.get(fr);
					f.friendStatus=FriendRecord.STATUS_RESERVED;
					f.serverTime=t;
					if(!persons.containsKey(f.uin))
						persons.put(f.uin,f);
				}
				dbg();
				saveConfigure();
				dbg();
			}
		}catch(Exception e){
			log(e);
		}
	}


	/**  ?(0xFE)QNC I_version I_size I_RAW_reserved 16_md5 DATA */
	public @Nullable void loadSavedPersonsInfo(){
		synchronized(this){
		if(fileData==null)fileData=new HashMap<>();
		else fileData.clear();
		if(persons==null){
			persons=new HashMap<>();
		}
		if(events==null){
			events=new HashMap<>();
		}
		dbg();
		File f=new File(Utils.getApplication().getFilesDir().getAbsolutePath()+"/qnotified_"+mUin+".dat");
		if(!f.exists())return;
		try{
			FileInputStream fin=new FileInputStream(f);
			DataInputStream in=new DataInputStream(fin);
			in.skip(4);//flag
			int ver=in.readInt();
			int file_size=in.readInt();
			readIRaw(in);//ignore
			byte[] md5=new byte[16];
			if(in.read(md5,0,16)<16)throw new IOException("Failed to read md5");
			String key;

			a:while(in.available()>0){
				int _type=in.read();
				if(_type<=0||_type>255)throw new IOException("Unexpected type:"+_type+",version:"+ver);
				key=readIStr(in);
				switch((byte)_type){
					case TYPE_NULL:
						fileData.put(key,null);
						break;
					case TYPE_BYTE:
						fileData.put(key,(byte)in.read());
						break;
					case TYPE_BOOL:
						fileData.put(key,in.read()!=0);
						break;
					case TYPE_CODEPOINT:
						fileData.put(key,in.readInt());
						break;
					case TYPE_INT:
						fileData.put(key,in.readInt());
						break;
					case TYPE_SHORT:
						fileData.put(key,in.readShort());
						break;
					case TYPE_LONG:
						fileData.put(key,in.readLong());
						break;
					case TYPE_FLOAT:
						fileData.put(key,in.readFloat());
						break;
					case TYPE_DOUBLE:
						fileData.put(key,in.readDouble());
						break;
					case TYPE_ISTR:
						fileData.put(key,readIStr(in));
						break;
					case TYPE_IRAW:
						fileData.put(key,readIRaw(in));
						break;
					case TYPE_TABLE:
						fileData.put(key,readTable(in));
						break;
					case TYPE_EOF:
						break a;
					default:
						throw new IOException("Unexpected type:"+_type+",name:\""+key+"\",version:"+ver);
				}
			}
			dbg();
			updateFriendTableVersion();
			initEventsTable();
			dbg();
			tableToFriend();
			tableToEvents();
			lastUpdateTimeSec=0l;
			try{
				lastUpdateTimeSec=fileData.get("lastUpdateFl");
			}catch(Exception e){}
			dbg();
		}catch(IOException e){
			log(e);
		}
		}
	}


	/* We try to add some columns */
	private void updateFriendTableVersion(){
		Table<Long> fr=(Table<Long>)fileData.get("friends");
		if(fr==null){
			log("damn! updateFriendTableVersion in null");
		}
		/** uin+"" is key */
		fr.keyName="uin";
		fr.keyType=TYPE_LONG;
		fr.addField("nick",TYPE_ISTR);
		fr.addField("remark",TYPE_ISTR);
		fr.addField("friendStatus",TYPE_INT);
		fr.addField("serverTime",TYPE_LONG);
	}

	private void friendToTable(){
		dbg();
		Iterator<Map.Entry<Long,FriendRecord>> it=/*(Iterator<Map.Entry<Long, FriendRecord>>)*/persons.entrySet().iterator();
		Map.Entry<Long,FriendRecord> ent;
		String suin;
		Table<Long> t=(Table<Long>)fileData.get("friends");
		if(t==null){
			t=new Table<>();
			t.init();
			fileData.put("friends",t);
			updateFriendTableVersion();
		}
		long t_t;
		FriendRecord f;
		Long k;
		while(it.hasNext()){
			ent=it.next();
			f=ent.getValue();
			t.insert(ent.getKey());
			k=ent.getKey();
			try{
				t.set(k,"nick",f.nick);
				t.set(k,"remark",f.remark);
				t.set(k,"serverTime",f.serverTime);
				t.set(k,"friendStatus",f.friendStatus);
			}catch(NoSuchFieldException e){
				//shouldn't happen
			}
		}
		dbg();
	}

	private void tableToFriend(){
		Table<Long> t=(Table<Long>)fileData.get("friends");
		if(t==null){
			log("t_fr==null,aborting!");
			dbg();
			return;
		}
		dbg();
		if(persons==null)persons=new HashMap<>();
		Iterator<Map.Entry<Long,Object[]>> it=t.records.entrySet().iterator();
		Map.Entry<Long,Object[]> entry;
		int _nick,_remark,_fs,_time;
		_nick=t.getFieldId("nick");
		_remark=t.getFieldId("remark");
		_fs=t.getFieldId("friendStatus");
		_time=t.getFieldId("serverTime");
		Object rec[];
		while(it.hasNext()){
			entry=it.next();
			FriendRecord f=new FriendRecord();
			f.uin=entry.getKey();
			rec=entry.getValue();
			f.remark=(String)rec[_remark];
			f.nick=(String)rec[_nick];
			f.friendStatus=(Integer)rec[_fs];
			f.serverTime=(Long)rec[_time];
			persons.put(f.uin,f);
		}
		dbg();
		/*FriendRecord f=new FriendRecord();
		 f.uin=1084515740;
		 f.remark="李王凯";
		 f.nick="三尺竹剑泣血歌";
		 f.friendStatus=FriendRecord.STATUS_FRIEND_MUTUAL;
		 f.serverTime=1543202800;
		 persons.put(f.uin,f);
		 log("Faking lwk");*/
	}


	/** We try to add some columns */
	private void initEventsTable(){
		Table<Integer> ev=(Table<Integer>)fileData.get("events");
		if(ev==null){
			log("damn! initEvT in null");
			return;
		}
		/** uin+"" is key */
		ev.keyName="id";
		ev.keyType=TYPE_INT;
		ev.addField("timeRangeEnd",TYPE_LONG);
		ev.addField("timeRangeBegin",TYPE_LONG);
		ev.addField("event",TYPE_INT);
		ev.addField("operator",TYPE_INT);
		ev.addField("before",TYPE_ISTR);
		ev.addField("after",TYPE_ISTR);
		ev.addField("extra",TYPE_ISTR);
		ev.addField("_nick",TYPE_ISTR);
		ev.addField("_remark",TYPE_ISTR);
		ev.addField("_friendStatus",TYPE_INT);
	}

	private void eventsToTable(){
		Iterator<Map.Entry<Integer,EventRecord>> it=/*(Iterator<Map.Entry<Long, FriendRecord>>)*/events.entrySet().iterator();
		Map.Entry<Integer,EventRecord> ent;
		String suin;
		Table<Integer> t=(Table<Integer>)fileData.get("events");
		if(t==null){
			t=new Table<>();
			t.init();
			fileData.put("events",t);
			initEventsTable();
		}else{
			t.records.clear();
		}
		EventRecord ev;
		int k;
		while(it.hasNext()){
			ent=it.next();
			ev=ent.getValue();
			t.insert(ent.getKey());
			k=ent.getKey();
			try{
				t.set(k,"timeRangeEnd",ev.timeRangeEnd);
				t.set(k,"timeRangeBegin",ev.timeRangeBegin);
				t.set(k,"event",ev.event);
				t.set(k,"operator",ev.operator);
				t.set(k,"before",ev.before);
				t.set(k,"after",ev.after);
				t.set(k,"extra",ev.extra);
				t.set(k,"_nick",ev._nick);
				t.set(k,"_remark",ev._remark);
				t.set(k,"_friendStatus",ev._friendStatus);
			}catch(NoSuchFieldException e){
				//shouldn't happen
			}
			//log("addEx,"+ev.operator);
		}
	}

	private void tableToEvents(){
		Table<Integer> t=(Table<Integer>)fileData.get("events");
		if(t==null){
			log("t_ev==null,aborting!");
			return;
		}
		if(events==null)events=new HashMap<>();
		Iterator<Map.Entry<Integer,Object[]>> it=t.records.entrySet().iterator();
		Map.Entry<Integer,Object[]> entry;
		int __nick,__remark,__fs,_te,_tb,_ev,_op,_b,_a,_extra;
		__nick=t.getFieldId("_nick");
		__remark=t.getFieldId("_remark");
		__fs=t.getFieldId("_friendStatus");
		_te=t.getFieldId("timeRangeEnd");
		_tb=t.getFieldId("timeRangeBegin");
		_ev=t.getFieldId("event");
		_op=t.getFieldId("operator");
		_b=t.getFieldId("before");
		_a=t.getFieldId("after");
		_extra=t.getFieldId("extra");
		Object rec[];
		while(it.hasNext()){
			entry=it.next();
			EventRecord ev=new EventRecord();
			//e=entry.getKey();
			rec=entry.getValue();
			ev._nick=(String)rec[__nick];
			ev._remark=(String)rec[__remark];
			ev._friendStatus=(Integer)rec[__fs];
			ev.timeRangeBegin=(Long)rec[_tb];
			ev.timeRangeEnd=(Long)rec[_te];
			ev.event=(Integer)rec[_ev];
			ev.operator=(Long)rec[_op];
			ev.before=(String)rec[_b];
			ev.after=(String)rec[_a];
			ev.extra=(String)rec[_extra];
			events.put(entry.getKey(),ev);
		}
	}




	public void saveConfigure(){
		dbg();
		//log("save: persons.size()="+persons.size()+"event.size="+events.size());
		if(persons==null)return;
		File f=new File(Utils.getApplication().getFilesDir().getAbsolutePath()+"/qnotified_"+mUin+".dat");
		friendToTable();
		eventsToTable();
		fileData.put("uin",mUin);
		try{
			if(!f.exists())f.createNewFile();
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			DataOutputStream out=new DataOutputStream(baos);
			Iterator<Map.Entry<String,Object>> it=fileData.entrySet().iterator();
			Map.Entry<String,Object> record;
			String fn;
			Object val;
			while(it.hasNext()){
				record=it.next();
				fn=record.getKey();
				val=record.getValue();
				writeRecord(out,fn,val);
			}
			out.flush();
			out.close();
			baos.close();
			byte[] dat=baos.toByteArray();
			byte[] md5;
			try{
				MessageDigest md=MessageDigest.getInstance("MD5");
				md.update(dat);
				md5=md.digest();
			}catch(NoSuchAlgorithmException e){
				md5=new byte[16];
			}
			FileOutputStream fout=new FileOutputStream(f);
			out=new DataOutputStream(fout);
			out.write(new byte[]{(byte)0xFE,'Q','N','C'});
			out.writeInt(_VERSION_CURRENT);//ver
			out.writeInt(dat.length);
			out.writeInt(0);//reserved
			out.write(md5,0,16);
			out.write(dat,0,dat.length);
			out.flush();
			fout.flush();
			out.close();
			fout.close();
			dbg();
		}catch(IOException e){
			log(e);
		}
		dirtyFlag=false;
	}

	public static ExfriendManager getCurrent(){
		return get(Utils.getLongAccountUin());
	}

	public static ExfriendManager get(long uin){
		if(uin<10000)throw new IllegalArgumentException("uin must >= 10000 ");
		synchronized(instances){
			ExfriendManager ret=instances.get(uin);
			if(ret!=null)return ret;
			ret=new ExfriendManager(uin);
			instances.put(uin,ret);
			return ret;
		}
	}

	public static Object getFriendsManager() throws Exception{
		Object qqAppInterface=Utils.getAppRuntime();
        return invoke_virtual(qqAppInterface,"getManager",50,int.class);
    }

    public static ConcurrentHashMap getFriendsConcurrentHashMap(Object friendsManager) throws IllegalAccessException, NoSuchFieldException{
        for(Field field : load("com.tencent.mobileqq.app.FriendsManager").getDeclaredFields()){
            if(ConcurrentHashMap.class==field.getType()){
                field.setAccessible(true);
                ConcurrentHashMap concurrentHashMap = (ConcurrentHashMap) field.get(friendsManager);
                if(concurrentHashMap!=null&&concurrentHashMap.size()>0){
                    if(concurrentHashMap.get(concurrentHashMap.keySet().toArray()[0]).getClass()==load("com.tencent.mobileqq.data.Friends")){
                        return concurrentHashMap;
                    }
                }
            }
        }
        throw new NoSuchFieldException();
    }

	/** @hide */
	@Deprecated
	public HashMap<Long,FriendRecord> getPersons(){
		return persons;
	}

	/** @hide */
	@Deprecated
	public HashMap<Integer,EventRecord> getEvents(){
		return events;
	}

	/** @method getRemark: return remark if it's a friend,or one's nickname if not*/
	public String getRemark(long uin){
		return (String)mStdRemarks.get(""+uin);
	}

	public static void onGetFriendListResp(FriendChunk fc){
		//log("onGetFriendListResp");
		get(fc.uin).recordFriendChunk(fc);
	}



	public synchronized void recordFriendChunk(FriendChunk fc){
		//log("recordFriendChunk");
		if(fc.getfriendCount==0){
			//ignore it;
		}else{
			if(fc.startIndex==0)cachedFriendChunks.clear();
			cachedFriendChunks.add(fc);
			//log(fc.friend_count+","+fc.startIndex+","+fc.totoal_friend_count);
			if(fc.friend_count+fc.startIndex==fc.totoal_friend_count){
				final FriendChunk[] update=new FriendChunk[cachedFriendChunks.size()];
				cachedFriendChunks.toArray(update);
				cachedFriendChunks.clear();
				tp.execute(new Runnable(){
						@Override
						public void run(){
							asyncUpdateFriendListTask(update);
						}
					});
			}
		}
	}

	public void setRedDot(){
		if(QQMainHook.redDotRef==null)return;
		final TextView rd=QQMainHook.redDotRef.get();
		if(rd==null){
			log("Red dot missing!");
			return;
		}
		int m=0;
		try{
			m=fileData.get("unread");
		}catch(Exception e){}
		final int n=m;
		((Activity)Utils.getContext(rd)).runOnUiThread(new Runnable(){
				@Override
				public void run(){
					if(n<1)rd.setVisibility(View.INVISIBLE);
					else{
						rd.setText(""+n);
						rd.setVisibility(View.VISIBLE);
					}
				}
			});
	}

	public void reportEventWithoutSave(EventRecord ev,Object[] out){
		//log("Report event,uin="+ev.operator);
		int k=events.size();
		while(events.containsKey(k)){
			k++;
		}
		events.put(k,ev);
		dirtyFlag=true;
		if(out==null)return;
		int unread=0;
		if(fileData.containsKey("unread")){
			unread=(Integer)fileData.get("unread");
		}
		unread++;
		fileData.put("unread",unread);
		String title,ticker,tag,c;
		//Notification.Builder nb=Notification.Builder();
		if(ev._remark!=null&&ev._remark.length()>0)tag=ev._remark+"("+ev.operator+")";
		else if(ev._nick!=null&&ev._nick.length()>0)tag=ev._nick+"("+ev.operator+")";
		else tag=""+ev.operator;
		out[0]=unread;
		ticker=unread+"位好友已删除";
		if(unread>1){
			title=unread+"位好友已删除";
			c=tag+"等"+unread+"位好友";
		}else{
			title=tag;
			c="删除于"+new Date(ev.timeRangeBegin*1000)+"后";
		}
		out[1]=ticker;
		out[2]=title;
		out[3]=c;
	}

	public void clearUnreadFlag(){
		fileData.put("unread",0);
		try{
			NotificationManager nm=(NotificationManager) Utils.getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
			nm.cancel(ID_EX_NOTIFY);
		}catch(Exception e){
			log(e);
		}
		dirtyFlag=true;
		setRedDot();
		saveConfigure();
	}

	private void asyncUpdateFriendListTask(FriendChunk[] fcs){
		Object[] ptr=new Object[4];
		synchronized(this){
			//check integrity
			dbg();
			boolean totality=true;
			int tmp=fcs[fcs.length-1].totoal_friend_count;
			int len=fcs.length;
			for(int i=0;i<fcs.length;i++){
				tmp-=fcs[len-i-1].friend_count;
			}
			totality=tmp==0;
			if(!totality){
				log("Inconsistent friendlist chunk data!Aborting!total="+tmp);
				return;
			}
			HashMap<Long,FriendRecord> del=(HashMap<Long, FriendRecord>) persons.clone();
			FriendRecord fr;
			for(int i=0;i<fcs.length;i++){
				for(int ii=0;ii<fcs[i].friend_count;ii++){
					fr=del.remove(fcs[i].arrUin[ii]);
					if(fr!=null){
						fr.friendStatus=FriendRecord.STATUS_FRIEND_MUTUAL;
						fr.nick=fcs[i].arrNick[ii];
						fr.remark=fcs[i].arrRemark[ii];
						fr.serverTime=fcs[i].serverTime;
					}else{
						fr=new FriendRecord();
						fr.uin=fcs[i].arrUin[ii];
						fr.friendStatus=FriendRecord.STATUS_FRIEND_MUTUAL;
						fr.nick=fcs[i].arrNick[ii];
						fr.remark=fcs[i].arrRemark[ii];
						fr.serverTime=fcs[i].serverTime;
						persons.put(fcs[i].arrUin[ii],fr);
					}
				}
			}
			dbg();
			Iterator<Map.Entry<Long,FriendRecord>> it=del.entrySet().iterator();
			Map.Entry<Long,FriendRecord> ent;
			EventRecord ev;
			ptr[0]=0;//num,ticker,title,content
			while(it.hasNext()){
				ent=it.next();
				fr=ent.getValue();
				if(fr.friendStatus==FriendRecord.STATUS_FRIEND_MUTUAL){
					ev=new EventRecord();
					ev._friendStatus=fr.friendStatus;
					ev._nick=fr.nick;
					ev._remark=fr.remark;
					ev.event=EventRecord.EVENT_FRIEND_DELETE;
					ev.operator=fr.uin;
					ev.timeRangeBegin=fr.serverTime;
					ev.timeRangeEnd=fcs[fcs.length-1].serverTime;
					reportEventWithoutSave(ev,ptr);
					fr.friendStatus=FriendRecord.STATUS_EXFRIEND;
				}
				//requestIndividual(fr.uin);
			}
		}
		doNotifyDelFlAndSave(ptr);
		lastUpdateTimeSec=fcs[0].serverTime;
	}

	public void markActiveDelete(long uin){
		try{
			if(!ConfigManager.get().getBooleanOrDefault("qn_del_op_silence",true))return;
		}catch(IOException e){}
		synchronized(this){
			FriendRecord fr=persons.get(uin);
			if(fr==null){
				try{
					showToast(QQMainHook.splashActivityRef.get(),TOAST_TYPE_ERROR,"onActDelResp:get("+uin+")==null",Toast.LENGTH_SHORT);
				}catch(Throwable e){}
				return;
			}
			EventRecord ev=new EventRecord();
			ev._friendStatus=fr.friendStatus;
			ev._nick=fr.nick;
			ev._remark=fr.remark;
			ev.timeRangeBegin=fr.serverTime;
			ev.timeRangeEnd=fr.serverTime=System.currentTimeMillis()/1000;
			fr.friendStatus=FriendRecord.STATUS_EXFRIEND;
			ev.operator=this.getUin();
			ev.event=EventRecord.EVENT_FRIEND_DELETE;
			reportEventWithoutSave(ev,null);
			saveConfigure();
		}
	}
	
	@SuppressLint("MissingPermission")
	public void doNotifyDelFlAndSave(Object[] ptr){
		if(((int)ptr[0])>0){
			Intent intent=new Intent(getApplication(),load(ActProxyMgr.STUB_ACTIVITY));
			int id=ActProxyMgr.next();
			intent.putExtra(QQMainHook.ACTIVITY_PROXY_ID_TAG,id);
			intent.putExtra(QQMainHook.ACTIVITY_PROXY_ACTION,QQMainHook.ACTION_EXFRIEND_LIST);
			PendingIntent pi= PendingIntent.getActivity(getApplication(),0,intent,0);
			try{
				/*Bitmap bp;
				 InputStream in=QThemeKit.openAsset("ic_del_friend_top.png");
				 bp=BitmapFactory.decodeStream(in);*/
				NotificationManager nm=(NotificationManager) Utils.getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
				Notification n=createNotiComp((String)ptr[1],(String)ptr[2],(String)ptr[3],pi);
				nm.notify(ID_EX_NOTIFY,n);
				Vibrator vb=(Vibrator)getApplication().getSystemService(Context.VIBRATOR_SERVICE);
				vb.vibrate(new long[]{100,200,200,100},-1);
				setRedDot();
			}catch(Exception e){
				log(e);
			}
			dirtyFlag=true;
		}
		fileData.put("lastUpdateFl",lastUpdateTimeSec);
		log("Friendlist updated @"+lastUpdateTimeSec);
		dbg();
		saveConfigure();
		dbg();
	}

	private static Context remotePackageContext;

	public Notification createNotiComp(String ticker,String title,String content,PendingIntent pi) throws PackageManager.NameNotFoundException, InvocationTargetException, SecurityException, IllegalAccessException, IllegalArgumentException, NoSuchMethodException, InstantiationException{
		if(remotePackageContext==null)
			remotePackageContext=getApplication().createPackageContext(PACKAGE_NAME_SELF,Context.CONTEXT_INCLUDE_CODE|Context.CONTEXT_IGNORE_SECURITY);
		Object builder=new_instance(load("android/support/v4/app/NotificationCompat$Builder"),remotePackageContext,Context.class);
		invoke_virtual(builder,"setSmallIcon",R.drawable.ic_del_friend_top,int.class);
		invoke_virtual(builder,"setTicker",ticker,CharSequence.class);
		invoke_virtual(builder,"setContentTitle",title,CharSequence.class);
		invoke_virtual(builder,"setContentText",content,CharSequence.class);
		invoke_virtual(builder,"setContentIntent",pi,PendingIntent.class);
		return (Notification)invoke_virtual(builder,"build");
	}


	/*public static int getResourceId(Context context,String name,String type,String packageName){
	 Resources themeResources=null;
	 PackageManager pm=context.getPackageManager();
	 try{
	 themeResources=pm.getResourcesForApplication(packageName);
	 return themeResources.getIdentifier(name,type,packageName);
	 }catch(PackageManager.NameNotFoundException e){}
	 return 0;
	 }*/


	public void doRequestFlRefresh(){
		boolean inLogin;
		inLogin=(Utils.getLongAccountUin()==mUin);
		if(!inLogin){
			log("Uin("+mUin+") isn't logined in.");
			return;
		}
		try{
			log("Request friendlist update for "+mUin+" ...");
			invoke_virtual(Utils.getFriendListHandler(),"a",true,true,boolean.class,boolean.class,void.class);
		}catch(Exception e){
			log(e);
		}
	}

	public void timeToUpdateFl(){
		long t=System.currentTimeMillis()/1000;
		//log(t+"/"+lastUpdateTimeSec);
		if(t-lastUpdateTimeSec>FL_UPDATE_INT_MIN){
			tp.execute(new Runnable(){
					@Override
					public void run(){
						doRequestFlRefresh();
					}
				});
		}
	}
}
