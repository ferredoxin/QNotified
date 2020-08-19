package me.singleneuron.base

interface ConditionalHook {

    val condition : ()->Boolean
    fun getCondition() : Boolean {
        return condition()
    }

}