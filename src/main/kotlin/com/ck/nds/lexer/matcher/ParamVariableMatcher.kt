package com.ck.nds.lexer.matcher

import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.lexer.NdsMatcher
import com.ck.nds.token.NdsParamVariableToken
import com.ck.nds.token.NdsToken

/**
 *
 * @author 陈坤
 * 2022/6/1
 */
object ParamVariableMatcher : NdsMatcher {

    override fun match(lineNumberCharArray: LineNumberCharArray): NdsToken? {
        if (lineNumberCharArray.peek(0) != ':') return null
        if (!lineNumberCharArray.peek(1).isLowerCase()) return null

        var tempIndex = 2
        var ch = lineNumberCharArray.peek(tempIndex)
        while (ch.isLetterOrDigit() || ch == '.' || ch == '_') {
            tempIndex += 1
            ch = lineNumberCharArray.peek(tempIndex)
        }

        ch = lineNumberCharArray.peek(tempIndex - 1)
        if (!(ch.isLetterOrDigit() || ch == '_')) return null

        val readStr = lineNumberCharArray.read(tempIndex)
        return NdsParamVariableToken(readStr, lineNumberCharArray)
    }
}