package me.singleneuron.fragment;

import android.os.*;
import android.widget.*;

import androidx.preference.*;

import com.microsoft.appcenter.*;
import com.microsoft.appcenter.analytics.*;

import java.util.*;

import me.singleneuron.data.*;
import me.singleneuron.preference.*;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.util.*;

public class BugReportFragment extends PreferenceFragmentCompat {
    
    private final Map<String, String> arguments = new HashMap<>();
    private ArrayList<BugReportArguments> bugReportArgumentsList;
    
    private BugReportFragment() {}
    
    public static BugReportFragment getInstance(ArrayList<BugReportArguments> list) {
        BugReportFragment fragment = new BugReportFragment();
        fragment.bugReportArgumentsList = list;
        return fragment;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        PreferenceCategory preferenceCategory = findPreference(BugReportPreference.CATEGORY);
        preferenceCategory.removeAll();
        for (BugReportArguments bugReportArguments : bugReportArgumentsList) {
            Preference preference;
            /*if (bugReportArguments.multiple) {
                MultiSelectListPreference multiSelectListPreference = new MultiSelectListPreference(getContext());
                multiSelectListPreference.setEntries(bugReportArguments.choices);
                preference = multiSelectListPreference;
            } else {*/
            ListPreference listPreference = new ListPreference(getContext());
            listPreference.setEntryValues(bugReportArguments.choices);
            listPreference.setEntries(bugReportArguments.choices);
            preference = listPreference;
            //}
            preference.setKey(bugReportArguments.key);
            preference.setTitle(bugReportArguments.name);
            preference.setSummary(bugReportArguments.description);
            preference.setOnPreferenceChangeListener((preference1, newValue) -> {
                preference1.setSummary(String.valueOf(newValue));
                arguments.put(preference1.getKey(), String.valueOf(newValue));
                return true;
            });
            preferenceCategory.addPreference(preference);
        }
        findPreference(BugReportPreference.COMMIT).setOnPreferenceClickListener(preference -> {
            arguments.put("QQ", String.valueOf(Utils.getLongAccountUin()));
            arguments.put("App Center ID", AppCenter.getInstallId().get().toString());
            for (BugReportArguments bugReportArguments : bugReportArgumentsList) {
                if (arguments.get(bugReportArguments.key) == null || arguments.get(bugReportArguments.key).isEmpty()) {
                    Toast.makeText(getContext(), String.format("请填写%s。", bugReportArguments.name), Toast.LENGTH_LONG).show();
                    return true;
                }
            }
            Analytics.trackEvent("bugReport", arguments);
            Toast.makeText(getContext(), "成功", Toast.LENGTH_SHORT).show();
            return true;
        });
    }
    
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_bug_report);
    }
    
}
