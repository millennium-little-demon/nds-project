package com.ck.nds.lexer

import com.ck.nds.token.Token
import com.ck.nds.token.TokenType

/**
 *
 * @author 陈坤
 * 2022/5/29 20:01
 */
internal fun interface Matcher {

    fun match(charArray: LineNumberCharArray): Token?


    companion object {

        fun simpleMatcher(tokenType: TokenType) = Matcher { charArray ->
            if (tokenType.keyword.isBlank()) return@Matcher null

            val f = charArray.match(tokenType.keyword)

            if (!f) return@Matcher null
            val readStr = charArray.read(tokenType.keyword.length)
            return@Matcher Token(readStr, charArray, tokenType)
        }

    }

}


