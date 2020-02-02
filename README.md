# QNotified
QNotified（以下简称本模块）是一款依赖 Xposed 框架运行的辅助性QQ功能增强模块, 本模块无毒无害, 免费开源, 旨在
1. **接手部分停更模块的部分功能**
2. 提供被删好友通知功能
3. **移除部分臃肿功能, 增加部分实用功能**

QNotified is a Xposed module for mobile QQ that aims to
1. bring some nice functions in some modules back to life
2.  automatically refreshes friend list and tell(notify) user which friend had deleted him/her
3. remove some unnecessary functions and add to facility

-  **适配QQ版本** : 普通版QQ,TIM (全版本)
-  **使用方法** : 在Xposed Installer 激活模块后，在QQ自带设置中点击QNotified即可开关对应功能。
- 关于删好友通知:  **(从安装并激活日起生效，无法查看安装本模块以前删除自己的好友!)** ,自己删除好友没有多余提示但是有记录。激活模块后自动检测，无需手动配置. 
- QQ内模块界面主题自动跟随QQ主题或ColorQQ(2). 

**功能介绍** 
1. 隐藏消息列表小程序入口
2. 去除回复自动at
3. 语音消息转发
4. 文本消息群发(原QQHelper)
5. 以图片方式打开闪照(原辅助模块)
6. 以图片方式打开表情包(原QQ净化)
7. Ark(json)/StructMsg(xml)卡片消息(原BUG复读机)
8. 复读机(+1,原BUG复读机)
9. 被删好友通知
10. 防撤回(实验性,不稳定)
11. 签到文本化,隐藏礼物动画
12. 简洁模式圆头像(from Rikka)
13. 自定义电量(实验性)
14. 转发消息点击头像打开资料卡
15. 下载重定向(原QQ净化)
16. 屏蔽 \@全体成员 或者 群红包 的通知(不影响接收消息,不影响某些插件抢红包功能)
17. 屏蔽QQ更新提示
18. 屏蔽QQ空间点赞通知
19. 禁止聊天界面输入＄自动弹出 选择赠送对象 窗口

**在建功能** (未必一次更新全部到位)
1. 查看已删除好友的聊天记录
2. 收藏更多表情(原辅助模块)
3. 屏蔽回执消息通知
4. 自定义复读次数
5. 修复偶发性的莫名其妙好友全部被标记为删除的bug

**考虑中功能**
1. 去你大爷的TX地图
2. _用户建议_ 打开网页请用户选择使用内置浏览器或外部浏览器
3. _用户建议_ (可详细设置)在某些情景模式下禁用QQ提醒功能

**如发现bug或好的建议请在Issue区留言**

本模块完全免费开源,近期发现模块倒卖现象严重,请勿上当

(EE) CET4 failed,update halted.

