package nil.nadph.qnotified.util;

import android.app.Application;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import nil.nadph.qnotified.BuildConfig;
import nil.nadph.qnotified.config.ConfigManager;

import static nil.nadph.qnotified.util.Utils.getApplication;

public class CliOper {
    private static boolean sInit = false;

    public static void __init__(Application app, boolean mustInit) {
        if (app == null) return;
        if (sInit) return;
        if (BuildConfig.DEBUG) return;

        long longAccount = Utils.getLongAccountUin();
        if (longAccount!=-1) {
            AppCenter.setUserId(String.valueOf(longAccount));
        }

        if (!Crashes.isEnabled().get()) {
            AppCenter.start(app, "ddf4b597-1833-45dd-af28-96ca504b8123", Crashes.class);
        }

        ConfigManager configManager = ConfigManager.getDefaultConfig();
        final String LAST_TRACE_DATA_CONFIG = "lastTraceDate";
        final String format = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        String nowTime = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        if (!mustInit) {
            String oldTime = configManager.getString(LAST_TRACE_DATA_CONFIG);
            if (oldTime != null&&oldTime.equals(nowTime)) {
                if (Analytics.isEnabled().get()) {
                    Analytics.setEnabled(false);
                }
                return;
            }
        }
        configManager.putString(LAST_TRACE_DATA_CONFIG, nowTime);

        sInit = true;
        AppCenter.start(app, "ddf4b597-1833-45dd-af28-96ca504b8123", Analytics.class);
        Analytics.setEnabled(true);
    }

    public static void onLoad() {
        CliOper.__init__(getApplication(),false);
        final String LAST_TRACE_HASHCODE_CONFIG = "lastTraceHashcode";
        ConfigManager configManager = ConfigManager.getDefaultConfig();
        Integer oldHashCode = null;
        try {
            oldHashCode = (Integer) configManager.getObject(LAST_TRACE_HASHCODE_CONFIG);
        } catch (Exception e) {
            Utils.log(e);
        }
        HashMap<String, String> properties = new HashMap<>();
        properties.put("versionName", Utils.QN_VERSION_NAME);
        properties.put("versionCode", String.valueOf(Utils.QN_VERSION_CODE));
        properties.put("Auth2Status", String.valueOf(LicenseStatus.getAuth2Status()));
        Integer newHashCode = properties.hashCode();
        if (oldHashCode!=null&&oldHashCode.equals(newHashCode)) {
            return;
        }
        try {
            configManager.putObject(LAST_TRACE_HASHCODE_CONFIG, newHashCode);
            configManager.save();
        } catch (Exception e) {
            //ignored
        }
        Analytics.trackEvent("onLoad", properties);
    }

    public static void passAuth2Once(int retryCount, int chiralCount) {
        __init__(Utils.getApplication(),true);
        Map<String, String> prop = new HashMap<>();
        prop.put("retryCount", String.valueOf(retryCount));
        prop.put("chiralCount", String.valueOf(chiralCount));
        Analytics.trackEvent("passAuth2Once", prop);
    }

    public static void abortAuth2Once(int retryCount) {
        /*__init__(Utils.getApplication());
        Map<String, String> prop = new HashMap<>();
        prop.put("retryCount", String.valueOf(retryCount));
        Analytics.trackEvent("abortAuth2Once", prop);*/
    }

    public static void revokeAuth2Once() {
        /*__init__(Utils.getApplication());
        Map<String, String> prop = new HashMap<>();
        prop.put("isAuth2Whitelist", String.valueOf(LicenseStatus.isBypassAuth2()));
        Analytics.trackEvent("revokeAuth2Once", prop);*/
    }

    public static void copyCardMsg(String msg) {
        if (msg == null) return;
        __init__(Utils.getApplication(),true);
        try {
            Analytics.trackEvent("copyCardMsg", digestCardMsg(msg));
        } catch (Throwable e) {
            Utils.log(e);
        }
    }

    public static void sendCardMsg(long uin, String msg) {
        if (msg == null) return;
        __init__(Utils.getApplication(),true);
        try {
            Map<String, String> prop = digestCardMsg(msg);
            prop.put("uin", String.valueOf(uin));
            Analytics.trackEvent("sendCardMsg", prop);
        } catch (Throwable e) {
            Utils.log(e);
        }
    }

