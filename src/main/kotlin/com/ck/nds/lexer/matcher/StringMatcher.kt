package com.ck.nds.lexer.matcher

import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.lexer.Matcher
import com.ck.nds.token.Token
import com.ck.nds.token.TokenType

/**
 *
 * @author 陈坤
 * 2022/5/29 22:50
 */
internal object StringMatcher : Matcher {

    override fun match(charArray: LineNumberCharArray): Token? {
        if (charArray.peek(0) != '"') return null

        var temp = 1
        var f: Boolean
        do {
            f = charArray.match(temp, '"')
            temp += 1
        } while (!f)

        if (temp < 2) return null

        val str = charArray.read(temp).run { substring(1, this.length - 1) }
        return Token(str, charArray, TokenType.STRING_LITERAL)
    }


}