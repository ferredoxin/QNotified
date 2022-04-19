# QNotified

![QNotified logo](https://raw.githubusercontent.com/ferredoxin/QNotified/master/docs/title.png)

---

[![Build Status](https://dev.azure.com/Cryolitia/QNotified/_apis/build/status/ferredoxin.QNotified?branchName=master)](https://dev.azure.com/Cryolitia/QNotified/_build/latest?definitionId=1&branchName=master)
[![license](https://img.shields.io/github/license/ferredoxin/QNotified.svg)](https://www.gnu.org/licenses/agpl-3.0.html)
[![GitHub release](https://img.shields.io/github/release/ferredoxin/QNotified.svg)](https://github.com/ferredoxin/QNotified/releases/latest)

QNotified 是一个旨在使 QQ 变得更好用的开源 Xposed 模块

**[持续集成版本下载](https://install.appcenter.ms/orgs/qnotifieddev/apps/qnotified/distribution_groups/alpha)**

## 使用方法

激活本模块后，在 QQ/TIM 自带设置中点击 QNotified 即可开关对应功能。

## 一切开发旨在学习，请勿用于非法用途

-   本项目保证永久开源，欢迎提交 PR，但是请不要提交用于非法用途的功能。
-   如果某功能被大量运用于非法用途或严重侵害插件使用者权益，那么该功能将会被移除。
-   本模块完全免费开源, 近期发现模块倒卖现象严重,请勿上当
-   鉴于项目的特殊性，开发团队可能在任何时间**停止更新**或**删除项目**

### 许可证

-   [EULA](https://github.com/qwq233/License/blob/master/v2/LICENSE.md)

```
版权所有©2022 gao_cai_sheng <qwq233@qwq2333.top, qwq2333.top>

允许在遵守 CC BY-NC-SA 4.0 协议的同时，复制和分发此协议文档的逐字记录副本，且允许对其进行更改，但必须保留其版权信息与原作者。如果您提出申请特殊权限，协议作者可在其口 头或书面授予任何人任何但不包括以盈利为目的的使用本协议的权利。

请务必仔细阅读和理解通用许可协议书中规定的所有权利和限制。在使用前，您需要仔细阅读并决定接受或不接受本协议的条款。除非或直至您接受本协议的条款，否则本作品及其相关副本、相关程序代码或相关资源不得在您的任何终端上下载、安装或使用。

您一旦下载、使用本作品及其相关副本、相关程序代码或相关资源，即表示您同意接受本协议各项条款的约束。如您不同意本协议中的条款，您则应当立即删除本作品、附属资源及其相关源代码。

本作品权利只许可使用，而不出售。
```

## 功能介绍

<details>
  <summary>目前已开发功能</summary>

1. 隐藏消息列表小程序入口
2. 去除回复自动 at
3. 语音消息转发
4. 强制默认气泡
5. 以图片方式打开闪照(原辅助模块)
6. 以图片方式打开表情包(原 QQ 净化)
7. Ark(json)/StructMsg(xml)卡片消息(注 1,原 BUG 复读机)
8. 复读机(+1,原 QQ 复读机)
9. 被删好友通知(可导出好友列表)
10. 防撤回
11. 签到文本化,隐藏礼物动画
12. 简洁模式圆头像(原花 Q)
13. 自定义电量
14. 转发消息点击头像查看原消息发送者和所在群
15. 下载重定向(原 QQ 净化)
16. 屏蔽 \@全体成员 或者 群红包 的通知(不影响接收消息,不影响某些插件抢红包功能)
17. 屏蔽 QQ 更新提示
18. 屏蔽 QQ 空间点赞通知
19. 禁止聊天界面输入＄自动弹出 选择赠送对象 窗口
20. 直接打开不可通过 QQ 号码搜索到用户的资料卡
21. 屏蔽秀图
22. 显示进行禁言操作的管理员(查看哪个管理员禁言了你)
23. 去除夜间模式聊天界面深色遮罩
24. 直接打开指定用户资料卡(无视隐藏 QQ 号)
25. 自定义+1 图标
26. 群发文本消息(注 1)
27. 显示具体消息数量而不是 99+(原花 Q)
28. 隐藏侧滑群应用
29. 隐藏好友侧滑亲密抽屉
30. 使用系统相机
31. 使用系统相册
32. 使用系统文件
33. 聊天自动发送原图
34. 隐藏小红点
35. 隐藏群在线人数
36. 隐藏群总人数
37. 批量撤回消息
38. 隐藏移出群助手提示
39. 修改消息左滑回复
40. at 界面以管理员优先顺序排序
41. 自动续火
42. 静默指定类型通知
43. 聊天字数统计
44. 自定义钱包显示余额
45. 显示消息发送者 QQ 号与时间
46. 聊天自动发送/接收原图

注 1: 卡片消息及群发文本这两个功能因大量被用于广告引流而被加以限制

</details>

### [计划或正在开发的功能](https://github.com/ferredoxin/QNotified/projects/2)

### 不会支持的功能

-   抢红包
-   群发图片或其他类型消息

## 发行渠道说明

<details>

QNotified 将为分`Beta`、`Alpha`、`Canary`三个版本：`Beta`版本为重大功能变更或长期积累更新，发布频率由开发组决定，包含上次`Beta`版至今的所有功能更新及 Bug 修复，但可能不包括尚未稳定或正在开发中的功能，原则上更新频率将大于两周一次；`Alpha`版本为每周积累更新，在每周周末由开发组发布，包含发布时的全部更新，可能包含不稳定功能或异常问题；`Canary`版本为每 commit 自动更新，可能不包含外围文档或 CI 流程更新，不会编写任何更新文档或说明，具体更新内容可在[Github](https://github.com/ferredoxin/QNotified/commits/master)自行查看，本更新由开源的流程（包括 Azure 和开发组自研 Bot）自动编译发布，可能包含严重的功能及行为异常。

开发组不限制用户选择自己需要的版本，同时也不为任何版本产生的任何后果承担任何责任（详情请见[QNotified EULA](https://github.com/ferredoxin/QNotified/blob/master/app/src/main/assets/eula.md)），但希望各位用户各取所需，根据自己的能力范围选择适合自己的版本。

QNotified 的版本号组成为`x.y.z.w`，正常情况下`x`位将一直保持为 0，`Beta`版本更新会将`y`位+`1`并使 z 位归零，`Alpha`版本更新会将`z`位+`1`，所有版本更新的`w`位都会是触发此次更新的 Commit 的 hash 的前 7 位。

1. [@QNotified 频道](https://t.me/QNotified) 将只发布`Beta`版和`Alpha`版更新。

2. [@QNotified_CI](https://t.me/QNotified_CI) 频道将只发布`Canary`版更新。

3. [Github Release](https://github.com/ferredoxin/QNotified/releases/) 将只发布`Beta`版更新。

4. [App Center - Alpha](https://install.appcenter.ms/orgs/qnotifieddev/apps/qnotified/distribution_groups/alpha) 发布`CI`版本更新；[App Center - Weekly](https://install.appcenter.ms/orgs/qnotifieddev/apps/qnotified/distribution_groups/weekly) 发布`Alpha`版本更新。两个轨道均开放给所有人自由下载。

5. [Google Play](https://play.google.com/store/apps/details?id=nil.nadph.qnotified) 将发布`Beta`版和`Alpha`版更新。其中`Beta`版更新将在 Google Play 上以正式版轨道发布，`Alpha`版更新将在 Google Play 上以开放性测试轨道发布。任何可以登录 Google Play 的人都可自由加入或退出 Google Play 上 QNotified 的开放测试。

6. [Xposed 仓库](https://repo.xposed.info/module/nil.nadph.qnotified) 将发布`Beta`版和`Alpha`版更新。其中`Beta`版更新将被标注为 Stable，`Alpha`版更新将被标注为 Beta。

7. [LSPosed 仓库](https://github.com/Xposed-Modules-Repo/nil.nadph.qnotified/releases/) 将发布所有版本更新，其中`CI`版本更新将被标注为 Pre-release。

</details>

## 开始贡献

-   [CONTRIBUTING](https://github.com/ferredoxin/QNotified/blob/master/CONTRIBUTING.md)

## 赞助

-   由于项目的特殊性，我们不接受任何形式的捐赠，但是我们希望有更多的人能够参与本项目的开发

## [通用许可协议](https://github.com/qwq233/License/blob/master/v2/LICENSE.md)
