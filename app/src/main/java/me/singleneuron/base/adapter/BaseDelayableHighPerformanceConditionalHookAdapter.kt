package me.singleneuron.base.adapter

import me.singleneuron.data.PageFaultHighPerformanceFunctionCache
import nil.nadph.qnotified.SyncUtils

/*
请在大量调用的情况下使用此类并标注为重启QQ后生效
使用此类后极大幅度提高了简洁模式圆头像的性能，使用此类前每次调用时间为1~6ms，使用后每次调用20μs内完成
测试环境：Mi9 PixelExperience EdXposed(Sandhook)
 */
abstract class BaseDelayableHighPerformanceConditionalHookAdapter @JvmOverloads constructor(string:String, proc:Int = SyncUtils.PROC_MAIN) : BaseDelayableConditionalHookAdapter(string,proc) {

    //protected var highPerformanceEnabled by Delegates.notNull<Boolean>()
    protected var highPerformanceEnabledCache : PageFaultHighPerformanceFunctionCache<Boolean> = PageFaultHighPerformanceFunctionCache { super.checkEnabled() }

    override fun checkEnabled(): Boolean {
        return highPerformanceEnabledCache.getValue()
        /*return try {
            highPerformanceEnabled
        } catch (e:IllegalStateException) {
            //Utils.log(e)
            highPerformanceEnabled = super.checkEnabled()
            highPerformanceEnabled
        }*/
    }

}