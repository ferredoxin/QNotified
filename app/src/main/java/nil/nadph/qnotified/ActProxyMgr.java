package nil.nadph.qnotified;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Stack;

import static nil.nadph.qnotified.Utils.log;
/* ActivityProxyManager */
public class ActProxyMgr{

	public static final String STUB_ACTIVITY="com/tencent/mobileqq/activity/photo/CameraPreviewActivity";

	/**
	 * @field HashSet mThreads<Long threadId> :Ids of threads which is calling invokeSuper
	 * You may wonder what this field is for,
	 * This is to suppress Xposed's endless hook recursion
	 * beforeHookMethod->invokeSuper->beforeHookedMethod->invokeSuper->...->StackOverflow :p 
	 * see isInfiniteLoop():Z 
	 **/
	private HashMap<Long,Stack<Member>> mThreadStack=new HashMap<>();
	private HashMap<Member,StackBreakHook> hooks=new HashMap<>();;

	private int next_uuid=1;

	public static int next(){
		return instance.next_uuid++;
	}
	/*
	 public static Activity get(int i){
	 return instance.activities.get(i);
	 }*

	 public static void set(int i,Activity act){
	 //instance.activities.put(i,act);
	 }

	 public static Activity remove(int i){
	 //return instance.activities.remove(i);
	 return null;
	 }*/

	public static boolean isInfiniteLoop(){
		try{
			long tid=Thread.currentThread().getId();
			Stack<Member> s=instance.mThreadStack.get(tid);
			return !s.empty();
		}catch(Throwable e){
			//.log(e);
			return false;
		}
	}


	/**
	 * method invokeSuper
	 * @param obj: thisObject,should be null if it's a static method
	 * @param smethod: the SUPER method(you must get it from its super class) you want to invoke
	 * @param args
	 * @return the return value(if it has,or null)
	 * //@throws BreakUncaughtException:the super method was invoked,but the return value was lost
	 * TODO: Replace this fragile method with JNI
	 **/
	@Deprecated 
	public static Member invokeSuper(android.app.Activity obj, Method smethod, Object...args) throws Throwable{
		try{
			synchronized(instance){
				StackBreakHook hook=instance.hooks.get(smethod);
				if(hook==null){
					hook=new StackBreakHook(smethod);
					instance.hooks.put(smethod,hook);
					XposedBridge.hookMethod(smethod,hook);
				}
				smethod.setAccessible(true);
				Stack<Member> stack=instance.mThreadStack.get(Thread.currentThread().getId());
				if(stack==null){
					stack=new Stack<>();
					instance.mThreadStack.put(Thread.currentThread().getId(),stack);
				}
				//log("invokeSuper("+smethod.getName()+"),tid="+Thread.currentThread().getId()+",stack_before="+stack);
				stack.push(smethod);
			}
			smethod.invoke(obj,args);
			//XposedBridge.invokeOriginalMethod(smethod,obj,args);This doesn't work!!!
		}catch(InvocationTargetException e){
			if(e.getCause() instanceof StackBreak){
				//Utils.log("success!");
				//return ((StackBreak)e.getCause()).returnValue;
			}else throw e;
		}
		finally{
			synchronized(instance){
				/*if(unhook!=null)unhook.unhook();*/
				Stack<Member> s=instance.mThreadStack.get(Thread.currentThread().getId());
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
		public Member method;
		public StackBreakHook(Member m){
			super(65535);
			method=m;
		}

		@Override
		protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable{
			try{
				//Utils.log("setEx");
				if(param.hasThrowable())return;//Sth has already went wrong;don't cope with it
				long threadId=Thread.currentThread().getId();//make sure shit won't happen
				synchronized(instance){
					Stack s=instance.mThreadStack.get(threadId);
					//log("invokeSuper_end("+method.getName()+"),tid="+Thread.currentThread().getId()+",stack="+s);
					if(s.empty())return;
					if(!method.equals(s.peek()))return;
					StackBreak sb=new StackBreak(param.getResult());
					s.pop();
					s.push(param.getResult());
					param.setThrowable(sb);
				}
			}catch(Throwable e){
				log(e);
				throw e;
			}
		}

	}
}
