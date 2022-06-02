package com.ck.nds.lexer.matcher

import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.lexer.NdsMatcher
import com.ck.nds.token.*

/**
 *
 * @author 陈坤
 * 2022/6/1
 */
object NormalLiteralMatcher : NdsMatcher {

    override fun match(lineNumberCharArray: LineNumberCharArray): NdsToken? {

        var tempIndex = 0
        var ch: Char
        while (true) {
            ch = lineNumberCharArray.peek(tempIndex)
            if (
                ch.isLetterOrDigit()
                || ch == '.'
                || ch == '$'
                || ch == '_'
                || ch == '*'
                || ch == '='
            ) tempIndex += 1 else break
        }

        if (tempIndex > 0) {
            val readStr = lineNumberCharArray.read(tempIndex)
            return typeOf(readStr, lineNumberCharArray)
        }

        return null
    }

    private fun typeOf(str: String, lineNumberCharArray: LineNumberCharArray): NdsToken {
        return when (str) {
            "true", "false" -> NdsLiteralToken(str, NdsLiteralType.BOOLEAN_LITERAL, lineNumberCharArray)
            "null" -> NdsLiteralToken(str, NdsLiteralType.NULL_LITERAL, lineNumberCharArray)
            else -> NdsNormalLiteralToken(str, NdsNormalLiteralType.GENERIC, lineNumberCharArray)
        }

    }
}