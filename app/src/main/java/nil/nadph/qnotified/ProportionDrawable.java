package nil.nadph.qnotified;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

public class ProportionDrawable extends Drawable{

	@Override
	public void draw(Canvas canvas){
		int h=canvas.getHeight();
		int w=canvas.getWidth();
		if(Gravity.LEFT==iGravity){
			int x=(int)(0.5f+fProportion*w);
			p.setColor(iDoneColor);
			canvas.drawRect(0,0,x,h,p);
			p.setColor(iUndoneColor);
			canvas.drawRect(x,0,w,h,p);
		}else{
			throw new UnsupportedOperationException("Only Gravity.LEFT is supported!");
		}
	}
	
	public void setProportion(float p){
		if(p<0f)p=0f;
		if(p>1.0f)p=1.0f;
		fProportion=p;
	}
	
	public float getProportion(){
		return fProportion;
	}

	@Override
	public void setAlpha(int alpha){
		throw new UnsupportedOperationException("Stub!");
	}

	@Override
	public void setColorFilter(ColorFilter colorFilter){
		throw new UnsupportedOperationException("Stub!");
	}

	@Override
	public int getOpacity(){
		throw new UnsupportedOperationException("Stub!");
	}

	private int iGravity;
	private int iDoneColor;
	private int iUndoneColor;
	private float fProportion;
	private Paint p;
	
	public ProportionDrawable(int doneColor,int undoneColor,int gravity,float prop){
		iGravity=gravity;
		iDoneColor=doneColor;
		iUndoneColor=undoneColor;
		fProportion=prop;
		p=new Paint();
	}
	
	
}
