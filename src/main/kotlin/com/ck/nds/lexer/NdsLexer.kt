package com.ck.nds.lexer

import com.ck.nds.lexer.matcher.NdsSkipCharMatcher
import com.ck.nds.lexer.matcher.NormalLiteralMatcher
import com.ck.nds.lexer.matcher.ParamVariableMatcher
import com.ck.nds.lexer.matcher.StringMatcher
import com.ck.nds.token.*

/**
 * 词法解析
 *
 * @author 陈坤
 * 2022/5/31 11:16
 */
class NdsLexer(private val lineNumberCharArray: LineNumberCharArray) {

    /**
     * 根据匹配器获取一个token, 没有对应的匹配项将返回 null
     */
    fun getNextToken(): NdsToken? {
        for (matcher in matcherArray) {
            val token = matcher.match(lineNumberCharArray)
            if (token != null) return token
        }
        return null
    }

    companion object {

        private val matcherArray: Array<NdsMatcher>

        init {

            /**
             * 关键字token匹配器
             */
            val keywordMatchers = NdsKeywordType.values().map {
                SimpleLiteralMatcher(it.keyword) { str, arr -> NdsKeywordToken(str, it, arr) }
            }

            /**
             * 字符token匹配器
             */
            val symbolMatchers = NdsSymbolType.values().map {
                SimpleLiteralMatcher(it.symbolText) { str, arr -> NdsSymbolToken(str, it, arr) }
            }

            /**
             * 其他token匹配器
             */
            val otherMatchers = listOf(
                StringMatcher,
                NormalLiteralMatcher
            )

            matcherArray = listOf(
                /**
                 * 忽略字符匹配器应第一个处理
                 */
                listOf(
                    NdsSkipCharMatcher,
                    ParamVariableMatcher,
                ),
                keywordMatchers,
                symbolMatchers,
                otherMatchers
            ).flatten().toTypedArray()
        }
    }


}