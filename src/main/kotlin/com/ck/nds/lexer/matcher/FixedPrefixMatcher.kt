package com.ck.nds.lexer.matcher

import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.lexer.NdsMatcher
import com.ck.nds.token.NdsFixedPreFixToken
import com.ck.nds.token.NdsFixedPrefixType
import com.ck.nds.token.NdsToken

/**
 * 匹配 [NdsFixedPrefixType] 类型的token
 *
 * @author 陈坤
 * 2022/6/1
 */
object FixedPrefixMatcher : NdsMatcher {

    private val listMatcher = arrayOf(
        { lineNumberCharArray: LineNumberCharArray -> matchColonPrefixToken(lineNumberCharArray) },
        { lineNumberCharArray: LineNumberCharArray -> matchDollarPrefixToken(lineNumberCharArray) },
    )

    override fun match(lineNumberCharArray: LineNumberCharArray): NdsToken? {

        for (function in listMatcher) {
            val token = function(lineNumberCharArray)

            if (token == null) {
                continue
            } else {
                return token
            }
        }

        return null
    }

    private fun matchColonPrefixToken(lineNumberCharArray: LineNumberCharArray): NdsFixedPreFixToken? {
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
        return NdsFixedPreFixToken(readStr, NdsFixedPrefixType.COLON_PREFIX, lineNumberCharArray)
    }

    private fun matchDollarPrefixToken(lineNumberCharArray: LineNumberCharArray): NdsFixedPreFixToken? {
        if (lineNumberCharArray.peek(0) != '$') return null
        if (!lineNumberCharArray.peek(1).isLowerCase()) return null

        var tempIndex = 2
        var ch = lineNumberCharArray.peek(tempIndex)
        while (ch.isLetterOrDigit() || ch == '_') {
            tempIndex += 1
            ch = lineNumberCharArray.peek(tempIndex)
        }

        ch = lineNumberCharArray.peek(tempIndex - 1)
        if (!(ch.isLetterOrDigit() || ch == '_')) return null

        val readStr = lineNumberCharArray.read(tempIndex)
        return NdsFixedPreFixToken(readStr, NdsFixedPrefixType.DOLLAR_PREFIX, lineNumberCharArray)
    }
}