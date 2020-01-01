package com.anatawa12.kotlinPattern.lib

/**
 * Created by anatawa12 on 2020/01/01.
 */

fun <T> List(size: Int, init: (index: Int) -> T): List<T> = ArrayList(Array<Any?>(size, init))

fun <T> Array<T>.toRefList(): List<T> = ArrayList(this)

@Suppress("UNCHECKED_CAST")
internal class ArrayList<T>(val baseArray: Array<out Any?>) : List<T> {
    override val size: Int
        get() = baseArray.size

    override fun contains(element: T): Boolean = element in baseArray

    override fun containsAll(elements: Collection<T>): Boolean = elements.all { it in baseArray }

    override fun get(index: Int): T {
        if (index !in indices) throw IndexOutOfBoundsException("Index: $index")
        return baseArray[index] as T
    }

    override fun indexOf(element: T): Int = baseArray.indexOf(element)

    override fun isEmpty(): Boolean = baseArray.isEmpty()

    override fun lastIndexOf(element: T): Int = baseArray.lastIndexOf(element)

    override fun iterator(): Iterator<T> = listIterator()

    override fun listIterator(): ListIterator<T> = listIterator(0)

    override fun listIterator(index: Int): ListIterator<T> {
        if (index !in 0..size) throw IndexOutOfBoundsException("Index: $index")
        return ListItr(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        if (fromIndex !in indices) throw IndexOutOfBoundsException("fromIndex: $fromIndex")
        if (toIndex !in indices) throw IndexOutOfBoundsException("toIndex: $toIndex")
        require(fromIndex <= toIndex) { "fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")" }

        return SubList(fromIndex, toIndex)
    }

    override fun toString(): String = joinToString(separator = ", ", prefix = "[", postfix = "]")

    override fun equals(other: Any?): Boolean {
        if (other !is List<*>) return false
        if (size != other.size) return false
        return asSequence().zip(other.asSequence()).all { (a, b) -> a == b }
    }

    override fun hashCode(): Int {
        var hashCode = 1
        for (e in this) hashCode = 31 * hashCode + (e?.hashCode() ?: 0)
        return hashCode
    }

    private inner class SubList(val fromIndex: Int, toIndex: Int) : List<T> {
        override val size = toIndex - fromIndex

        private val superRange = fromIndex until toIndex

        override fun isEmpty(): Boolean = size == 0

        override fun contains(element: T): Boolean = indexOf(element) != -1

        override fun containsAll(elements: Collection<T>): Boolean = elements.all { it in this }

        override fun get(index: Int): T {
            if (index !in indices) throw IndexOutOfBoundsException("Index: $index")
            return this@ArrayList[fromIndex + index]
        }

        override fun indexOf(element: T): Int {
            val superIndex = this@ArrayList.indexOf(element)
            return if (superIndex in superRange) superIndex - fromIndex else -1
        }

        override fun lastIndexOf(element: T): Int {
            val superIndex = this@ArrayList.lastIndexOf(element)
            return if (superIndex in superRange) superIndex - fromIndex else -1
        }

        override fun iterator(): Iterator<T> = listIterator()

        override fun listIterator(): ListIterator<T> = listIterator(0)

        override fun listIterator(index: Int): ListIterator<T> {
            if (index !in 0..size) throw IndexOutOfBoundsException("Index: $index")
            return ListItr(index + fromIndex, fromIndex, size)
        }

        override fun subList(fromIndex: Int, toIndex: Int): List<T> {
            if (fromIndex !in indices) throw IndexOutOfBoundsException("fromIndex: $fromIndex")
            if (toIndex !in indices) throw IndexOutOfBoundsException("toIndex: $toIndex")
            require(fromIndex <= toIndex) { "fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")" }
            return SubList(this.fromIndex + fromIndex, fromIndex + toIndex)
        }

        override fun toString(): String = joinToString(separator = ", ", prefix = "[", postfix = "]")

        override fun equals(other: Any?): Boolean {
            if (other !is List<*>) return false
            if (size != other.size) return false
            return asSequence().zip(other.asSequence()).all { (a, b) -> a == b }
        }

        override fun hashCode(): Int {
            var hashCode = 1
            for (e in this) hashCode = 31 * hashCode + (e?.hashCode() ?: 0)
            return hashCode
        }
    }

    private inner class ListItr(private var cursor: Int, val baseIndex: Int = 0, listSize: Int = size) :
        ListIterator<T> {
        val range = 0 until listSize
        override fun hasNext(): Boolean = nextIndex() in range

        override fun hasPrevious(): Boolean = previousIndex() in range

        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()
            return this@ArrayList[cursor++]
        }

        override fun previous(): T {
            if (!hasPrevious()) throw NoSuchElementException()
            return this@ArrayList[cursor--]
        }

        override fun nextIndex(): Int = cursor - baseIndex

        override fun previousIndex(): Int = cursor - 1 - baseIndex
    }
}
