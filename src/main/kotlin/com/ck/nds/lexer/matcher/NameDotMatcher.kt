package com.ck.nds.lexer.matcher

import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.lexer.Matcher
import com.ck.nds.token.Token
import com.ck.nds.token.TokenType

/**
 *
 * @author 陈坤
 * 2022/5/30 10:24
 */
internal object NameDotMatcher : Matcher {

    override fun match(charArray: LineNumberCharArray): Token? {
        var ch = charArray.peek(0)
        if (!ch.isLetter()) return null

        var temp = 1
        ch = charArray.peek(temp)
        while (ch.isLetter() || ch == '.') {
            temp += 1
            ch = charArray.peek(temp)
        }
        if (!charArray.peek(temp - 1).isLetter()) return null

        val readStr = charArray.read(temp)
        return Token(readStr, charArray, TokenType.NAME_DOT_LITERAL)
    }
}