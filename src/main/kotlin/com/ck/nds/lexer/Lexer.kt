package com.ck.nds.lexer

import com.ck.nds.lexer.Matcher.Companion.simpleMatcher
import com.ck.nds.lexer.matcher.*
import com.ck.nds.token.Token
import com.ck.nds.token.TokenType

/**
 *
 * @author 陈坤
 * 2022/5/29 14:23
 */
internal class Lexer(string: String) {

    companion object {

        private val matcherArray = arrayOf(
            SkipMatcher,
            simpleMatcher(TokenType.NAMESPACE),
            simpleMatcher(TokenType.METADATA),
            simpleMatcher(TokenType.FRAGMENT),
            simpleMatcher(TokenType.MAPPER),
            simpleMatcher(TokenType.IF),
            simpleMatcher(TokenType.WHEN),
            ParamVariable,

            MetadataKeyMatcher,
            NameDotMatcher,
            StringMatcher,
            NumericLiteral,

            simpleMatcher(TokenType.TRUE),
            simpleMatcher(TokenType.FALSE),
            simpleMatcher(TokenType.L_BRACE),
            simpleMatcher(TokenType.R_BRACE),
            simpleMatcher(TokenType.L_PAREN),
            simpleMatcher(TokenType.R_PAREN),
            simpleMatcher(TokenType.COMMA),
            simpleMatcher(TokenType.DOT),
            simpleMatcher(TokenType.COLON),
            simpleMatcher(TokenType.QUESTION_DOT),
            simpleMatcher(TokenType.ARROW),
            simpleMatcher(TokenType.GT),
            simpleMatcher(TokenType.LT),
            simpleMatcher(TokenType.COLON_COLON),
            simpleMatcher(TokenType.EQUAL),
            simpleMatcher(TokenType.NOT_EQUAL),
            simpleMatcher(TokenType.LE),
            simpleMatcher(TokenType.GE),
            simpleMatcher(TokenType.AND),
            simpleMatcher(TokenType.OR),

            IdMatcher,
        )
    }

    private val lineNumberCharArray: LineNumberCharArray

    init {
        lineNumberCharArray = LineNumberCharArray(string.stripWindowsNewlines())
    }

    private fun String.stripWindowsNewlines() = this.replace("\r", "").toCharArray()

    tailrec fun getNextToken(): Token? {
        var token: Token?
        for (matcher in matcherArray) {
            token = matcher.match(lineNumberCharArray)
            return if (token == null) {
                continue
            } else if (token.tokenType == TokenType.SKIP) {
                getNextToken()
            } else {
                token
            }
        }
        return null
    }

}





