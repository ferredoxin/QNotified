package nil.nadph.qnotified.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class CustomDialog {
    private static Class<?> clz_DialogUtil;
    private static Class<?> clz_CustomDialog;
    private static Method m_DialogUtil_a;

    private Dialog mDialog = null;
    private AlertDialog mFailsafeDialog = null;
    private AlertDialog.Builder mBuilder = null;
    private boolean failsafe = false;

    public static CustomDialog create(Context ctx) {
        try {
            if (clz_DialogUtil == null) {
                clz_DialogUtil = DexKit.doFindClass(DexKit.C_DIALOG_UTIL);
            }
            if (clz_CustomDialog == null) {
                clz_CustomDialog = load("com/tencent/mobileqq/utils/QQCustomDialog");
                if (clz_CustomDialog == null) {
                    Class clz_Lite = load("com/dataline/activities/LiteActivity");
                    Field[] fs = clz_Lite.getDeclaredFields();
                    for (Field f : fs) {
                        if (Modifier.isPrivate(f.getModifiers()) && Dialog.class.equals(f.getType().getSuperclass())) {
                            clz_CustomDialog = f.getType();
                            break;
                        }
                    }
                }
            }
            if (m_DialogUtil_a == null) {
                Method tmpa = null, tmpb = null;
                for (Method m : clz_DialogUtil.getDeclaredMethods()) {
                    if (m.getReturnType().equals(clz_CustomDialog) && (Modifier.isPublic(m.getModifiers()))) {
                        Class<?>[] argt = m.getParameterTypes();
                        if (argt.length != 2) continue;
                        if (argt[0].equals(Context.class) && argt[1].equals(int.class)) {
                            if (m.getName().equals("a")) {
                                m_DialogUtil_a = m;
                                break;
                            } else {
                                if (tmpa == null) {
                                    tmpa = m;
                                } else {
                                    tmpb = m;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (m_DialogUtil_a == null && tmpa != null) {
                    if (tmpb == null) {
                        m_DialogUtil_a = tmpa;
                    } else {
                        m_DialogUtil_a = (strcmp(tmpa.getName(), tmpb.getName()) > 0) ? tmpb : tmpa;
                    }
                }
            }
        } catch (Exception e) {
            log(e);
        }
        CustomDialog ref = new CustomDialog();
        try {
            ref.mDialog = (Dialog) m_DialogUtil_a.invoke(null, ctx, 230);
        } catch (Exception e) {
            log(e);
        }
        if (ref.mDialog == null) {
            ref.failsafe = true;
            ref.mBuilder = new AlertDialog.Builder(ctx, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        }
        return ref;
    }

    public CustomDialog setCancelable(boolean flag) {
        if (!failsafe) {
            mDialog.setCancelable(flag);
            if (flag) {
                mDialog.setCanceledOnTouchOutside(true);
            }
        } else {
            mBuilder.setCancelable(flag);
        }
        return this;
    }

    public CustomDialog setTitle(String title) {
        if (!failsafe) {
            try {
                invoke_virtual(mDialog, "setTitle", title, String.class);
            } catch (Exception e) {
                log(e);
            }
        } else {
            if (mFailsafeDialog == null)
                mBuilder.setTitle(title);
            else mFailsafeDialog.setTitle(title);
        }
        return this;
    }

    public CustomDialog setMessage(CharSequence msg) {
        if (!failsafe) {
            try {
                invoke_virtual(mDialog, "setMessage", msg, CharSequence.class);
            } catch (Exception e) {
                log(e);
            }
        } else {
            if (mFailsafeDialog == null)
                mBuilder.setMessage(msg);
            else mFailsafeDialog.setMessage(msg);
        }
        return this;
    }

    public CustomDialog setView(View v) {
        if (!failsafe) {
            try {
                invoke_virtual(mDialog, "setView", v, View.class);
            } catch (Exception e) {
                log(e);
            }
        } else {
            if (mFailsafeDialog == null)
                mBuilder.setView(v);
            else mFailsafeDialog.setView(v);
        }
        return this;
    }

    public CustomDialog setPositiveButton(String text, DialogInterface.OnClickListener listener) {
        if (!failsafe) {
            if (text != null && listener == null) {
                listener = new DummyCallback();
            }
            try {
                invoke_virtual(mDialog, "setPositiveButton", text, listener, String.class, DialogInterface.OnClickListener.class);
            } catch (Exception e) {
                log(e);
            }
        } else {
            mBuilder.setPositiveButton(text, listener);
        }
        return this;
    }

    public CustomDialog setNegativeButton(String text, DialogInterface.OnClickListener listener) {
        if (!failsafe) {
            if (text != null && listener == null) {
                listener = new DummyCallback();
            }
            try {
                invoke_virtual(mDialog, "setNegativeButton", text, listener, String.class, DialogInterface.OnClickListener.class);
            } catch (Exception e) {
                log(e);
            }
        } else {
            mBuilder.setNegativeButton(text, listener);
        }
        return this;
    }

    public Dialog create() {
        if (!failsafe) {
            return mDialog;
        } else {
            if (mFailsafeDialog == null)
                mFailsafeDialog = mBuilder.create();
            return mFailsafeDialog;
        }
    }

    public Dialog show() {
        if (!failsafe) {
            mDialog.show();
            return mDialog;
        } else {
            if (mFailsafeDialog == null)
                mFailsafeDialog = mBuilder.create();
            mFailsafeDialog.show();
            return mFailsafeDialog;
        }
    }

    public void dismiss() {
        if (!failsafe) {
            mDialog.dismiss();
        } else {
            if (mFailsafeDialog != null) {
                mFailsafeDialog.dismiss();
            }
        }
    }

    public boolean isShowing() {
        if (mDialog != null) return mDialog.isShowing();
        if (mFailsafeDialog != null) return mFailsafeDialog.isShowing();
        return false;
    }

    @Nullable
    public TextView getMessageTextView() {
        if (!failsafe) {
            return (TextView) iget_object_or_null(mDialog, "text");
        } else {
            return null;
        }
    }
}
