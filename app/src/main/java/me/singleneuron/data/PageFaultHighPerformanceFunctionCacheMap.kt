package me.singleneuron.data

class PageFaultHighPerformanceFunctionCacheMap : MutableMap<String, Any> {

    private val hashMap : HashMap<String, Any> = HashMap()
    private val functionHashMap : HashMap<String, ()->Any> = HashMap()

    override val size: Int
        get() = functionHashMap.size

    override fun containsKey(key: String): Boolean {
        return functionHashMap.containsKey(key)
    }

    @Deprecated("Avoid to use this method, it will cache all values.")
    override fun containsValue(value: Any): Boolean {
        cacheAll()
        return functionHashMap.contains(value)
    }

    override fun get(key: String): Any? {
        return try {
            hashMap[key]!!
        } catch (e:Exception) {
            hashMap[key] = functionHashMap[key]!!.invoke()
            hashMap[key]
        }
    }

    override fun isEmpty(): Boolean {
       return functionHashMap.isEmpty()
    }

    @Deprecated("Avoid to use this method, it will cache all values.")
    override val entries: MutableSet<MutableMap.MutableEntry<String, Any>>
        get() {
            cacheAll()
            return hashMap.entries
        }

    override val keys: MutableSet<String>
        get() = functionHashMap.keys

    @Deprecated("Avoid to use this method, it will cache all values.")
    override val values: MutableCollection<Any>
        get() {
            cacheAll()
            return hashMap.values
        }

    override fun clear() {
        functionHashMap.clear()
        hashMap.clear()
    }

    override fun put(key: String, value: Any): Any? {
        putFunctional(key) {value}
        return null
    }

    override fun putAll(from: Map<out String, Any>) {
        from.keys.forEach {
            putFunctional(it) {from[it]!!}
        }
    }

    override fun remove(key: String): Any? {
        functionHashMap.remove(key)
        hashMap.remove(key)
        return null
    }

    fun putFunctional(key: String, function: ()->Any) {
        functionHashMap[key] = function
    }

    fun cacheAll() {
        functionHashMap.keys.forEach {
            hashMap[it] = functionHashMap[it]!!.invoke()
        }
    }

    fun getBoolean(key: String): Boolean {
        return this[key] as Boolean
    }

    fun getInt(key: String): Int {
        return this[key] as Int
    }

    fun getLong(key: String): Long {
        return this[key] as Long
    }

    fun getFloat(key: String): Float {
        return this[key] as Float
    }

    fun getStringSet(key: String): MutableSet<String> {
        return this[key] as MutableSet<String>
    }

    fun getString(key: String): String {
        return this[key] as String
    }

    fun putLong(key: String, function: ()->Long) {
        functionHashMap[key] = function
    }

    fun putInt(key: String, function: ()->Int) {
        functionHashMap[key] = function
    }

    fun putBoolean(key: String, function: ()->Boolean) {
        functionHashMap[key] = function
    }

    fun putStringSet(key: String, function: () -> MutableSet<String>) {
        functionHashMap[key] = function
    }

    fun putFloat(key: String, function: ()->Float) {
        functionHashMap[key] = function
    }

    fun putString(key: String, function: () -> String) {
        functionHashMap[key] = function
    }

}