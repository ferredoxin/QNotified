package nil.nadph.qnotified;

import android.graphics.drawable.*;
import android.graphics.*;

public class SimpleBgDrawable extends Drawable{
	private int iColor;
	private int iEdgeColor;
	private int iEdgeWidth;
	
	
	public SimpleBgDrawable(int color,int edgeColor,int edgeWidth){
		iColor=color;
		iEdgeColor=edgeColor;
		iEdgeWidth=edgeWidth;
		mPaint=new Paint();
	}
	
	private Paint mPaint;


	public Paint getPaint(){
		return mPaint;
	}

	@Override
	public void draw(Canvas canvas){
		int i=iEdgeWidth;
		int w=canvas.getWidth();
		int h=canvas.getHeight();
		if(iEdgeWidth>0){
			mPaint.setColor(iEdgeColor);
			canvas.drawRect(0,0,w,i,mPaint);
			canvas.drawRect(0,h-i,w,h,mPaint);
			canvas.drawRect(0,i,i,h-i,mPaint);
			canvas.drawRect(w-i,i,w,h-i,mPaint);
		}
		mPaint.setColor(iColor);
		canvas.drawRect(i,i,w-i,h-i,mPaint);
	}

	@Override
	public void setAlpha(int alpha){
		// TODO: Implement this method
	}

	@Override
	public void setColorFilter(ColorFilter colorFilter){
		// TODO: Implement this method
	}

	@Override
	public int getOpacity(){
		// TODO: Implement this method
		return 0;
	}

}
