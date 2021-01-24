# 贡献指南

**首先，欢迎您为QNotified这个项目做出贡献。**

## 分支约定

不管是直接 Push 代码还是提交 Pull Request，都必须使 commit 指向 master 分支。

## Commit 相关

1. 禁止中文/拼音
2. 简洁明了
3. 一个Commit做一件事情
4. 请勿在Commit附上任何有关[skip ci]的字段

## 开发

1. 请确认您的编辑器支持EditorConfig，否则请注意您的编码、行位序列及其其他事项。

2. 在原则上代码风格建议遵循[Google Java Style](https://google.github.io/styleguide/javaguide.html)[中文翻译](https://github.com/fantasticmao/google-java-style-guide-zh_cn)

3. 每位开发者的代码风格应保持一致

4. 以UTF-8编码，以LF作为行位序列

5. 命名方面应
    1. 禁止拼音

    2. 使用大写字母分隔单词
6. 使用4个空格缩进

7. 大括号放应同一行上

8. 代码请务必格式化

9. 原则上建议将自己的代码放在自己的包里，**强烈不建议放入nil.nadph.qnotified中**，另外，应注意的是，如果你创建了自己的包，**一定要记得修改proguard-rules.pro**

10. 针对适配指定QQ/TIM版本的methods扔进[QQConfigTable.kt](app/src/main/java/me/singleneuron/qn_kernel/tlb/QQConfigTable.kt)或[TIMConfigTable.kt](app/src/main/java/me/singleneuron/qn_kernel/tlb/TIMConfigTable.kt)

11. 原则上建议添加代码头

12. **在任何时候，您都不应该随意更改[build.gradle](build.gradle)，特别是升级 `com.android.tools.build:gradle` 版本**

## 其他

如还有疑问，可直接PM Telegram机器人[QNotified](https://t.me/QNotified_bot)
