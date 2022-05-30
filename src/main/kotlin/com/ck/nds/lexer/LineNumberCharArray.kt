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
    private var lastCursor = 0
    private var isNewLine = false

    var linePosition = 0
    var lineNumber = 1


    fun match(i: Int, ch: Char): Boolean {
        return when (peek(i)) {
            ch -> true
            EMPTY -> false
            else -> false
        }
    }

    fun match(str: String): Boolean {
        if (str.isEmpty()) return false

        return str.withIndex().all { (index, ch) -> match(index, ch) }
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

        val copyStr = charArray.copyOfRange(lastCursor, cursor).concatToString()
        lineNumber += copyStr.count { it == '\n' }

        if (isNewLine) {
            linePosition = copyStr.length
            isNewLine = false
        } else {
            linePosition += copyStr.length
        }

        val lastIndex = copyStr.lastIndexOf('\n')
        if (lastIndex != -1) {
            isNewLine = true
        }
        return copyStr
    }


}