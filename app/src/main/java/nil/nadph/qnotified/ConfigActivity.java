package nil.nadph.qnotified;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;

public class ConfigActivity extends Activity{

	

	//ClazzExplorer ce;
    @Override
    protected void onCreate(Bundle savedInstanceState){
		Utils.log("OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		String str="";
		try{
			str+="SystemClassLoader:"+ClassLoader.getSystemClassLoader()+
				"\nActiveModuleVersion:"+Utils.getActiveModuleVersion()
				+"\nThisVersion:"+Utils.CURRENT_MODULE_VERSION;
		}catch(Throwable r){
			str+=r;
		}
		((TextView)findViewById(R.id.mainTextView)).setText(str);
    }
	
	@Override
	public void onPointerCaptureChanged(boolean hasCapture){
		// TODO: Implement this method
	}
	
}
