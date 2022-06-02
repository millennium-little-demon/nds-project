package com.ck.nds.lexer.matcher

import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.lexer.NdsMatcher
import com.ck.nds.token.NdsFixedPrefixToken
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
        { lineNumberCharArray: LineNumberCharArray -> match0(lineNumberCharArray, NdsFixedPrefixType.COLON_PREFIX) },
        { lineNumberCharArray: LineNumberCharArray -> match0(lineNumberCharArray, NdsFixedPrefixType.DOLLAR_PREFIX) },
        { lineNumberCharArray: LineNumberCharArray -> match0(lineNumberCharArray, NdsFixedPrefixType.AND_PREFIX) },
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

    private fun match0(lineNumberCharArray: LineNumberCharArray, fixedPrefixType: NdsFixedPrefixType): NdsToken? {
        // 检查前缀是否匹配, 否则返回 null
        if (!lineNumberCharArray.match(fixedPrefixType.prefixText)) return null
        // 前缀后面第一个字符必须是小写字符
        if (!lineNumberCharArray.peek(fixedPrefixType.prefixSize() + 1).isLowerCase()) return null

        var tempIndex = fixedPrefixType.prefixSize()
        var ch = lineNumberCharArray.peek(tempIndex)
        while (ch.isLetterOrDigit() || ch in fixedPrefixType.containChar) {
            tempIndex += 1
            ch = lineNumberCharArray.peek(tempIndex)
        }

        val readStr = lineNumberCharArray.read(tempIndex)
        return NdsFixedPrefixToken(readStr, fixedPrefixType, lineNumberCharArray)
    }

}