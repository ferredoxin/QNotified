package me.singleneuron.data

class PageFaultHighPerformanceFunctionCache<T:Any>(function: () -> T) {

    private lateinit var mValue : T
    private val mFunction : ()->T = function

    fun getValue():T {
        return try {
            mValue
        } catch (e:UninitializedPropertyAccessException) {
            //Utils.log(e)
            mValue = mFunction()
            mValue
        }
    }

}