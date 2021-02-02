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
package nil.nadph.qnotified.hook;

import androidx.annotation.NonNull;

import com.rymmmmm.hook.*;

import me.ketal.hook.*;
import me.kyuubiran.hook.*;
import me.kyuubiran.hook.testhook.*;
import me.nextalone.hook.*;
import me.singleneuron.hook.*;
import me.singleneuron.hook.decorator.*;
import me.singleneuron.qn_kernel.dispacher.*;
import nil.nadph.qnotified.config.*;
import nil.nadph.qnotified.step.*;
import nil.nadph.qnotified.util.*;

public abstract class AbsDelayableHook implements SwitchConfigItem {

    private static AbsDelayableHook[] sAllHooks;

    private int myId = -1;

    @NonNull
    public static AbsDelayableHook getHookByType(int hookId) {
        return queryDelayableHooks()[hookId];
    }

    @NonNull
    public static AbsDelayableHook[] queryDelayableHooks() {
        if (sAllHooks == null) {
            sAllHooks = new AbsDelayableHook[]{
                SettingEntryHook.get(),
                DelDetectorHook.get(),
                PttForwardHook.get(),
                MuteAtAllAndRedPacket.get(),
                CardMsgHook.get(),
                ChatTailHook.get(),
                FlashPicHook.get(),
                RepeaterHook.get(),
                EmoPicHook.get(),
                FavMoreEmo.get(),
                RevokeMsgHook.get(),
                FakeVipHook.get(),
                HideGiftAnim.get(),
                PreUpgradeHook.get(),
                CheatHook.get(),
                RoundAvatarHook.get(),
                $endGiftHook.get(),
                MultiForwardAvatarHook.get(),
                ReplyNoAtHook.get(),
                MuteQZoneThumbsUp.get(),
                FakeBatteryHook.get(),
                FileRecvRedirect.get(),
                ShowPicGagHook.get(),
                DefaultBubbleHook.get(),
                DarkOverlayHook.get(),
                GagInfoDisclosure.get(),
                PicMd5Hook.get(),
                ShowMsgCount.get(),
                IgnoreDiyCard.get(),
                InspectMessage.get(),
                DefaultFont.get(),
                BaseApk.get(),
                DisableScreenshotHelper.get(),
                OneTapTwentyLikes.get(),
                DisableEnterEffect.get(),
                DisableColorNickName.get(),
                CustomMsgTimeFormat.get(),
                RemoveSendGiftAd.get(),
                DisableDropSticker.get(),
                DisablePokeEffect.get(),
                RemoveMiniProgramAd.get(),
                JumpController.get(),
                CustomDeviceModel.get(),
                CustomSplash.get(),
                DisableAvatarDecoration.get(),
                RemoveCameraButton.get(),
                RemovePlayTogether.get(),
                RemoveQbossAD.get(),
                NoApplet.INSTANCE,
                HideProfileBubble.INSTANCE,
                NewRoundHead.INSTANCE,
                ForceSystemCamera.INSTANCE,
                AutoMosaicName.INSTANCE,
                ForceSystemAlbum.INSTANCE,
                ForceSystemFile.INSTANCE,
                ShowSelfMsgByLeft.INSTANCE,
                RemoveGroupApp.INSTANCE,
                RemoveIntimateDrawer.INSTANCE,
                InputButtonHook.get(),
                SimplifyQQSettingMe.INSTANCE,
                DebugDump.INSTANCE,
                ChangeDrawerWidth.INSTANCE,
                CutMessage.INSTANCE,
                VasProfileAntiCrash.get(),
                RevokeMsg.INSTANCE,
                ItemBuilderFactoryHook.INSTANCE,
                SimpleCheckIn.INSTANCE,
                StartActivityHook.INSTANCE,
                ArbitraryFrdSourceId.get(),
                RemoveDailySign.INSTANCE,
                RemoveDiyCard.INSTANCE,
                RemoveRedDot.INSTANCE,
                EnableQLog.INSTANCE,
                ForcedSendOriginalPhoto.INSTANCE,
                InterceptZipBomb.INSTANCE,
                HideTotalNumber.INSTANCE,
                HideOnlineNumber.INSTANCE,
                BlockFluxThief.INSTANCE,
                MultiActionHook.INSTANCE,
                HideAssistantRemoveTips.INSTANCE,
                LeftSwipeReplyHook.INSTANCE,
                HideMiniAppPullEntry.INSTANCE,
                SortAtPanel.INSTANCE,
                ScriptEventHook.get(),
            };
        }
        return sAllHooks;
    }

    public static void allowEarlyInit(@NonNull AbsDelayableHook hook) {
        if (hook == null) {
            return;
        }
        try {
            if (hook.isTargetProc() && hook.isEnabled() && hook.checkPreconditions() && !hook.isInited()) {
                hook.init();
            }
        } catch (Throwable e) {
            Utils.log(e);
        }
    }

    public abstract boolean isTargetProc();

    public abstract int getEffectiveProc();

    public abstract boolean isInited();

    public abstract boolean init();

    @Override
    public abstract boolean sync();

    @NonNull
    public abstract Step[] getPreconditions();

    @Override
    public abstract boolean isValid();

    public abstract boolean checkPreconditions();

    public final int getId() {
        if (myId != -1) {
            return myId;
        }
        AbsDelayableHook[] hooks = queryDelayableHooks();
        for (int i = 0; i < hooks.length; i++) {
            if (hooks[i].getClass().equals(getClass())) {
                myId = i;
                return myId;
            }
        }
        return -1;
    }

    /**
     * safe to call, no Throwable allowed
     *
     * @return whether the config item is enabled
     */
    @Override
    public abstract boolean isEnabled();

    /**
     * This method must be safe to call even if it is NOT inited
     *
     * @param enabled has no effect if isValid() returns false
     */
    @Override
    public abstract void setEnabled(boolean enabled);

}
