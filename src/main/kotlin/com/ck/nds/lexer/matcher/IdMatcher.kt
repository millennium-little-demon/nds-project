package com.ck.nds.lexer.matcher

import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.lexer.Matcher
import com.ck.nds.token.Token
import com.ck.nds.token.TokenType

/**
 *
 * @author 陈坤
 * 2022/5/30 17:32
 */
internal object IdMatcher : Matcher {

    override fun match(charArray: LineNumberCharArray): Token? {
        var ch = charArray.peek(0)
        if (!(ch.isLetter() && ch.isLowerCase())) return null

        var temp = 1
        ch = charArray.peek(temp)
        while (ch.isLetterOrDigit() || ch == '_' || ch == '$') {
            temp += 1
            ch = charArray.peek(temp)
        }

        val readStr = charArray.read(temp)
        return Token(readStr, charArray, TokenType.ID)
    }
}