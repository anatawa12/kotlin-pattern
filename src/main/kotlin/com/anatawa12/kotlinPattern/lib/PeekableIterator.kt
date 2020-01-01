package com.anatawa12.kotlinPattern.lib

/**
 * Created by anatawa12 on 2020/01/01.
 */
class PeekableIterator<T>(val baseItr: Iterator<T>) : Iterator<T> {
    private var cached: Any? = NON_CACHED

    override fun hasNext(): Boolean = cached !== NON_CACHED || baseItr.hasNext()

    override fun next(): T {
        val result = peek()
        cached = NON_CACHED
        return result
    }

    @Suppress("UNCHECKED_CAST")
    fun peek(): T {
        if (cached === NON_CACHED)
            cached = baseItr.next()
        return cached as T
    }

    companion object {
        private val NON_CACHED = Any()
    }
}
