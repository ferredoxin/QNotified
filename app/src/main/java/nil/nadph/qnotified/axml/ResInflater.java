package nil.nadph.qnotified.axml;

import android.app.Application;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.StateSet;
import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Field;

import static nil.nadph.qnotified.Initiator.load;
import static nil.nadph.qnotified.Utils.*;

public class ResInflater{
	/**
     * Creates a ColorStateList from ANY XmlPullParser using given a set of
     * {@link Resources} and a {@link Resources.Theme}.
     *
     * @param r Resources against which the ColorStateList should be inflated.
     * @param parser Parser for the XML document defining the ColorStateList.
     * @param theme Optional theme to apply to the color state list, may be
     *              {@code null}.
     * @return A new color state list.
     */
    @NonNull
    public static ColorStateList inflateColorFromXml(@NonNull Resources r,@NonNull XmlPullParser parser,
													 @Nullable Resources.Theme theme) throws XmlPullParserException, IOException{
        final AttributeSet attrs = Xml.asAttributeSet(parser);

        int type;
        while((type=parser.next())!=XmlPullParser.START_TAG
			  &&type!=XmlPullParser.END_DOCUMENT){
            // Seek parser to start tag.
        }

        if(type!=XmlPullParser.START_TAG){
            throw new XmlPullParserException("No start tag found");
        }

		final String name = parser.getName();
		if(!name.equals("selector")){
			throw new XmlPullParserException(
				parser.getPositionDescription()+": invalid color state list tag "+name);
		}


		final int innerDepth = parser.getDepth()+1;
        int depth;
		// int type;

        int changingConfigurations = 0;
        int defaultColor = Color.RED;

        boolean hasUnresolvedAttrs = false;

        int[][] stateSpecList = ArrayUtils.newUnpaddedArray(int[].class,20);
        int[][] themeAttrsList = new int[stateSpecList.length][];
        int[] colorList = new int[stateSpecList.length];
        int listSize = 0;

        while((type=parser.next())!=XmlPullParser.END_DOCUMENT
			  &&((depth=parser.getDepth())>=innerDepth||type!=XmlPullParser.END_TAG)){
            if(type!=XmlPullParser.START_TAG||depth>innerDepth
			   ||!parser.getName().equals("item")){
                continue;
            }
			Class clz=null;
			try{
				clz=Application.class.getClassLoader().loadClass("android.R$styleable");
			}catch(ClassNotFoundException e){}
			Field[] f=clz.getDeclaredFields();
			int[] ret=(int[])sget_object(clz,"ColorStateListItem");
			
            final TypedArray a = Resources.obtainAttributes(r,theme,attrs,ret
															);
            final int[] themeAttrs = a.extractThemeAttrs();
            final int baseColor = a.getColor(((Integer)sget_object(load("android.R$styleable"),"ColorStateListItem_color")).intValue(),Color.MAGENTA);
            final float alphaMod = a.getFloat(((Integer)sget_object(load("android.R$styleable"),"ColorStateListItem_alpha")).intValue(),1.0f);

            changingConfigurations|=a.getChangingConfigurations();

            a.recycle();

            // Parse all unrecognized attributes as state specifiers.
            int j = 0;
            final int numAttrs = attrs.getAttributeCount();
            int[] stateSpec = new int[numAttrs];
            for(int i = 0; i<numAttrs; i++){
                final int stateResId = attrs.getAttributeNameResource(i);
                switch(stateResId){
                    case android.R.attr.color:
                    case android.R.attr.alpha:
                        // Recognized attribute, ignore.
                        break;
                    default:
                        stateSpec[j++]=attrs.getAttributeBooleanValue(i,false)
							? stateResId :-stateResId;
                }
            }
            stateSpec=StateSet.trimStateSet(stateSpec,j);

            // Apply alpha modulation. If we couldn't resolve the color or
            // alpha yet, the default values leave us enough information to
            // modulate again during applyTheme().
            final int color = modulateColorAlpha(baseColor,alphaMod);
            if(listSize==0||stateSpec.length==0){
                defaultColor=color;
            }

            if(themeAttrs!=null){
                hasUnresolvedAttrs=true;
            }

            colorList=GrowingArrayUtils.append(colorList,listSize,color);
            themeAttrsList=GrowingArrayUtils.append(themeAttrsList,listSize,themeAttrs);
            stateSpecList=GrowingArrayUtils.append(stateSpecList,listSize,stateSpec);
            listSize++;
        }
		
		int[]mColors = new int[listSize];
        int[][] mStateSpecs = new int[listSize][];
        System.arraycopy(colorList,0,mColors,0,listSize);
        System.arraycopy(stateSpecList,0,mStateSpecs,0,listSize);
		final ColorStateList colorStateList = new ColorStateList(mStateSpecs,mColors);
		try{
			iput_object(colorStateList,"mChangingConfigurations",changingConfigurations);
			iput_object(colorStateList,"mDefaultColor",defaultColor);
			if(hasUnresolvedAttrs){
				int [][]mThemeAttrs = new int[listSize][];
				System.arraycopy(themeAttrsList,0,mThemeAttrs,0,listSize);
				iput_object(colorStateList,"mThemeAttrs",mThemeAttrs);
			}else{
				iput_object(colorStateList,"mThemeAttrs",null);
			}
			invoke_virtual(colorStateList,"onColorsChanged");
		}catch(Exception e){}
		return colorStateList;
    }


	private static int modulateColorAlpha(int baseColor,float alphaMod){
        if(alphaMod==1.0f){
            return baseColor;
        }

        final int baseAlpha = Color.alpha(baseColor);
        final int alpha = MathUtils.constrain((int) (baseAlpha*alphaMod+0.5f),0,255);
        return (baseColor&0xFFFFFF)|(alpha<<24);
    }

}
