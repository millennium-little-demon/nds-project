package com.ck.nds.lexer.matcher

import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.lexer.Matcher
import com.ck.nds.token.Token
import com.ck.nds.token.TokenType

/**
 *
 * @author 陈坤
 * 2022/5/29 23:10
 */
internal object SkipMatcher : Matcher {
    private val skipCharArr = charArrayOf(
        ' ', '\n', '\t'
    )

    override fun match(charArray: LineNumberCharArray): Token? {
        while (charArray.peek(0) in skipCharArr) {
            charArray.read(1)
        }

        if (charArray.peek(0) == '/' && charArray.peek(1) == '/') {
            var temp = 2
            while (!charArray.match(temp, '\n')) {
                temp += 1
            }
            charArray.read(temp + 1)
            return Token("", charArray, TokenType.SKIP)
        }

        return null
    }
}