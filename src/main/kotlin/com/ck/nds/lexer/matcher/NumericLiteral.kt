package com.ck.nds.lexer.matcher

import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.lexer.Matcher
import com.ck.nds.token.Token
import com.ck.nds.token.TokenType

/**
 *
 * @author 陈坤
 * 2022/5/30 14:46
 */
internal object NumericLiteral : Matcher {

    override fun match(charArray: LineNumberCharArray): Token? {
        var temp = 0
        while (charArray.peek(temp).isDigit()) {
            temp += 1
        }

        if (temp == 0) return null
        
        val num = charArray.read(temp)
        return Token(num, charArray, TokenType.NUMERIC_LITERAL)
    }
}