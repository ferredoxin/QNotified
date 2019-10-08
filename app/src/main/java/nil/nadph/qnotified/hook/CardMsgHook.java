package nil.nadph.qnotified.hook;


import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static nil.nadph.qnotified.ActProxyMgr.*;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;
import java.lang.reflect.*;
import de.robv.android.xposed.*;
import nil.nadph.qnotified.record.*;
import android.content.*;
import android.widget.*;
import android.view.*;
import nil.nadph.qnotified.ui.*;
import nil.nadph.qnotified.ipc.*;
import android.annotation.*;
import android.os.*;
import android.graphics.*;


public class CardMsgHook extends BaseDelayableHook{
	private CardMsgHook() {
    }

    private static final CardMsgHook self = new CardMsgHook();

    public static CardMsgHook get() {
        return self;
    }

    private boolean inited = false;

    @Override
    public boolean init() {
        if (inited) return true;
		Class cl_BaseBubbleBuilder = load("com.tencent.mobileqq.activity.aio.BaseBubbleBuilder");
		Class cl_ChatMessage = load("com.tencent.mobileqq.data.ChatMessage");
		Class cl_BaseChatItemLayout = load("com.tencent.mobileqq.activity.aio.BaseChatItemLayout");
		assert cl_BaseBubbleBuilder != null;
		assert cl_ChatMessage != null;
		assert cl_BaseChatItemLayout != null;
		Method[] ms = cl_BaseBubbleBuilder.getDeclaredMethods();
		Method m = null;
		Class[] argt;
		for (int i = 0; i < ms.length; i++) {
			argt = ms[i].getParameterTypes();
			if (argt.length != 6) continue;
			if (argt[0].equals(cl_ChatMessage) && argt[1].equals(Context.class)
				&& argt[2].equals(cl_BaseChatItemLayout) && argt[4].equals(int.class)
				&& argt[5].equals(int.class)) {
				m = ms[i];
			}
		}
		XposedBridge.hookMethod(m, new XC_MethodHook(51) {
                private static final int R_ID_BB_LAYOUT = 0x300AFF41;
                private static final int R_ID_BB_TEXTVIEW = 0x300AFF42;

                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    if (ConfigManager.getDefault().getBooleanOrFalse(qn_send_card_msg)) {
                        final Object msgObj = methodHookParam.args[0];
                        ViewGroup viewGroup = (ViewGroup) methodHookParam.args[2];
                        if (!load("com.tencent.mobileqq.data.MessageForStructing").isAssignableFrom(msgObj.getClass())
							&& !load("com.tencent.mobileqq.data.MessageForArkApp").isAssignableFrom(msgObj.getClass()))
                            return;
                        if (viewGroup.findViewById(R_ID_BB_LAYOUT) == null) {
                            Context context = viewGroup.getContext();
                            LinearLayout linearLayout = new LinearLayout(context);
                            linearLayout.setId(R_ID_BB_LAYOUT);
                            //linearLayout.setBackground(new DebugDrawable(context));//SimpleBgDrawable(0x00000000, Color.BLUE, dip2px(context, 1)));
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                            lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                            TextView textView = new TextView(context);
                            textView.setId(R_ID_BB_TEXTVIEW);
                            textView.setGravity(Gravity.CENTER);
                            textView.setTextColor(Color.RED);
                            textView.setText("长按复制");
                            linearLayout.addView(textView, lp);
                            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
                            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            //rlp.addRule(RelativeLayout.ALIGN_PARENT_TO
                            int i = dip2px(context, 2);
                            rlp.setMargins(i, i, i, i);
                            //linearLayout.hashCode();
                            viewGroup.addView(linearLayout, rlp);
                            //iput_object(viewGroup,"DEBUG_DRAW",true);
                        }
                        ((TextView) viewGroup.findViewById(R_ID_BB_TEXTVIEW)).setOnLongClickListener(new View.OnLongClickListener() {
								@Override
								public boolean onLongClick(View view) {
									try {
										ClipboardManager clipboardManager = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
										if (load("com.tencent.mobileqq.data.MessageForStructing").isAssignableFrom(msgObj.getClass())) {
											clipboardManager.setText((String) invoke_virtual(iget_object_or_null(msgObj, "structingMsg"), "getXml", new Object[0]));
											showToast(view.getContext(), TOAST_TYPE_INFO, "复制成功", Toast.LENGTH_SHORT);
										} else if (load("com.tencent.mobileqq.data.MessageForArkApp").isAssignableFrom(msgObj.getClass())) {
											clipboardManager.setText((String) invoke_virtual(iget_object_or_null(msgObj, "ark_app_message"), "toAppXml", new Object[0]));
											showToast(view.getContext(), TOAST_TYPE_INFO, "复制成功", Toast.LENGTH_SHORT);
										}
									} catch (Throwable ignored) {
									}
									return true;
								}
							});
                        viewGroup.setBackgroundDrawable(new DebugDrawable(viewGroup.getContext()));
                    }
                }
            });
		inited = true;
		return true;
    }

	@Override
	public int getEffectiveProc() {
		return SyncUtils.PROC_MAIN;
	}

    @Override
    public boolean checkPreconditions() {
        return true;
    }

    @Override
    public int[] getPreconditions() {
        return new int[]{};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefault().getBooleanOrFalse(qn_send_card_msg);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
