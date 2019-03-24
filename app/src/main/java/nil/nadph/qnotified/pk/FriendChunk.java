package nil.nadph.qnotified.pk;

import java.io.*;
import java.lang.reflect.*;
import nil.nadph.qnotified.*;
import de.robv.android.xposed.*;
import java.util.*;

public class FriendChunk implements Serializable,Cloneable{
	public byte cHasOtherRespFlag;
	public byte cRespType;
	public short errorCode;
	public short friend_count;//len
	public short getfriendCount;
	public byte ifReflush;
	public short online_friend_count;
	public int reqtype;
	public int result;
	public long serverTime;
	public short sqqOnLine_count;
	public short startIndex;
	public short totoal_friend_count;
	public long uin;

	public long[] arrUin;
	public String[] arrRemark;
	public String[] arrNick;
	public byte[] arrcSpecialFlag;
	public byte[] arrStatus;

	private static Field[] from;
	private static Field[] to;

	private static int validLength=-1;
	private static int maxLength=14;

	private static Field f_uin,f_remark,f_nick,f_cSpecialFlag,f_status,f_stSelfInfo;

	public FriendChunk(Object resp){
		fromGetFriendListResp(resp);
	}
	
	public FriendChunk(){}

	public void fromGetFriendListResp(Object resp){
		if(validLength<0)initOnce();
		try{
			for(int i=0;i<validLength;i++){
				to[i].set(this,from[i].get(resp));
				//XposedBridge.log(from[i].getName()+"=>"+to[i].getName());
			}
			int len=friend_count;
			arrStatus=new byte[len];
			arrUin=new long[len];
			arrRemark=new String[len];
			arrNick=new String[len];
			arrcSpecialFlag=new byte[len];
			ArrayList fs=(ArrayList)f_stSelfInfo.get(resp);
			for(int i=0;i<len;i++){
				arrStatus[i]=f_status.get(fs.get(i));
				arrUin[i]=f_uin.get(fs.get(i));
				arrRemark[i]=(String)f_remark.get(fs.get(i));
				arrNick[i]=(String)f_nick.get(fs.get(i));
				arrcSpecialFlag[i]=f_cSpecialFlag.get(fs.get(i));
			}
		}catch(IllegalAccessException e){}catch(ClassCastException e){
			XposedBridge.log(e);
		}
	}

	public static synchronized void initOnce(){
		if(validLength>0)return;
		from=new Field[maxLength];
		to=new Field[maxLength];
		Class clz_gfr=QConst.load("friendlist/GetFriendListResp");
		validLength=0;
		Field[] mine=FriendChunk.class.getDeclaredFields();
		//Field[] his=clz_gfr.getDeclaredFields();
		Field f;
		for(int i=0;i<mine.length;i++){
			try{
				if(!mine[i].getName().startsWith("arr")&&!Modifier.isStatic(mine[i].getModifiers())){
					f=clz_gfr.getField(mine[i].getName());
					f.setAccessible(true);
					mine[i].setAccessible(true);
					from[validLength]=f;
					to[validLength++]=mine[i];
				}
			}catch(Throwable e){}
		}
		try{
			f_stSelfInfo=clz_gfr.getField("vecFriendInfo");
			f_stSelfInfo.setAccessible(true);
		}catch(NoSuchFieldException e){}
		Class clz_fi=QConst.load("friendlist/FriendInfo");
		try{
			f_uin=clz_fi.getField("friendUin");
			f_uin.setAccessible(true);
		}catch(NoSuchFieldException e){}
		try{
			f_remark=clz_fi.getField("remark");
			f_remark.setAccessible(true);
		}catch(NoSuchFieldException e){}
		try{
			f_nick=clz_fi.getField("nick");
			f_nick.setAccessible(true);
		}catch(NoSuchFieldException e){}
		try{
			f_cSpecialFlag=clz_fi.getField("cSpecialFlag");
			f_cSpecialFlag.setAccessible(true);
		}catch(NoSuchFieldException e){}
		try{
			f_status=clz_fi.getField("status");
			f_status.setAccessible(true);
		}catch(NoSuchFieldException e){}

	}

	@Override
	public int hashCode(){
		return (int)serverTime;
	}
	
	

}
