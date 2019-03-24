package nil.nadph.qnotified;

import java.util.*;

@SuppressWarnings ("all")
public class QConst{
/**
	@formatter:off

    private static HashMap<String, Integer> id;

    private static int[] supportVersion = new int[]{/*818, 828,* 832/*, 836, 850, 852, 864, 872, 884*};

    
    public static boolean isSupport(int versionCode){
        if(versionCode>supportVersion[supportVersion.length-1]){
            return false;
        }
        if(versionCode<supportVersion[0]){
            return true;
        }

        for(int i : supportVersion){
            if(versionCode==i){
                return true;
            }
        }
        return false;
    }

    public static Integer getId(String name){
        return id.get(name);
    }
	
	private static void init760_832(){
		id.put("custom_commen_title_rightview", 0x7F040107);
		id.put("custom_commen_title", 0x7F040100);
		id.put("bg_texture", 0x7F0202C2);
	}
	

    
		if(id==null){
            id=new HashMap<>();
        }else{
            id.clear();
        }
        int version = Utils.getQQVersionCode(Utils.getSystemContext());
        switch(version){
				/*case 818:
				 init758_818();
				 break;
				 case 828:
				 init800_828();
				 break;*
            case 832:
                init760_832();
                break;
				/*case 836:
				 init763_836();
				 break;
				 case 850:
				 case 852:
				 init763_850();
				 break;
				 case 864:
				 init765_864();
				 break;
				 case 872:
				 init768_872();
				 break;
				 case 884:
				 init770_884();
				 break;*
        }
		/*)
		 if (R$drawable == null)
		 R$drawable = load(".R$drawable");
		 if (AbstractChatItemBuilder$ViewHolder == null)
		 AbstractChatItemBuilder$ViewHolder = load(".activity.aio.AbstractChatItemBuilder$ViewHolder");
		 if (AbstractGalleryScene == null)
		 AbstractGalleryScene = load("com.tencent.common.galleryactivity.AbstractGalleryScene");
		 if (AioAnimationConfigHelper == null)
		 AioAnimationConfigHelper = load(".activity.aio.anim.AioAnimationConfigHelper");
		 if (AIOImageProviderService == null)
		 AIOImageProviderService = load(".activity.aio.photo.AIOImageProviderService");
		 if (AIOPanelUtiles == null)
		 AIOPanelUtiles = load(".activity.aio.panel.AIOPanelUtiles");
		 if (ApolloManager$CheckApolloInfoResult == null)
		 ApolloManager$CheckApolloInfoResult = load(".apollo.ApolloManager$CheckApolloInfoResult");
		 if (BannerManager == null)
		 BannerManager = load(".activity.recent.BannerManager");
		 if (BaseActivity == null)
		 BaseActivity = load(".app.BaseActivity");
		 if (BaseBubbleBuilder$ViewHolder == null)
		 BaseBubbleBuilder$ViewHolder = load(".activity.aio.BaseBubbleBuilder$ViewHolder");
		 if (CardController == null)
		 CardController = load(".activity.contacts.base.CardController");
		 if (BaseChatItemLayout == null)
		 BaseChatItemLayout = load(".activity.aio.BaseChatItemLayout");
		 if (BaseChatPie == null)
		 BaseChatPie = load(".activity.BaseChatPie");
		 if (BaseTroopChatPie == null)
		 BaseTroopChatPie = load(".activity.aio.rebuild.BaseTroopChatPie");
		 if (BubbleManager == null)
		 BubbleManager = load(".bubble.BubbleManager");
		 if (BusinessInfoCheckUpdate$RedTypeInfo == null)
		 BusinessInfoCheckUpdate$RedTypeInfo = load("com.tencent.pb.getbusiinfo.BusinessInfoCheckUpdate$RedTypeInfo");
		 if (Card == null)
		 Card = load(".data.Card");
		 if (ChatActivityUtils == null)
		 ChatActivityUtils = load(".activity.ChatActivityUtils");
		 if (ChatMessage == null)
		 ChatMessage = load(".data.ChatMessage");
		 if (CommonCardEntry  == null)
		 CommonCardEntry = load(".activity.contacts.base.CommonCardEntry");
		 if (Conversation == null)
		 Conversation = load(".activity.Conversation");
		 if (ConversationNowController == null)
		 ConversationNowController = load(".now.enter.ConversationNowController");
		 if (Contacts == null)
		 Contacts = load(".activity.Contacts");
		 if (ContactUtils == null)
		 ContactUtils = load(".utils.ContactUtils");
		 if (CoreService == null)
		 CoreService = load(".app.CoreService");
		 if (CoreService$KernelService == null)
		 CoreService$KernelService = load(".app.CoreService$KernelService");
		 if (CountDownProgressBar == null)
		 CountDownProgressBar = load("com.tencent.widget.CountDownProgressBar");
		 if (EmoticonMainPanel == null)
		 EmoticonMainPanel = load(".emoticonview.EmoticonMainPanel");
		 if (EmoticonManager == null)
		 EmoticonManager = load(".model.EmoticonManager");
		 if (FileManagerUtil == null)
		 FileManagerUtil = load(".filemanager.util.FileManagerUtil");
		 if (FriendFragment == null)
		 FriendFragment = load(".activity.contacts.fragment.FriendFragment");
		 if (FontManager == null)
		 FontManager = load("com.etrump.mixlayout.FontManager");
		 if (FontSettingManager == null)
		 FontSettingManager = load(".app.FontSettingManager");
		 if (FrameHelperActivity == null)
		 FrameHelperActivity = load(".app.FrameHelperActivity");
		 if (GatherContactsTips == null)
		 GatherContactsTips = load(".activity.aio.tips.GatherContactsTips");
		 if (GrayTipsItemBuilder == null)
		 GrayTipsItemBuilder = load(".activity.aio.item.GrayTipsItemBuilder");
		 if (HotChatFlashPicActivity == null)
		 HotChatFlashPicActivity = load(".dating.HotChatFlashPicActivity");
		 if (ItemBuilderFactory == null)
		 ItemBuilderFactory = load(".activity.aio.item.ItemBuilderFactory");
		 if (Leba == null)
		 Leba = load(".activity.Leba");
		 if (LebaQZoneFacePlayHelper == null)
		 LebaQZoneFacePlayHelper = load(".activity.LebaQZoneFacePlayHelper");
		 if (LocalSearchBar == null)
		 LocalSearchBar = load(".activity.recent.LocalSearchBar");
		 /*if (MainEntryAni == null && isMoreThan763())
		 MainEntryAni = findClassInQQ(".ar.config.MainEntryAni");
		 if (MainFragment == null)
		 MainFragment = load(".activity.MainFragment");
		 if (MedalNewsItemBuilder == null)
		 MedalNewsItemBuilder = load(".activity.aio.item.MedalNewsItemBuilder");
		 if (MessageForDeliverGiftTips == null)
		 MessageForDeliverGiftTips = load(".data.MessageForDeliverGiftTips");
		 if (MessageForPic == null)
		 MessageForPic = load(".data.MessageForPic");
		 if (MessageInfo == null)
		 MessageInfo = load(".troop.data.MessageInfo");
		 if (MessageRecord == null)
		 MessageRecord = load(".data.MessageRecord");
		 if (MessageRecordFactory == null)
		 MessageRecordFactory = load(".service.message.MessageRecordFactory");
		 if (OnLongClickAndTouchListener == null)
		 OnLongClickAndTouchListener = load(".activity.aio.OnLongClickAndTouchListener");
		 if (PanelIconLinearLayout == null)
		 PanelIconLinearLayout = load(".activity.aio.panel.PanelIconLinearLayout");
		 if (PicItemBuilder == null)
		 PicItemBuilder = load(".activity.aio.item.PicItemBuilder");
		 if (PicItemBuilder$Holder == null)
		 PicItemBuilder$Holder = load(".activity.aio.item.PicItemBuilder$Holder");
		 if (PopupMenuDialog == null)
		 PopupMenuDialog = load("com.tencent.widget.PopupMenuDialog");
		 if (PopupMenuDialog$MenuItem == null)
		 PopupMenuDialog$MenuItem = load("com.tencent.widget.PopupMenuDialog$MenuItem");
		 if (PopupMenuDialog$OnClickActionListener == null)
		 PopupMenuDialog$OnClickActionListener = load("com.tencent.widget.PopupMenuDialog$OnClickActionListener");
		 if (PopupMenuDialog$OnDismissListener == null)
		 PopupMenuDialog$OnDismissListener = load("com.tencent.widget.PopupMenuDialog$OnDismissListener");
		 if (QQAppInterface == null)
		 QQAppInterface = load(".app.QQAppInterface");
		 if (QQMessageFacade == null)
		 QQMessageFacade = load(".app.message.QQMessageFacade");
		 if (QQSettingMe == null)
		 QQSettingMe = load(".activity.QQSettingMe");
		 if (QQSettingSettingActivity == null)
		 QQSettingSettingActivity = load(".activity.QQSettingSettingActivity");
		 if (QzoneFeedItemBuilder == null)
		 QzoneFeedItemBuilder = load(".activity.aio.item.QzoneFeedItemBuilder");
		 if (QzonePluginProxyActivity == null)
		 QzonePluginProxyActivity = load("cooperation.qzone.QzonePluginProxyActivity");
		 if (QZoneHelper == null)
		 QZoneHelper = load("cooperation.qzone.QZoneHelper");
		 if (RecentBaseData == null)
		 RecentBaseData = load(".activity.recent.RecentBaseData");
		 if (RecentEfficientItemBuilder == null)
		 RecentEfficientItemBuilder = load(".activity.recent.RecentEfficientItemBuilder");
		 if (RecentOptPopBar == null)
		 RecentOptPopBar = load(".activity.recent.RecentOptPopBar");
		 if (RichStatItemBuilder == null)
		 RichStatItemBuilder = load(".activity.aio.item.RichStatItemBuilder");
		 if (SimpleSlidingIndicator == null)
		 SimpleSlidingIndicator = load(".activity.contacts.view.SimpleSlidingIndicator");
		 if (SougouInputGrayTips == null)
		 SougouInputGrayTips = load(".activity.aio.tips.SougouInputGrayTips");
		 if (TextItemBuilder == null)
		 TextItemBuilder = load(".activity.aio.item.TextItemBuilder");
		 if (TextPreviewActivity == null)
		 TextPreviewActivity = load(".activity.TextPreviewActivity");
		 if (TListView == null)
		 TListView = load("com.tencent.widget.ListView");
		 if (TroopAssistantActivity == null)
		 TroopAssistantActivity = load(".activity.TroopAssistantActivity");
		 if (TroopEnterEffectController == null)
		 TroopEnterEffectController = load(".troop.enterEffect.TroopEnterEffectController");
		 if (TroopGiftAnimationController == null)
		 TroopGiftAnimationController = load(".troopgift.TroopGiftAnimationController");
		 if (UpgradeController == null)
		 UpgradeController = load(".app.upgrade.UpgradeController");
		 if (UpgradeDetailWrapper == null)
		 UpgradeDetailWrapper = load(".app.upgrade.UpgradeDetailWrapper");
		 if (URLImageView == null)
		 URLImageView = load("com.tencent.image.URLImageView");
		 if (VipSpecialCareGrayTips == null)
		 VipSpecialCareGrayTips = load(".activity.aio.tips.VipSpecialCareGrayTips");
		 if (XEditTextEx == null)
		 XEditTextEx = load("com.tencent.widget.XEditTextEx");
		 
	 	@formatter:on
    /}*/
	public static void init(ClassLoader classLoader){
        qqClassLoader=classLoader;
	}

	private static ClassLoader qqClassLoader;
	
    public static Class<?> load(String className){
        if(qqClassLoader==null||className.isEmpty()){
            return null;
        }
		className=className.replace('/','.');
		if(className.endsWith(";"))className=className.substring(0,className.length()-1);
		if(className.charAt(0)=='L'&&className.charAt(1)>='a')className=className.substring(1);
        if(className.startsWith(".")){
            className=Utils.PACKAGE_NAME_QQ+className;
        }
        try{
            return qqClassLoader.loadClass(className);
        }catch(Throwable e){
            if(!className.contains("com.tencent.mobileqq.R$")){
                Utils.log(String.format("Can't find the Class of name: %s!",className));
            }
            return null;
        }
    }
}
