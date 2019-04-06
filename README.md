# QNotified
QNotified（**QQ删好友通知** 模块，以下简称本软件）是一款依赖 Xposed 框架运行的模块，本软件通过定期刷新QQ好友列表判断是否有好友删除用户，并在检测到被好友删除后向用户发出通知提醒。

QNotified is a Xposed module that automatically refreshes friend list and tell(notify) user which friend had deleted him/her.

-  **适配QQ版本** : 国内版QQ 7.6.0-7.9.9
-  **使用方法** : 在Xposed Installer 激活模块后，在QQ "联系人-好友" 列表最下方有 "历史好友" 按钮，点击进入可查看历史好友 **(从安装并激活日起生效，无法查看安装本模块以前删除自己的好友!)** 。激活模块后自动检测，无需手动配置
- 计划后期会适配TIM
- 计划后期会兼容ColorQQ等美化插件
# 准备高考,暂停更新