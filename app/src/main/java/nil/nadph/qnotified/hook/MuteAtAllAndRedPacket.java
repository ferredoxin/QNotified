package nil.nadph.qnotified.hook;
import nil.nadph.qnotified.ipc.*;


import static nil.nadph.qnotified.util.Utils.*;
import static nil.nadph.qnotified.util.Initiator.load;
import nil.nadph.qnotified.record.*;
import de.robv.android.xposed.*;
import nil.nadph.qnotified.util.*;
public class MuteAtAllAndRedPacket extends BaseDelayableHook{
		private MuteAtAllAndRedPacket() {
		}

		private static final MuteAtAllAndRedPacket self = new MuteAtAllAndRedPacket();

		public static MuteAtAllAndRedPacket get() {
			return self;
		}

		private boolean inited = false;

		@Override
		public boolean init() {
			if (inited) return true;
			try {
				Class cl_MessageInfo = load("com/tencent/mobileqq/troop/data/MessageInfo");
				if (cl_MessageInfo == null) {
					Class c = load("com/tencent/mobileqq/data/MessageRecord");
					cl_MessageInfo = c.getDeclaredField("mMessageInfo").getType();
				}
				/* @author qiwu */
				final int at_all_type = (Utils.getHostInfo(getApplication()).versionName.compareTo("7.8.0") >= 0) ? 13 : 12;
				XposedHelpers.findAndHookMethod(cl_MessageInfo, "a", load("com/tencent/mobileqq/app/QQAppInterface"), boolean.class, String.class, new XC_MethodHook(60) {
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable {
							int ret = (int) param.getResult();
							String troopuin = (String) param.args[2];
							if (ret != at_all_type) return;
							String muted = "," + ConfigManager.getDefault().getString(qn_muted_at_all) + ",";
							if (muted.contains("," + troopuin + ",")) {
								param.setResult(0);
							}
						}
					});
			} catch (Exception e) {
				log(e);
			}
			try {
				XposedHelpers.findAndHookMethod(load("com.tencent.mobileqq.data.MessageForQQWalletMsg"), "doParse", new XC_MethodHook(200) {
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable {
							boolean mute = false;
							int istroop = (Integer) iget_object_or_null(param.thisObject, "istroop");
							if (istroop != 1) return;
							String troopuin = (String) iget_object_or_null(param.thisObject, "frienduin");
							String muted = "," + ConfigManager.getDefault().getString(qn_muted_red_packet) + ",";
							if (muted.contains("," + troopuin + ",")) mute = true;
							if (mute) XposedHelpers.setObjectField(param.thisObject, "isread", true);
						}
					});
				inited = true;
				return true;
			} catch (Throwable e) {
				log(e);
				return false;
			}
		}

		@Override
		public boolean checkPreconditions() {
			return true;
		}

		@Override
		public int getEffectiveProc() {
			return SyncUtils.PROC_MAIN|SyncUtils.PROC_MSF;
		}

		@Override
		public int[] getPreconditions() {
			return new int[0];
		}

		@Override
		public boolean isInited() {
			return inited;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}
}
