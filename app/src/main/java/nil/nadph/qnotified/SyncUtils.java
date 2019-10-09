package nil.nadph.qnotified;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import libcore.io.Libcore;

import java.io.*;
import java.util.UUID;

import static nil.nadph.qnotified.util.Utils.*;
import android.os.*;
import android.content.*;
import nil.nadph.qnotified.record.*;
import android.app.*;
import java.util.*;
import nil.nadph.qnotified.hook.*;

@SuppressLint("PrivateApi")
public class SyncUtils {

	public static final int PROC_ERROR=0;
	public static final int PROC_MAIN=1 << 0;
	public static final int PROC_MSF=1 << 1;
	public static final int PROC_PEAK=1 << 2;
	public static final int PROC_TOOL=1 << 3;
	public static final int PROC_QZONE=1 << 4;
	public static final int PROC_VIDEO=1 << 5;
	public static final int PROC_MINI=1 << 6;
	public static final int PROC_LOLA=1 << 7;

	public static final int PROC_OTHERS=1 << 31;

	public static int myId=0;
	private static int mProcType=0;

	//file=0 
	public static final String SYNC_FILE_CHANGED="nil.nadph.qnotified.SYNC_FILE_CHANGED";

	//process=010001 hook=0011000
	public static final String HOOK_DO_INIT="nil.nadph.qnotified.HOOK_DO_INIT";

	public static int seq=0;

	public static boolean inited=false;

    public static void initBroadcast(Context ctx) {
		if (inited)return;
		BroadcastReceiver recv=new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				switch (intent.getAction()) {
					case SYNC_FILE_CHANGED:
						int id=intent.getIntExtra("id", -1);
						int file=intent.getIntExtra("file", -1);
						if (id != -1 && id != myId && file == 1) {
							try {
								ConfigManager.getDefault().setDirtyFlag();
							} catch (IOException e) {}
						}
						break;
					case HOOK_DO_INIT:
						int myType=getProcessType();
						int targetType=intent.getIntExtra("process", 0);
						int hookId=intent.getIntExtra("hook", -1);
						if (hookId != -1 && (myType & targetType) != 0) {
							BaseDelayableHook hook=BaseDelayableHook.getHookByType(hookId);
							log("Remote: recv init "+hook);
							if (hook != null)hook.init();
						}
						break;
				}
			}
		};
		IntentFilter filter=new IntentFilter();
		filter.addAction(SYNC_FILE_CHANGED);
		filter.addAction(HOOK_DO_INIT);
		ctx.registerReceiver(recv, filter);
		inited = true;
		log("Proc:  " + Libcore.os.getpid() + "/" + getProcessType() + "/" + getProcessName());
	}

	public static void onFileChanged(int file) {
		Context ctx=getApplication();
		Intent changed=new Intent(SYNC_FILE_CHANGED);
		changed.setPackage(ctx.getPackageName());
		initId();
		changed.putExtra("id", myId);
		changed.putExtra("file", file);
		ctx.sendBroadcast(changed);
	}

	public static void requestInitHook(int process, int hookId) {
		Context ctx=getApplication();
		Intent changed=new Intent(HOOK_DO_INIT);
		changed.setPackage(ctx.getPackageName());
		initId();
		changed.putExtra("process", process);
		changed.putExtra("hook", hookId);
		ctx.sendBroadcast(changed);
	}

	public static int getProcessType() {
		if (mProcType != 0)return mProcType;
		String[] parts=getProcessName().split(":");
		if (parts.length == 1) {
			if (parts[0].equals("unknown")) {
				return PROC_MAIN;
			} else {
				mProcType = PROC_MAIN;
			}
		} else {
			String tail=parts[parts.length - 1];
			switch (tail) {
				case "MSF":
					mProcType = PROC_MSF;
					break;
				case "peak":
					mProcType = PROC_PEAK;
					break;
				case "tool":
					mProcType = PROC_TOOL;
					break;
				case "qzone":
					mProcType = PROC_QZONE;
					break;
				case "video":
					mProcType = PROC_VIDEO;
					break;
				case "mini":
					mProcType = PROC_MINI;
					break;
				case "lola":
					mProcType = PROC_LOLA;
					break;
				default:
					mProcType = PROC_OTHERS;
					break;
			}
		}
		return mProcType;
	}

	public static String getProcessName() {
		String name = "unknown";
		int retry = 0;
		do {
			try {
				List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) getApplication().getSystemService("activity")).getRunningAppProcesses();
				if (runningAppProcesses != null) {
					for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
						if (runningAppProcessInfo != null && runningAppProcessInfo.pid == android.os.Process.myPid()) {
							return runningAppProcessInfo.processName;
						}
					}
				}
				/*FileInputStream fin = new FileInputStream("/proc/" + android.os.Process.myPid() + "/cmdline");
				 byte[] b = new byte[64];
				 int len = fin.read(b, 0, b.length);
				 fin.close();
				 String procName = new String(b, 0, len).trim();
				 //XposedBridge.log(procName);*/
			} catch (Throwable e) {
				log("getProcessName error " + e);
			}
			retry++;
			if (retry >= 3) {
				break;
			}
		} while ("unknown".equals(name));
		return name;
	}


	/*
	 public static synchronized String getSocketUuid() {
	 Context ctx = getApplication();
	 File f = new File(ctx.getFilesDir(), "nil_nadph_uuid");
	 try {
	 if (f.exists()) {
	 FileInputStream fin = new FileInputStream(f);
	 byte[] buf = new byte[20];
	 int l = fin.read(buf);
	 fin.close();
	 String str = new String(buf, 0, l)
	 .replace("\r", "").replace("\n", "").replace(" ", "").replace("\t", "");
	 if (str.length() > 4) return str;
	 }
	 String uuid = UUID.randomUUID().toString().replace("\r", "").replace("\n", "").replace(" ", "").replace("\t", "");
	 if (!f.exists()) f.createNewFile();
	 FileOutputStream fout = new FileOutputStream(f);
	 fout.write(uuid.getBytes());
	 fout.flush();
	 fout.close();
	 return uuid;
	 } catch (IOException e) {
	 throw new RuntimeException("Unable to allocate uuid");
	 }
	 }
	 */
    public static int getUid() {
        return Libcore.os.getuid();
    }

	public static void initId() {
		if (myId == 0) {
			myId = (int)((Math.random()) * (Integer.MAX_VALUE / 4));
		}
	}

}
