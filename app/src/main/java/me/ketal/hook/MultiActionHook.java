/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package me.ketal.hook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.mobileqq.app.BaseActivity;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import me.singleneuron.qn_kernel.tlb.ConfigTable;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.bridge.QQMessageFacade;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.DexKit;

import static nil.nadph.qnotified.util.Initiator._BaseChatPie;
import static nil.nadph.qnotified.util.Initiator._ChatMessage;
import static nil.nadph.qnotified.util.ReflexUtil.*;
import static nil.nadph.qnotified.util.Utils.getFirstByType;
import static nil.nadph.qnotified.util.Utils.log;

public class MultiActionHook extends CommonDelayableHook {
    public static final MultiActionHook INSTANCE = new MultiActionHook();
    private static Bitmap img;
    private final String fieldName = HostInformationProviderKt.getHostInformationProvider().isTim() ? ConfigTable.INSTANCE.getConfig(MultiActionHook.class.getSimpleName()) : "a";
    private Object baseChatPie;

    MultiActionHook() {
        super("qn_multi_action", new DexDeobfStep(DexKit.C_MessageCache), new DexDeobfStep(DexKit.C_MSG_REC_FAC), new DexDeobfStep(DexKit.N_BASE_CHAT_PIE__createMulti), new DexDeobfStep(DexKit.C_MultiMsg_Manager));
    }

    private static Bitmap getRecallBitmap() {
        if (img == null || img.isRecycled())
            img = BitmapFactory.decodeStream(ResUtils.openAsset("recall.png"));
        return img;
    }

    @Override
    public boolean initOnce() {
        try {
            XposedBridge.hookMethod(DexKit.doFindMethod(DexKit.N_BASE_CHAT_PIE__createMulti), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    try {
                        if (!isEnabled())
                            return;
                        LinearLayout rootView = iget_object_or_null(param.thisObject, fieldName, LinearLayout.class);
                        if (rootView == null || !check(rootView)) return;
                        BaseActivity context = (BaseActivity) rootView.getContext();
                        baseChatPie = getFirstByType(param.thisObject, (Class<?>) _BaseChatPie());
                        int count = rootView.getChildCount();
                        boolean enableTalkBack = rootView.getChildAt(0).getContentDescription() != null;
                        if (rootView.findViewById(R.id.ketalRecallImageView) == null)
                            rootView.addView(create(context, getRecallBitmap(), enableTalkBack), count - 1);
                        setMargin(rootView);
                    } catch (Exception e) {
                        log(e);
                    }
                }
            });
            return true;
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    private void recall() {
        try {
            Class<?> clazz = DexKit.doFindClass(DexKit.C_MultiMsg_Manager);
            Object manager = findMethodByTypes_1(clazz, clazz).invoke(null);
            List<?> list = (List<?>) findMethodByTypes_1(clazz, List.class).invoke(manager);
            if (list != null) {
                for (Object msg : list)
                    QQMessageFacade.revokeMessage(msg);
            }
            invoke_virtual_any(baseChatPie, false, null, false, boolean.class, _ChatMessage(), boolean.class);
            baseChatPie = null;
        } catch (Exception e) {
            log(e);
        }
    }

    private void setMargin(LinearLayout rootView) {
        int width = rootView.getResources().getDisplayMetrics().widthPixels;
        int count = rootView.getChildCount();
        int rootMargin = ((RelativeLayout.LayoutParams) rootView.getLayoutParams()).leftMargin;
        int w = ((LinearLayout.LayoutParams) rootView.getChildAt(0).getLayoutParams()).height;
        int leftMargin = (width - rootMargin * 2 - w * count) / (count - 1);
        for (int i = 1; i < count; i++) {
            View view = rootView.getChildAt(i);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(w, w);
            layoutParams.setMargins(leftMargin, 0, 0, 0);
            layoutParams.gravity = 16;
            view.setLayoutParams(layoutParams);
        }
    }

    private boolean check(LinearLayout rootView) {
        int count = rootView.getChildCount();
        if (count == 1) return false;
        for (int i = 0; i < count; i++) {
            View view = rootView.getChildAt(i);
            if (view instanceof TextView)
                return false;
        }
        return true;
    }

    private ImageView create(Context context, Bitmap bitmap, boolean enableTalkBack) {
        ImageView imageView = new ImageView(context);
        if (enableTalkBack) {
            imageView.setContentDescription("撤回");
        }
        imageView.setOnClickListener(v -> recall());
        imageView.setImageBitmap(bitmap);
        imageView.setId(R.id.ketalRecallImageView);
        return imageView;
    }
}
