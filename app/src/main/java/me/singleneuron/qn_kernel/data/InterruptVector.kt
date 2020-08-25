package me.singleneuron.qn_kernel.data

import androidx.core.util.Supplier

interface InterruptVector<T> {

    val SYSTEM_CALL_NUMBER: Int
    val SYSTEM_CALL: Supplier<T>

}