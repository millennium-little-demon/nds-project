package com.ck.nds.lexer.matcher

import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.lexer.NdsMatcher
import com.ck.nds.token.NdsLiteralToken
import com.ck.nds.token.NdsLiteralType
import com.ck.nds.token.NdsToken

/**
 *
 * 匹配 [NdsLiteralType] 类型的token
 *
 * @author 陈坤
 * 2022/5/29 22:50
 */
object StringLiteralMatcher : NdsMatcher {

    override fun match(lineNumberCharArray: LineNumberCharArray): NdsToken? {
        if (lineNumberCharArray.peek(0) != '"') return null

        var temp = 1
        var f: Boolean
        do {
            f = lineNumberCharArray.match(temp, '"')
            temp += 1
        } while (!f)

        if (temp < 2) return null

        val str = lineNumberCharArray.read(temp).run { substring(1, this.length - 1) }
        return NdsLiteralToken(str, NdsLiteralType.STRING_LITERAL, lineNumberCharArray)
    }


}