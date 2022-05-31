package com.ck.nds.m2.lexer

import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.m2.NdsToken

/**
 * 简单字符匹配器
 *
 * @author 陈坤
 * 2022/5/31
 */
class SimpleLiteralMatcher(
    private val string: String,
    private val block: (String, LineNumberCharArray) -> NdsToken,
) : NdsMatcher {

    override fun match(lineNumberCharArray: LineNumberCharArray): NdsToken? {
        if (string.isBlank() || !lineNumberCharArray.match(string)) return null

        val readStr = lineNumberCharArray.read(string.length)
        return block(readStr, lineNumberCharArray)
    }

    override fun toString(): String {
        return "SimpleLiteralMatcher(string='$string')"
    }

}
