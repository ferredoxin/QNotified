package nil.nadph.qnotified;

import java.util.*;
import android.app.*;
import java.lang.reflect.*;
import de.robv.android.xposed.*;
import de.robv.android.xposed.XC_MethodHook.*;

/* ActivityProxyManager */
public class ActProxyMgr{
	
	//
	public static final String DUMMY_ACTIVITY="com/tencent/mobileqq/activity/photo/CameraPreviewActivity";
	//private HashMap<Integer,Activity> activities=new HashMap();
	
	/* @field HashSet mThreads<Long threadId> :Ids of threads which is calling invokeSuper
	 * You may wonder what this field is for,
	 * This is to suppress Xposed's endless hook recursion
	 * beforeHookMethod->invokeSuper->beforeHookedMethod->invokeSuper->...->StackOverflow :p 
	 * see hasIgnored():Z */
	private HashMap<Long,Stack> mThreadStack=new HashMap();
	
	
	private int next_uuid=1;
	
	public static int next(){
		return instance.next_uuid++;
	}
	/*
	public static Activity get(int i){
		return instance.activities.get(i);
	}*/
	
	public static void set(int i,Activity act){
		//instance.activities.put(i,act);
	}
	
	public static Activity remove(int i){
		//return instance.activities.remove(i);
		return null;
	}
	
	public static boolean isInfiniteLoop(){
		try{
			long tid=Thread.currentThread().getId();
			Stack s=instance.mThreadStack.get(tid);
			return !s.empty();
		}catch(Throwable e){
			//XposedBridge.log(e);
			return false;
		}
	}
	
	
	/* @method invokeSuper
	 * @param obj: thisObject,should be null if it's a static method
	 * @param smathod: the SUPER method(you must get it from its super class) you want to invoke
	 * @param args
	 * @return the return value(if it has,or null)
	 * @throws BreakUncaughtException:the super method was invoked,but the return value was lost
	 * TODO: Replace this fragile method with JNI
	 */
	@Deprecated 
	public static Object invokeSuper(Object obj,Method smethod,Object...args) throws Throwable{
		XC_MethodHook.Unhook unhook=null;
		try{
			StackBreakHook hook=new StackBreakHook(Thread.currentThread().getId());
			unhook=XposedBridge.hookMethod(smethod,hook);
			smethod.setAccessible(true);
			synchronized(instance){
				Stack stack=instance.mThreadStack.get(Thread.currentThread().getId());
				if(stack==null){
					stack=new Stack();
					instance.mThreadStack.put(Thread.currentThread().getId(),stack);
				}
				stack.push(hook);
			}
			smethod.invoke(obj,args);
			//XposedBridge.invokeOriginalMethod(smethod,obj,args);This doesn't work!!!
		}catch(InvocationTargetException e){
			if(e.getCause() instanceof StackBreak){
				//Utils.log("success!");
				//return ((StackBreak)e.getCause()).returnValue;
			}
			else throw e;
		}finally{
			synchronized(instance){
				if(unhook!=null)unhook.unhook();
				Stack s=instance.mThreadStack.get(Thread.currentThread().getId());
				return s.pop();
			}
		}
		//throw new BreakUnaughtException();
	}
	
	
	private static final ActProxyMgr instance=new ActProxyMgr();
	
	
	public static class StackBreak extends Throwable{/* This is NOT Exception,to be caught! */
		public StackBreak(Object ret){
			returnValue=ret;
		}
		public Object returnValue;
	}
	public static class BreakUnaughtException extends Exception{}
	private static class StackBreakHook extends XC_MethodHook{
		public long threadId;

		public StackBreakHook(long tid){
			super(65535);
			threadId=tid;
		}
		
		@Override
		protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable{
			//Utils.log("setEx");
			if(param.hasThrowable())return;//Sth has already went wrong;don't cope with it
			if(threadId!=Thread.currentThread().getId())return;//make sure shit won't happen
			synchronized(instance){
				Stack s=instance.mThreadStack.get(Thread.currentThread().getId());
				if(s.peek()!=this)return;
				StackBreak sb=new StackBreak(param.getResult());
				s.pop();
				s.push(param.getResult());
				param.setThrowable(sb);
			}
		}
		
	}
}
