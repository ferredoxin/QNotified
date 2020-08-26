package me.singleneuron.qn_kernel.data

import androidx.core.util.Supplier
import me.singleneuron.data.PageFaultHighPerformanceFunctionCache

abstract class PageFaultInterruptVector<T : Any>: InterruptVector<T> {

    abstract val cache: PageFaultHighPerformanceFunctionCache<T>
    final override val SYSTEM_CALL: Supplier<T> = Supplier{
        cache.getValue()
    }

}