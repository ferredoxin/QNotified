package nil.nadph.qnotified.util;
import android.content.*;

public class ArscKit {
	
	
	
	public static int getIdentifier(Context ctx,String type,String name){
		String pkg=ctx.getPackageName();
		int ret=ctx.getResources().getIdentifier(name,type,pkg);
		if(ret!=0)return ret;
		
		
	}
}
