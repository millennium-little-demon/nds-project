package com.ck.nds.lexer

/**
 *
 * @author 陈坤
 * 2022/5/29 21:19
 */
internal class LineNumberCharArray(private val charArray: CharArray) {

    companion object {
        private const val EMPTY = (-1).toChar()
    }

    private var cursor = 0
    var lineNumber = 1
    var lastCursor = 0

    fun match(i: Int, ch: Char): Boolean {
        return when (peek(i)) {
            ch -> true
            EMPTY -> false
            else -> false
        }
    }

    fun match(str: String): Boolean {
        if (str.isEmpty()) return false

        return str.withIndex().all {(index, ch) ->  match(index, ch) }
    }

    fun peek(i: Int): Char {
        if (cursor + i >= charArray.size) {
            return EMPTY
        }

        return charArray[cursor + i]
    }

    fun read(size: Int): String {
        if (size <= 0) throw RuntimeException()

        lastCursor = cursor
        cursor = if (cursor + size < charArray.size) cursor + size else charArray.size

        val copyOf = charArray.copyOfRange(lastCursor, cursor)
        lineNumber += copyOf.count { it == '\n' }
        return copyOf.concatToString()
    }


}