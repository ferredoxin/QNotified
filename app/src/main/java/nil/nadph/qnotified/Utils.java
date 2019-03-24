package nil.nadph.qnotified;

import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import de.robv.android.xposed.*;
import java.lang.reflect.*;

import static nil.nadph.qnotified.QConst.load;
import android.app.*;

public class Utils{

	public static boolean DEBUG=true;

	public static final int CURRENT_MODULE_VERSION=1;

	public static final String PACKAGE_NAME_QQ = "com.tencent.mobileqq";
    public static final String PACKAGE_NAME_TIM = "com.tencent.tim";
	public static final String PACKAGE_NAME_SELF = "nil.nadph.qnotified";
    public static final String PACKAGE_NAME_XPOSED_INSTALLER = "de.robv.android.xposed.installer";

	public static final int TOAST_TYPE_INFO=0;
	public static final int TOAST_TYPE_ERROR=1;
	public static final int TOAST_TYPE_SUCCESS=2;
	
	
	
	public static int getActiveModuleVersion(){
		Math.sqrt(1);
		Math.random();
		Math.expm1(0.001);
		//Let's make the function longer,so that it will work in VirtualXposed
		return 0;
	}
	
	/** Use Utils.getApplication() Instead */
	@Deprecated()
    @SuppressWarnings ("all")
    public static Context getSystemContext() {
        return (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0]);
    }
	
	public static Application getApplication(){
		try{
			return (Application)invoke_static(load("com/tencent/common/app/BaseApplicationImpl"),"getApplication");
		}catch(Exception e){
			XposedBridge.log(e);
		}
		return null;
	}

	public static int getQQVersionCode(Context context){
        try{
            return context.getPackageManager()
				.getPackageInfo(PACKAGE_NAME_QQ,0).versionCode;
        }catch(Throwable e){
            Log.e("Utils","Can not get QQ versionCode!");
            return 0;
        }
    }
	
	public static long getLongAccountUin(){
		try{
			return (long)invoke_virtual(getAppRuntime(),"getLongAccountUin");
		}catch(Exception e){
			XposedBridge.log(e);
		}
		return -1;
	}

    public static String getQQVersionName(Context context){
        try{
            return context.getPackageManager()
				.getPackageInfo(PACKAGE_NAME_QQ,0).versionName;
        }catch(Throwable e){
            Log.e("Utils","Can not get QQ versionName!");
            return "unknown";
        }
    }

	public static void ref_setText(View obj,CharSequence str){
		try{
			Method m=obj.getClass().getMethod("setText",CharSequence.class);
			m.setAccessible(true);
			m.invoke(obj,str);
		}catch(Exception e){
			log(e.toString());
		}
	}

	public static View.OnClickListener getOnClickListener(View v){
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH){
			return getOnClickListenerV14(v);
		}else{
			return getOnClickListenerV(v);
		}
	}

	/*public static Object invoke_virtual(Object obj,String method,Object...args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException{
	 Class clazz=obj.getClass();
	 Method m=findMethodByArgs(clazz,method,args);
	 m.setAccessible(true);
	 return m.invoke(obj,args);
	 }*/

	public static Object invoke_virtual(Object obj,String name,Object...argsTypesAndReturnType) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException{
		Class clazz=obj.getClass();
		int argc=argsTypesAndReturnType.length/2;
		Class[] argt=new Class[argc];
		Object[] argv=new Object[argc];
		Class returnType=null;
		if(argc*2+1==argsTypesAndReturnType.length)returnType=(Class)argsTypesAndReturnType[argsTypesAndReturnType.length-1];
		int i,ii;
		Method m[]=null;
		Method method=null;
		Class[] _argt;
		for(i=0;i<argc;i++){
			argt[i]=(Class)argsTypesAndReturnType[argc+i];
			argv[i]=argsTypesAndReturnType[i];
		}
		loop_main:do{
			m=clazz.getDeclaredMethods();
			loop:for(i=0;i<m.length;i++){
				if(m[i].getName().equals(name)){
					_argt=m[i].getParameterTypes();
					if(_argt.length==argt.length){
						for(ii=0;ii<argt.length;ii++){
							if(!argt[ii].equals(_argt[ii]))continue loop;
						}
						if(returnType!=null&&!returnType.equals(m[i].getReturnType()))continue;
						method=m[i];
						break loop_main;
					}
				}
			}
		}while(!Object.class.equals(clazz=clazz.getSuperclass()));
		if(method==null)throw new NoSuchMethodException(name);
		method.setAccessible(true);
		return method.invoke(obj,argv);
	}

	public static Object invoke_static(Class staticClass,String name,Object...argsTypesAndReturnType) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException{
		Class clazz=staticClass;
		int argc=argsTypesAndReturnType.length/2;
		Class[] argt=new Class[argc];
		Object[] argv=new Object[argc];
		Class returnType=null;
		if(argc*2+1==argsTypesAndReturnType.length)returnType=(Class)argsTypesAndReturnType[argsTypesAndReturnType.length-1];
		int i,ii;
		Method m[]=null;
		Method method=null;
		Class[] _argt;
		for(i=0;i<argc;i++){
			argt[i]=(Class)argsTypesAndReturnType[argc+i];
			argv[i]=argsTypesAndReturnType[i];
		}
		loop_main:do{
			m=clazz.getDeclaredMethods();
			loop:for(i=0;i<m.length;i++){
				if(m[i].getName().equals(name)){
					_argt=m[i].getParameterTypes();
					if(_argt.length==argt.length){
						for(ii=0;ii<argt.length;ii++){
							if(!argt[ii].equals(_argt[ii]))continue loop;
						}
						if(returnType!=null&&!returnType.equals(m[i].getReturnType()))continue;
						method=m[i];
						break loop_main;
					}
				}
			}
		}while(!Object.class.equals(clazz=clazz.getSuperclass()));
		if(method==null)throw new NoSuchMethodException(name);
		method.setAccessible(true);
		return method.invoke(null,argv);
	}
	
	
	public static Object getQQAppInterface(){
		return getAppRuntime();
	}

	public static Object getMobileQQService(){
		return iget_object(getQQAppInterface(),"a",load("com/tencent/mobileqq/service/MobileQQService"));
	}
	
	

	/*
	 public static Method findMethodByArgs(Class mclazz,String name,Object...argv)throws NoSuchMethodException{
	 Method ret=null;
	 Method[] m;

	 int i=0,ii=0;
	 Class clazz=mclazz;
	 Class argt[];
	 do{
	 m=clazz.getDeclaredMethods();
	 loop:for(i=0;i<m.length;i++){
	 if(m[i].getName().equals(name)){
	 argt=m[i].getParameterTypes();
	 if(argt.length==argv.length){
	 for(ii=0;ii<argt.length;ii++){
	 if(argv[ii]==null&&argt[ii].isPrimitive())continue loop;
	 if(
	 }
	 }
	 }
	 }
	 }while(!Object.class.equals(clazz=clazz.getSuperclass()));
	 throw new NoSuchMethodException(name+"@"+mclazz);
	 }*/


	//Used for APIs lower than ICS (API 14)
	private static View.OnClickListener getOnClickListenerV(View view){
		View.OnClickListener retrievedListener = null;
		String viewStr = "android.view.View";
		Field field;

		try{
			field=Class.forName(viewStr).getDeclaredField("mOnClickListener");
			retrievedListener=(View.OnClickListener) field.get(view);
		}catch(NoSuchFieldException ex){
			log("Reflection: No Such Field.");
		}catch(IllegalAccessException ex){
			log("Reflection: Illegal Access.");
		}catch(ClassNotFoundException ex){
			log("Reflection: Class Not Found.");
		}

		return retrievedListener;
	}

