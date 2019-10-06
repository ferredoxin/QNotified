package nil.nadph.qnotified.ipc;

import java.io.*;

import static nil.nadph.qnotified.util.Utils.*;
import android.content.*;
import java.util.*;
import android.net.*;

public class SyncUtils {
	
	public static void startServerSocket(){
		String name=getSocketUuid();
		try {
			LocalServerSocket server=new LocalServerSocket(name);
			server.accept().
		} catch (IOException e) {
			log(e);
		}
	}

	public static boolean isMainProcess() {
		try {
			FileInputStream fin = new FileInputStream("/proc/" + android.os.Process.myPid() + "/cmdline");
			byte[] b = new byte[64];
			int len = fin.read(b, 0, b.length);
			fin.close();
			String procName = new String(b, 0, len).trim();
			return !procName.contains(":");
		} catch (IOException e) {
			log(e);
			//should not happen
			return true;
		}
	}

	public static synchronized String getSocketUuid() {
		Context ctx=getApplication();
		File f=new File(ctx.getFilesDir(), "qnotified_uuid");
		try {
			if (f.exists()) {
				FileInputStream fin=new FileInputStream(f);
				byte[]buf=new byte[20];
				int l=fin.read(buf);
				fin.close();
				String str=new String(buf, 0, l)
					.replace("\r", "").replace("\n", "").replace(" ", "").replace("\t", "");
				if (str.length() > 4)return str;
			}
			String uuid=UUID.randomUUID().toString().replace("\r", "").replace("\n", "").replace(" ", "").replace("\t", "");;
			if (!f.exists())f.createNewFile();
			FileOutputStream fout=new FileOutputStream(f);
			fout.write(uuid.getBytes());
			fout.flush();
			fout.close();
			return uuid;
		} catch (IOException e) {
			throw new RuntimeException("Unable to allocate uuid");
		}
	}

	public static int getUid(){
		FileInputStream fin = new FileInputStream("/proc/" + android.os.Process.myPid() + "/status");
		Scanner scan=new Scanner(fin);
		String line;
		while((line=scan.nextLine())!=null){
			if(line.startsWith("Uid")){
				line=line.replace(
			}
		}
	}
	
}
