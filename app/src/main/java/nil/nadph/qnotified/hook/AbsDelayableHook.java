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

import cn.lliiooll.hook.AntiMessage;
import cn.lliiooll.hook.MessageInterception;
import com.rymmmmm.hook.BaseApk;
import com.rymmmmm.hook.CustomDeviceModel;
import com.rymmmmm.hook.CustomMsgTimeFormat;
import com.rymmmmm.hook.CustomSplash;
import com.rymmmmm.hook.DefaultFont;
import com.rymmmmm.hook.DisableAvatarDecoration;
import com.rymmmmm.hook.DisableColorNickName;
import com.rymmmmm.hook.DisableDropSticker;
import com.rymmmmm.hook.DisableEnterEffect;
import com.rymmmmm.hook.DisablePokeEffect;
import com.rymmmmm.hook.IgnoreDiyCard;
import com.rymmmmm.hook.OneTapTwentyLikes;
import com.rymmmmm.hook.RemoveMiniProgramAd;
import com.rymmmmm.hook.RemoveSendGiftAd;
import com.rymmmmm.hook.ShowMsgCount;

import cc.ioctl.hook.$endGiftHook;
import cc.ioctl.hook.ArbitraryFrdSourceId;
import cc.ioctl.hook.BlockFluxThief;
import cc.ioctl.hook.CardMsgHook;
import cc.ioctl.hook.ChatTailHook;
import cc.ioctl.hook.CheatHook;
import cc.ioctl.hook.DarkOverlayHook;
import cc.ioctl.hook.DefaultBubbleHook;
import cc.ioctl.hook.DelDetectorHook;
import cc.ioctl.hook.EmoPicHook;
import cc.ioctl.hook.FakeBatteryHook;
import cc.ioctl.hook.FakeVipHook;
import cc.ioctl.hook.FavMoreEmo;
import cc.ioctl.hook.FileRecvRedirect;
import cc.ioctl.hook.FlashPicHook;
import cc.ioctl.hook.GagInfoDisclosure;
import cc.ioctl.hook.HideGiftAnim;
import cc.ioctl.hook.HideMiniAppPullEntry;
import cc.ioctl.hook.InputButtonHook;
import cc.ioctl.hook.InspectMessage;
import cc.ioctl.hook.InterceptZipBomb;
import cc.ioctl.hook.JumpController;
import cc.ioctl.hook.MultiForwardAvatarHook;
import cc.ioctl.hook.MuteAtAllAndRedPacket;
import cc.ioctl.hook.MuteQZoneThumbsUp;
import cc.ioctl.hook.PicMd5Hook;
import cc.ioctl.hook.PreUpgradeHook;
import cc.ioctl.hook.PttForwardHook;
import cc.ioctl.hook.RepeaterHook;
import cc.ioctl.hook.ReplyNoAtHook;
import cc.ioctl.hook.RevokeMsgHook;
import cc.ioctl.hook.RoundAvatarHook;
import cc.ioctl.hook.ScriptEventHook;
import cc.ioctl.hook.SettingEntryHook;
import cc.ioctl.hook.ShowPicGagHook;
import cc.ioctl.hook.VasProfileAntiCrash;
import me.ketal.hook.HideAssistantRemoveTips;
import me.ketal.hook.LeftSwipeReplyHook;
import me.ketal.hook.MultiActionHook;
import me.ketal.hook.SendFavoriteHook;
import me.ketal.hook.SortAtPanel;
import me.kyuubiran.hook.AutoMosaicName;
import me.kyuubiran.hook.AutoRenewFire;
import me.kyuubiran.hook.DisableScreenshotHelper;
import me.kyuubiran.hook.RemoveCameraButton;
import me.kyuubiran.hook.RemoveDailySign;
import me.kyuubiran.hook.RemoveDiyCard;
import me.kyuubiran.hook.RemoveGroupApp;
import me.kyuubiran.hook.RemovePlayTogether;
import me.kyuubiran.hook.RemoveQbossAD;
import me.kyuubiran.hook.RemoveRedDot;
import me.kyuubiran.hook.RevokeMsg;
import me.kyuubiran.hook.ShowSelfMsgByLeft;
import me.kyuubiran.hook.SimplifyQQSettingMe;
import me.kyuubiran.hook.testhook.CutMessage;
import ltd.nextalone.hook.EnableQLog;
import ltd.nextalone.hook.ForcedSendOriginalPhoto;
import ltd.nextalone.hook.HideChatVipImage;
import ltd.nextalone.hook.HideOnlineNumber;
import ltd.nextalone.hook.HideOnlineStatus;
import ltd.nextalone.hook.HideProfileBubble;
import ltd.nextalone.hook.HideTotalNumber;
import ltd.nextalone.hook.RemoveIntimateDrawer;
import ltd.nextalone.hook.SimplifyChatLongItem;
import ltd.nextalone.hook.SimplifyPlusPanel;
import ltd.nextalone.hook.SimplifyQQSettings;
import me.singleneuron.hook.ChangeDrawerWidth;
import me.singleneuron.hook.DebugDump;
import me.singleneuron.hook.ForceSystemAlbum;
import me.singleneuron.hook.ForceSystemCamera;
import me.singleneuron.hook.ForceSystemFile;
import me.singleneuron.hook.NewRoundHead;
import me.singleneuron.hook.NoApplet;
import me.singleneuron.hook.decorator.SimpleCheckIn;
import me.singleneuron.qn_kernel.dispacher.ItemBuilderFactoryHook;
import me.singleneuron.qn_kernel.dispacher.StartActivityHook;
import nil.nadph.qnotified.config.SwitchConfigItem;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.Utils;

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
                ScriptEventHook.get(),
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
                SendFavoriteHook.INSTANCE,
                AutoRenewFire.INSTANCE,
                SimplifyChatLongItem.INSTANCE,
                HideOnlineStatus.INSTANCE,
                HideChatVipImage.INSTANCE,
                SimplifyQQSettings.INSTANCE,
                SimplifyPlusPanel.INSTANCE,
                MessageInterception.INSTANCE,
                AntiMessage.INSTANCE,
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
