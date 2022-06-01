package com.ck.nds.lexer.matcher

import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.lexer.NdsMatcher
import com.ck.nds.token.NdsNormalLiteralToken
import com.ck.nds.token.NdsToken

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
            return NdsNormalLiteralToken(readStr, lineNumberCharArray)
        }



        return null
    }
}