package nil.nadph.qnotified;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import dalvik.bytecode.*;

public class DexKit{

	public static void a() throws IOException{
		
		InputStream fin=new FileInputStream("/tmp/classes4.dex");
		final byte[]buf=new byte[fin.available()];
		fin.read(buf);
		if(fin.read()>=0)throw new IOException("size err");
		fin.close();
		final int ret[]={-2};

		byte[]target=new byte[]{04,(byte)0xE6,(byte)0x9A,(byte)0x82,(byte)0xE4,(byte)0xB8,(byte)0x8D,(byte)0xE5,(byte)0x8D,(byte)0x87,(byte)0xE7,(byte)0xBA,(byte)0xA7};
		final float f[]=new float[1];
		/*new Thread(new Runnable(){
		 @Override
		 public void run(){
		 while(ret[0]==-2){
		 try{
		 Thread.sleep(10);
		 }catch(InterruptedException e){}
		 System.out.println(f[0]*100);
		 }
		 System.out.println(ret[0]);
		 try{
		 ret.notify();
		 }catch(Exception e){}
		 }
		 }).start();*/
		ret[0]=arrayIndexOf(buf,target,0,buf.length,f);
		/*try{
		 synchronized(ret){
		 ret.wait();}
		 }catch(InterruptedException e){}*/
		System.out.println(ret[0]);
		ret[0]=arrayIndexOf(buf,int2u4le(ret[0]),0,buf.length,f);
		System.out.println(ret[0]);
		int off=(ret[0]-u4le2int(buf,0x3c))/4;
		if(off>0xFFFF){
			target=int2u4le(off);
		}else target=int2u2le(off);
		off=0;
		while(true){
			off=arrayIndexOf(buf,target,off+1,buf.length,f);
			if(off==-1)break;
			if(buf[off-2]==(byte)Opcodes.OP_CONST_STRING
			   ||buf[off-2]==(byte)Opcodes.OP_CONST_STRING_JUMBO)
				ret[0]=off-2;
		}

		System.out.println(ret[0]);
		int pDexMapList=u4le2int(buf,0x34);
		int ptr=pDexMapList;
		int map_size=u4le2int(buf,pDexMapList);
		ptr+=4;
		short type;
		int size;
		int pStringDataItem=0
		 ,pClassDataItem=0
		 ,pCodeItem=0
		 ,pTypeList=0
		 ,pMapList=0
		 ,pClassDefItem=0
		 ,pMethodIdItem=0
		 ,pFieldIdItem=0
		 ,pProtoIdItem=0
		 ,pTypeIdItem=0
		 ,pStringIdItem=0;
		 
		for(int i=0;i<map_size;i++){
			//type=read
		}

		System.out.println("end");
	}

	/*struct DexMethod {
	 u4 methodIdx;    /* 指向DexMethodId列表的索引 *
	 u4 accessFlags;
	 u4 codeOff;      /* 指向DexCode结构的偏移 *
	 };

	 struct DexCode {
	 u2  registersSize;//使用寄存器个数
	 u2  insSize;//参数个数
	 u2  outsSize;//调用其他方法时使用的寄存器个数
	 u2  triesSize;//try/catch个数
	 u4  debugInfoOff;//指向调试信息的偏移
	 u4  insnsSize;//指令集个数，以2字节为单位
	 u2  insns[1];//指令集
	 /* followed by optional u2 padding */
	/* followed by try_item[triesSize] */
	/* followed by uleb handlersSize */
	/* followed by catch_handler_item[handlersSize] *
	 };
	 作者：SmileUsers
	 链接：https://www.jianshu.com/p/f7f0a712ddfe
	 來源：简书
	 简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。
	 */


	public static int arrayIndexOf(byte[]arr,byte[]subarr,int startindex,int endindex,float[] progress){
		byte a=subarr[0];
		float d=endindex-startindex;
		int b=endindex-subarr.length;
		int i=startindex;
		int ii;
		a:while(i<=b){
			if(arr[i]!=a){
				progress[0]=(i++-startindex)/d;
				continue;
			}else{
				for(ii=0;ii<subarr.length;ii++){
					if(arr[i++]!=subarr[ii]){
						i=i-ii;
						continue a;
					}
				}
				return i-ii;
			}
		}
		return -1;
	}

	public static byte[] int2u4le(int i){
		return new byte[]{(byte)i,(byte)(i>>8),(byte)(i>>16),(byte)(i>>24)};
	}

	public static byte[] int2u2le(int i){
		return new byte[]{(byte)i,(byte)(i>>8)};
	}

	public static int u4le2int(byte[]buf,int index){
		int i=buf[index]|(buf[index+1]<<8)|(buf[index+2]<<16)|(buf[index+3]<<24);
		return i;
	}

	public static final short
	kDexTypeHeaderItem               = 0x0000,
    kDexTypeStringIdItem             = 0x0001,
    kDexTypeTypeIdItem               = 0x0002,
    kDexTypeProtoIdItem              = 0x0003,
    kDexTypeFieldIdItem              = 0x0004,
    kDexTypeMethodIdItem             = 0x0005,
    kDexTypeClassDefItem             = 0x0006,
    kDexTypeMapList                  = 0x1000,
    kDexTypeTypeList                 = 0x1001,
    kDexTypeAnnotationSetRefList     = 0x1002,
    kDexTypeAnnotationSetItem        = 0x1003,
    kDexTypeClassDataItem            = 0x2000,
    kDexTypeCodeItem                 = 0x2001,
    kDexTypeStringDataItem           = 0x2002,
    kDexTypeDebugInfoItem            = 0x2003,
    kDexTypeAnnotationItem           = 0x2004,
    kDexTypeEncodedArrayItem         = 0x2005,
    kDexTypeAnnotationsDirectoryItem = 0x2006;
}