    public static void batchSendMsg(long uin, String msg, int count) {
        if (msg == null) return;
        Map<String, String> properties = new HashMap<>();
        if (msg.length() > 127) {
            msg = msg.substring(0, 127);
        }
        properties.put("msg", msg);
        properties.put("uin", String.valueOf(uin));
        properties.put("count", String.valueOf(count));
        __init__(Utils.getApplication(),true);
        Analytics.trackEvent("batchSendMsg", properties);
    }

    private static Map<String, String> digestCardMsg(String msg) {
        Map<String, String> prop = new HashMap<>();
        if (msg.startsWith("<")) {
            //xml
            prop.put("type", "xml");
            prop.put("serviceID", findXmlValueOrEmpty(msg, "serviceID"));
            prop.put("templateID", findXmlValueOrEmpty(msg, "templateID"));
            prop.put("action", findXmlValueOrEmpty(msg, "action"));
            prop.put("brief", findXmlValueOrEmpty(msg, "brief"));
            prop.put("name", findXmlValueOrEmpty(msg, "name"));
        } else if (msg.startsWith("{")) {
            //json
            prop.put("type", "json");
            prop.put("app", findJsonValueOrEmpty(msg, "app"));
            prop.put("desc", findJsonValueOrEmpty(msg, "desc"));
            prop.put("prompt", findJsonValueOrEmpty(msg, "prompt"));
            prop.put("appID", findJsonValueOrEmpty(msg, "appID"));
            prop.put("text", findJsonValueOrEmpty(msg, "text"));
            prop.put("actionData", findJsonValueOrEmpty(msg, "actionData"));
        } else {
            if (msg.length() > 127) {
                msg = msg.substring(0, 127);
            }
            prop.put("type", "unknown");
            prop.put("raw", msg);
        }
        return prop;
    }

    @Deprecated
    private static String findJsonValueOrEmpty(String raw, String key) {
        if (key == null || raw == null) return "";
        key = '"' + key + '"';
        raw = raw.replace(" ", "");
        if (!raw.contains(key)) return "";
        int limit = raw.indexOf(key);
        int start = raw.indexOf(':', limit);
        int e1 = raw.indexOf(',', start);
        int e2 = raw.indexOf('}', start);
        int end;
        if (e1 * e2 == 1) return "";
        if (e1 * e2 < 0) {
            if (e1 == -1) end = e2;
            else end = e1;
        } else {
            end = Math.min(e1, e2);
        }
        String subseq = raw.substring(start + 1, end);
        if (subseq.startsWith("\"")) {
            int e3 = raw.indexOf('"', start);
            int stop = indexMax(end, e3);
            if ((raw.charAt(stop) == ',' || raw.charAt(stop) == '}') && raw.charAt(stop - 1) == '"') {
                return raw.substring(start + 2, stop - 1);//exclude '"'
            } else {
                return raw.substring(start + 1, stop);
            }
        } else {
            return subseq;
        }
    }

    @Deprecated
    private static String findXmlValueOrEmpty(String raw, String key) {
        if (key == null || raw == null) return "";
        raw = raw.replace('\'', '"').replace(" ", "");
        if (!raw.contains(key)) return "";
        int limit = raw.indexOf(key);
        int start = raw.indexOf('"', limit);
        int end = raw.indexOf('"', start + 1);
        if (start == -1 || end == -1) return "";
        return raw.substring(start + 1, end);
    }

    public static int indexMax(int a, int b) {
        if (a < 0) return b;
        if (b < 0) return a;
        return Math.max(a, b);
    }

    public static int indexMin(int a, int b) {
        if (a < 0) return b;
        if (b < 0) return a;
        return Math.min(a, b);
    }

    public static void enterModuleActivity(String shortName) {
        /*try {
            __init__(Utils.getApplication());
            Map<String, String> prop = new HashMap<>();
            prop.put("name", shortName);
            Analytics.trackEvent("enterModuleActivity", prop);
        } catch (Throwable ignored) {
        }*/
    }
}
