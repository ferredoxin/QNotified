package nil.nadph.qnotified.ipc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import libcore.io.Libcore;

import java.io.*;
import java.util.UUID;

import static nil.nadph.qnotified.util.Utils.*;

@SuppressLint("PrivateApi")
public class SyncUtils {

    public static void startServerSocket() {
        getSocketUuid();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = getSocketUuid();
                try {
                    LocalSocketAddress addr = new LocalSocketAddress(name, LocalSocketAddress.Namespace.ABSTRACT);
                    Object impl = new_instance(Context.class.getClassLoader().loadClass("android.net.LocalSocketImpl"));
                    invoke_virtual(impl, "create", LocalSocket.SOCKET_DGRAM, int.class);
                    invoke_virtual(impl, "bind", addr, LocalSocketAddress.class);
                    FileDescriptor fd= (FileDescriptor) iget_object_or_null(impl,"fd");

                } catch (Exception e) {
                    log(e);
                }

            }
        }).start();
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
        Context ctx = getApplication();
        File f = new File(ctx.getFilesDir(), "qnotified_uuid");
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

    public static int getUid() {
        return Libcore.os.getuid();
    }

}
