# QNotified

[![Build status](https://build.appcenter.ms/v0.1/apps/599b1851-3361-4e64-a277-6a4c8f6e7332/branches/master/badge)](https://install.appcenter.ms/orgs/qnotifieddev/apps/qnotified/distribution_groups/alpha)
[![license](https://img.shields.io/github/license/ferredoxin/QNotified.svg)](https://www.gnu.org/licenses/gpl-3.0.en.html)
[![GitHub release](https://img.shields.io/github/release/ferredoxin/QNotified.svg)](https://github.com/ferredoxin/QNotified/releases/latest) 

---
**QNotified开发组,管理组已正式休止活动，后续开发工作将由你们进行，感谢陪伴。**
---

等(zuo)不(si)及的小伙伴可以: [CI构建下载](https://install.appcenter.ms/orgs/qnotifieddev/apps/qnotified/distribution_groups/alpha) (危!富含bug与兼容性问题,PR可能含恶意代码,自行承担风险)

QNotified（以下简称本模块）是一款依赖 Xposed框架运行的辅助性QQ功能增强模块, 本模块无毒无害, 免费开源, 旨在:  
1. **接手部分停更模块的部分功能**
2. 提供被删好友通知功能
3. **移除部分臃肿功能, 增加部分实用功能**

QNotified is a Xposed module for mobile QQ that aims to:
1. bring some nice functions in some modules back to life
2. automatically refreshes friend list and tell(notify) user which friend had deleted him/her
3. remove some unnecessary functions and add to facility

-  **适配QQ版本** : 普通版QQ,TIM (全版本)
-  **使用方法** : 在Xposed Installer 激活模块后，在QQ自带设置中点击QNotified即可开关对应功能。
- 关于删好友通知:  **(从安装并激活日起生效，无法查看安装本模块以前删除自己的好友!)** ,自己删除好友没有多余提示但是有记录。激活模块后自动检测，无需手动配置. 
- QQ内模块界面主题自动跟随 QQ主题 或 Substratum主题如MaterialQQ - GoogleBlue 或 ColorQQ(2) . 

# 一切开发旨在学习，请勿用于非法用途
- 本项目保证永久开源，欢迎提交PR，但是请不要提交明显用于非法用途的功能。
- 如果某功能被大量运用于非法用途或严重侵害插件使用者权益，那么该功能将会被移除。
- 本模块完全免费开源, 近期发现模块倒卖现象严重,请勿上当

# 功能介绍 
1. 隐藏消息列表小程序入口
2. 去除回复自动at
3. 语音消息转发
4. 强制默认气泡
5. 以图片方式打开闪照(原辅助模块)
6. 以图片方式打开表情包(原QQ净化)
7. Ark(json)/StructMsg(xml)卡片消息(注1,原BUG复读机)
8. 复读机(+1,原QQ复读机)
9. 被删好友通知(可导出好友列表)
10. 防撤回
11. 签到文本化,隐藏礼物动画
12. 简洁模式圆头像(原花Q)
13. 自定义电量
14. 转发消息点击头像查看原消息发送者和所在群
15. 下载重定向(原QQ净化)
16. 屏蔽 \@全体成员 或者 群红包 的通知(不影响接收消息,不影响某些插件抢红包功能)
17. 屏蔽QQ更新提示
18. 屏蔽QQ空间点赞通知
19. 禁止聊天界面输入＄自动弹出 选择赠送对象 窗口
20. 直接打开不可通过QQ号码搜索到用户的资料卡
21. 屏蔽秀图
22. 显示进行禁言操作的管理员(查看哪个管理员禁言了你)
23. 去除夜间模式聊天界面深色遮罩
24. 直接打开指定用户资料卡(无视隐藏QQ号)
25. 自定义+1图标
26. 群发文本消息(注1)
27. 显示具体消息数量而不是99+(原花Q)

注1: 卡片消息及群发文本这两个功能因大量被用于广告引流而废除

#### 计划任务:
1. 查看已删除好友的聊天记录
2. 收藏更多表情(原辅助模块)
3. 屏蔽(类)回执消息(如:作业消息)通知
4. 自定义复读次数
5. 修复偶发性的莫名其妙好友全部被标记为删除的bug
6. 加好友(群)自定义来源
7. 禁用特别关心长震动
8. 去你大爷的TX地图/QQ/内置浏览器
9. (反)单向好友检测

#### 不会支持的功能:
- 抢红包
- 群发图片或其他类型消息

