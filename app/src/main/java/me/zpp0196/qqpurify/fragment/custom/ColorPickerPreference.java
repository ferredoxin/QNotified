package me.zpp0196.qqpurify.fragment.custom;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.jaredrummler.android.colorpicker.ColorPanelView;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jaredrummler.android.colorpicker.ColorShape;
import nil.nadph.qnotified.R;

/**
 * A Preference to select a color
 * copy from {@link com.jaredrummler.android.colorpicker.ColorPreferenceCompat}
 */
public class ColorPickerPreference extends Preference implements ColorPickerDialogListener {

    private ColorPickerDialogListener colorPickerDialogListener;
    private int color = Color.BLACK;
    private boolean showDialog;
    @ColorPickerDialog.DialogType
    private int dialogType;
    private int colorShape;
    private boolean allowPresets;
    private boolean allowCustom;
    private boolean showAlphaSlider;
    private boolean showColorShades;
    private int[] presets;
    private int dialogTitle;

    private ColorPickerDialog dialog;

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setPersistent(true);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ColorPreference);
        showDialog = a.getBoolean(R.styleable.ColorPreference_cpv_showDialog, true);
        //noinspection WrongConstant
        dialogType = a.getInt(R.styleable.ColorPreference_cpv_dialogType, ColorPickerDialog.TYPE_PRESETS);
        colorShape = a.getInt(R.styleable.ColorPreference_cpv_colorShape, ColorShape.CIRCLE);
        allowPresets = a.getBoolean(R.styleable.ColorPreference_cpv_allowPresets, true);
        allowCustom = a.getBoolean(R.styleable.ColorPreference_cpv_allowCustom, true);
        showAlphaSlider = a.getBoolean(R.styleable.ColorPreference_cpv_showAlphaSlider, false);
        showColorShades = a.getBoolean(R.styleable.ColorPreference_cpv_showColorShades, true);
        final int presetsResId = a.getResourceId(R.styleable.ColorPreference_cpv_colorPresets, 0);
        dialogTitle = a.getResourceId(R.styleable.ColorPreference_cpv_dialogTitle, R.string.cpv_default_title);
        if (presetsResId != 0) {
            presets = getContext().getResources().getIntArray(presetsResId);
        } else {
            presets = ColorPickerDialog.MATERIAL_COLORS;
        }
        a.recycle();
    }

    @Override
    protected void onClick() {
        super.onClick();
        if (showDialog) {
            dialog = ColorPickerDialog.newBuilder()
                    .setDialogType(dialogType)
                    .setDialogTitle(dialogTitle)
                    .setColorShape(colorShape)
                    .setPresets(presets)
                    .setAllowPresets(allowPresets)
                    .setAllowCustom(allowCustom)
                    .setShowAlphaSlider(showAlphaSlider)
                    .setShowColorShades(showColorShades)
                    .setColor(color)
                    .create();
            dialog.setColorPickerDialogListener(this);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(dialog, getFragmentTag())
                    .commitAllowingStateLoss();
        }
    }

    public FragmentActivity getActivity() {
        Context context = getContext();
        if (context instanceof FragmentActivity) {
            return (FragmentActivity) context;
        } else if (context instanceof ContextWrapper) {
            Context baseContext = ((ContextWrapper) context).getBaseContext();
            if (baseContext instanceof FragmentActivity) {
                return (FragmentActivity) baseContext;
            }
        }
        throw new IllegalStateException("Error getting activity from context");
    }

    @Override
    public void onAttached() {
        super.onAttached();
        if (showDialog) {
            ColorPickerDialog fragment =
                    (ColorPickerDialog) getActivity().getSupportFragmentManager().findFragmentByTag(getFragmentTag());
            if (fragment != null) {
                // re-bind preference to fragment
                fragment.setColorPickerDialogListener(this);
            }
        }
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        ColorPanelView preview = holder.itemView.findViewById(R.id.cpv_preference_preview_color_panel);
        if (preview != null) {
            preview.setColor(color);
        }
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        super.onSetInitialValue(defaultValue);
        if (defaultValue instanceof Integer) {
            color = (Integer) defaultValue;
            persistInt(color);
        } else {
            color = getPersistedInt(0xFF000000);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, Color.BLACK);
    }

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {
        if (colorPickerDialogListener != null) {
            colorPickerDialogListener.onColorSelected(dialogId, color);
        }
        dialog.dismiss();
        saveValue(color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        if (colorPickerDialogListener != null) {
            colorPickerDialogListener.onDialogDismissed(dialogId);
        }
    }

    /**
     * Set the new color
     *
     * @param color The newly selected color
     */
    public void saveValue(@ColorInt int color) {
        this.color = color;
        persistInt(this.color);
        notifyChanged();
        callChangeListener(color);
    }

    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Set the colors shown in the {@link ColorPickerDialog}.
     *
     * @param presets An array of color ints
     */
    public void setPresets(@NonNull int[] presets) {
        this.presets = presets;
    }

    public void setColorPickerDialogListener(ColorPickerDialogListener listener) {
        this.colorPickerDialogListener = listener;
    }

    /**
     * The tag used for the {@link ColorPickerDialog}.
     *
     * @return The tag
     */
    public String getFragmentTag() {
        return "color_" + getKey();
    }
}
