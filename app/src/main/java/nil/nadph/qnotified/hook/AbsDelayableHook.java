/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
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

import me.ketal.hook.HideAssistantRemoveTips;
import me.ketal.hook.LeftSwipeReplyHook;
import me.kyuubiran.hook.AutoMosaicName;
import me.kyuubiran.hook.DisableScreenshotHelper;
import me.kyuubiran.hook.RemoveCameraButton;
import me.kyuubiran.hook.RemoveDailySign;
import me.kyuubiran.hook.RemoveFuckingDiyCard;
import me.kyuubiran.hook.RemoveGroupApp;
import me.kyuubiran.hook.RemovePlayTogether;
import me.kyuubiran.hook.RemoveQbossAD;
import me.kyuubiran.hook.RemoveRedDot;
import me.kyuubiran.hook.RevokeMsg;
import me.kyuubiran.hook.ShowSelfMsgByLeft;
import me.kyuubiran.hook.SimplifyQQSettingMe;
import me.kyuubiran.hook.testhook.CutMessage;
import me.nextalone.hook.EnableQLog;
import me.nextalone.hook.ForcedSendOriginalPhoto;
import me.nextalone.hook.HideOnlineNumber;
import me.nextalone.hook.HideProfileBubble;
import me.nextalone.hook.HideTotalNumber;
import me.nextalone.hook.RemoveIntimateDrawer;
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
import nil.nadph.qnotified.hook.rikka.BaseApk;
import nil.nadph.qnotified.hook.rikka.CustomDeviceModel;
import nil.nadph.qnotified.hook.rikka.CustomMsgTimeFormat;
import nil.nadph.qnotified.hook.rikka.CustomSplash;
import nil.nadph.qnotified.hook.rikka.DefaultFont;
import nil.nadph.qnotified.hook.rikka.DisableAvatarDecoration;
import nil.nadph.qnotified.hook.rikka.DisableColorNickName;
import nil.nadph.qnotified.hook.rikka.DisableDropSticker;
import nil.nadph.qnotified.hook.rikka.DisableEnterEffect;
import nil.nadph.qnotified.hook.rikka.DisablePokeEffect;
import nil.nadph.qnotified.hook.rikka.IgnoreDiyCard;
import nil.nadph.qnotified.hook.rikka.OneTapTwentyLikes;
import nil.nadph.qnotified.hook.rikka.RemoveMiniProgramAd;
import nil.nadph.qnotified.hook.rikka.RemoveSendGiftAd;
import nil.nadph.qnotified.hook.rikka.ShowMsgCount;
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
                    //GalleryBgHook.get(),
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
                    //DisableShakeWindow.get(),
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
                    //RemovePokeGrayTips.INSTANCE,
                    ArbitraryFrdSourceId.get(),
                    RemoveDailySign.INSTANCE,
                    RemoveFuckingDiyCard.INSTANCE,
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
