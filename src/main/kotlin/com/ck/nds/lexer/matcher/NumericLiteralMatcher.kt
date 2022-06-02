package com.ck.nds.lexer.matcher

import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.lexer.NdsMatcher
import com.ck.nds.token.NdsLiteralToken
import com.ck.nds.token.NdsLiteralType
import com.ck.nds.token.NdsToken

/**
 *
 * @author 陈坤
 * 2022/6/3
 */
object NumericLiteralMatcher : NdsMatcher {

    override fun match(lineNumberCharArray: LineNumberCharArray): NdsToken? {
        var numSize = 0
        while (lineNumberCharArray.peek(numSize).isDigit()) {
            numSize += 1
        }
        if (numSize == 0) return null

        val str = lineNumberCharArray.read(numSize)

        return NdsLiteralToken(str, NdsLiteralType.NUMERIC_LITERAL, lineNumberCharArray)
    }
}