//Used for new ListenerInfo class structure used beginning with API 14 (ICS)
	private static View.OnClickListener getOnClickListenerV14(View view){
		View.OnClickListener retrievedListener = null;
		String viewStr = "android.view.View";
		String lInfoStr = "android.view.View$ListenerInfo";

		try{
			Field listenerField = Class.forName(viewStr).getDeclaredField("mListenerInfo");
			Object listenerInfo = null;

			if(listenerField!=null){
				listenerField.setAccessible(true);
				listenerInfo=listenerField.get(view);
			}

			Field clickListenerField = Class.forName(lInfoStr).getDeclaredField("mOnClickListener");

			if(clickListenerField!=null&&listenerInfo!=null){
				retrievedListener=(View.OnClickListener) clickListenerField.get(listenerInfo);
			}
		}catch(NoSuchFieldException ex){
			log("Reflection: No Such Field.");
		}catch(IllegalAccessException ex){
			log("Reflection: Illegal Access.");
		}catch(ClassNotFoundException ex){
			log("Reflection: Class Not Found.");
		}

		return retrievedListener;
	}

	public static <T extends Object> T _obj_clone(T obj){
        try{
			Class clazz=obj.getClass();
			T ret=(T)clazz.newInstance();
			Field f[];
			int i;
			while(!Object.class.equals(clazz)){
				f=clazz.getDeclaredFields();
				for(i=0;i<f.length;i++){
					f[i].setAccessible(true);
					f[i].set(ret,f[i].get(obj));
				}
				clazz=clazz.getSuperclass();
			}
			return ret;
		}catch(Throwable e){
			log("CLONE : "+e.toString());
		}
		return null;
    }

	public static <T extends View> T _view_clone(T obj){
        try{
			Class clazz=obj.getClass();
			T ret=(T)clazz.getConstructor(Context.class).newInstance(obj.getContext());
			Field f[];
			int i;
			while(!Object.class.equals(clazz)){
				f=clazz.getDeclaredFields();
				for(i=0;i<f.length;i++){
					f[i].setAccessible(true);
					f[i].set(ret,f[i].get(obj));
				}
				clazz=clazz.getSuperclass();
			}
			return ret;
		}catch(Throwable e){
			log("CLONE : "+e.toString());
		}
		return null;
    }
	
	public static Object sget_object(Class clazz,String name){
		return sget_object(clazz,name,null);
	}

	public static Object sget_object(Class clazz,String name,Class type){
		try{
			Field f=findField(clazz,type,name);
			f.setAccessible(true);
			return f.get(null);
		}catch(Exception e){
			XposedBridge.log(e);
		}
		return null;
	}
	
	public static Object iget_object(Object obj,String name){
		return iget_object(obj,name,null);
	}
	
	public static Object iget_object(Object obj,String name,Class type){
		Class clazz=obj.getClass();
		try{
			Field f=findField(clazz,type,name);
			f.setAccessible(true);
			return f.get(obj);
		}catch(Exception e){
			XposedBridge.log(e);
		}
		return null;
	}

	public static Object getAppRuntime(){
		Object baseApplicationImpl=sget_object(load("com/tencent/common/app/BaseApplicationImpl"),"sApplication");
		try{
			return invoke_virtual(baseApplicationImpl,"getRuntime");
		}catch(Exception e){
			log("getRuntime:"+e.toString());
			return null;
		}
	}
	
	public static String getAccount(){
		Object rt=getAppRuntime();
		try{
			return (String)invoke_virtual(rt,"getAccount");
		}catch(Exception e){
			XposedBridge.log(e);
			return null;
		}
	}
	
	public static Object getFriendListHandler(){
		return getBusinessHandler(1);
	}
	
	public static Object getBusinessHandler(int type){
		try{
			return invoke_virtual(getQQAppInterface(),"a",type,int.class,load("com/tencent/mobileqq/app/BusinessHandler"));
		}catch(Exception e){
			return null;
		}
	}

    public static <T> T getObject(Object obj,Class<?> type,String name){
        return getObject(obj.getClass(),type,name,obj);
    }

    public static <T> T getObject(Class clazz,Class<?> type,String name){
        return getObject(clazz,type,name,null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getObject(Class clazz,Class<?> type,String name,Object obj){
        try{
            Field field = findField(clazz,type,name);
            return field==null? null :(T) field.get(obj);
        }catch(Exception e){
            return null;
        }
    }


    public static Field findField(Class<?> clazz,Class<?> type,String name){
        if(clazz!=null&&!name.isEmpty()){
            Class<?> clz = clazz;
            do {
                for(Field field : clz.getDeclaredFields()){
                    if((type==null||field.getType().equals(type))&&field.getName()
					   .equals(name)){
                        field.setAccessible(true);
                        return field;
                    }
                }
            } while ((clz=clz.getSuperclass())!=null);
            log(String.format("Can't find the field of type: %s and name: %s in class: %s!",type.getName(),name,clazz.getName()));
        }
        return null;
    }

	public static void log(String str){
		Log.d("QNotified",str);
		Log.i("Xposed",str);
	}
	
	public static String en(String str){
		if(str==null)return "null";
		return "\""+str.replace("\\","\\\\").replace("\"","\\\"")
			.replace("\n","\\\n").replace("\r","\\\r")+"\"";
	}
	
	public static String de(String str){
		if(str==null)return null;
		if(str.equals("null"))return null;
		if(str.startsWith("\""))str=str.substring(1);
		if(str.endsWith("\"")&&!str.endsWith("\\\""))str=str.substring(0,str.length()-1);
		return str.replace("\\\"","\"").replace("\\\n","\n")
		.replace("\\\r","\r").replace("\\\\","\\");
	}
	
	private static Method method_Toast_show;
	private static Method method_Toast_makeText;
	
	public static Toast showToast(Context ctx,int type,CharSequence str,int length)throws Throwable{
		if(method_Toast_show==null){
			Class cls=QConst.load("com/tencent/mobileqq/widget/QQToast");
			Method[] ms=cls.getMethods();
			for(int i=0;i<ms.length;i++){
				if(Toast.class.equals(ms[i].getReturnType())&&ms[i].getParameterTypes().length==0){
					method_Toast_show=ms[i];
					break;
				}
			}
		}
		if(method_Toast_makeText==null){
			method_Toast_makeText=QConst.load("com/tencent/mobileqq/widget/QQToast").getMethod("a",Context.class,int.class,CharSequence.class,int.class);
		}
		Object qqToast=method_Toast_makeText.invoke(null,ctx,type,str,length);
		return (Toast)method_Toast_show.invoke(qqToast);
	}
	
	public static Toast showToastShort(Context ctx,CharSequence str)throws Throwable{
		return showToast(ctx,0,str,0);
	}
	
	public static void dumpTrace(){
		Throwable t=new Throwable("Trace dump");
		XposedBridge.log(t);
	}
	
	public static int getLineNo(){
		return Thread.currentThread().getStackTrace()[3].getLineNumber();
	} 
	
	public static int getLineNo(int depth){
		return Thread.currentThread().getStackTrace()[3+depth].getLineNumber();
	} 

	/**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
	public static int dip2px(Context context,float dpValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue*scale+0.5f);
	}
	
	public static int dip2sp(Context context,float dpValue){
		final float scale = context.getResources().getDisplayMetrics().density/
			context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (dpValue*scale+0.5f);
	}
	
	
	/**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
	public static int px2dip(Context context,float pxValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue/scale+0.5f);
	}
	/**
     * 将px值转换为sp值，保证文字大小不变
     */
	public static int px2sp(Context context,float pxValue){
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue/fontScale+0.5f);
	}
	/**
     * 将sp值转换为px值，保证文字大小不变
     */
	public static int sp2px(Context context,float spValue){
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue*fontScale+0.5f);
	}



}
