package me.singleneuron.qn_kernel.service

import androidx.core.content.pm.PackageInfoCompat
import me.singleneuron.data.PageFaultHighPerformanceFunctionCache
import me.singleneuron.qn_kernel.data.InterruptVector
import me.singleneuron.qn_kernel.data.PageFaultInterruptVector
import nil.nadph.qnotified.util.Utils

object InterruptServiceRoutine {

    const val GET_APP_NAME = 1
    const val GET_VERSION_CODE = 2

    private val interruptVectorTable = HashMap<Int, InterruptVector<out Any>>()

    private val getHostAppNameService: InterruptVector<String> = object : PageFaultInterruptVector<String>() {
        override val cache = PageFaultHighPerformanceFunctionCache {
            Utils.getHostInfo().applicationInfo.loadLabel(Utils.getPackageManager()).toString()
        }
        override val SYSTEM_CALL_NUMBER: Int = GET_APP_NAME
    }

    private val getHostVersionCodeService: InterruptVector<Long> = object : PageFaultInterruptVector<Long>() {
        override val cache = PageFaultHighPerformanceFunctionCache {
            val pi = Utils.getHostInfo(Utils.getApplication())
            return@PageFaultHighPerformanceFunctionCache PackageInfoCompat.getLongVersionCode(pi)
        }
        override val SYSTEM_CALL_NUMBER: Int = GET_VERSION_CODE
    }

    init {
        interruptVectorTable[GET_APP_NAME] = getHostAppNameService
        interruptVectorTable[GET_VERSION_CODE] = getHostVersionCodeService
    }

    fun <T> interrupt(systemCallNumber: Int): T{
        try {
            return interruptVectorTable[systemCallNumber]!!.SYSTEM_CALL.get() as T
        } catch (e:Exception) {
            throw RuntimeException("QN Kernel error: ${e.message}")
        }
    }

}