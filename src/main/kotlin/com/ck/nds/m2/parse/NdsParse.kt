package com.ck.nds.m2.parse

import com.ck.nds.SyntaxException
import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.m2.*
import com.ck.nds.m2.lexer.NdsLexer

/**
 * 语法解析
 *
 * @author 陈坤
 * 2022/5/31 11:17
 */
class NdsParse(string: String) {

    private val lexer: NdsLexer
    private var _lookahead: NdsToken?

    init {
        val lineNumberCharArray = LineNumberCharArray(string.stripWindowsNewlines())
        this.lexer = NdsLexer(lineNumberCharArray)
        this._lookahead = lexer.getNextToken()
    }

    private fun String.stripWindowsNewlines() = this.replace("\r", "").toCharArray()

    /**
     * 匹配一个token
     * 如果匹配成功则消费当前token
     */
    private inline fun <reified T : NdsToken> expectedMatch(derivedType: NdsDerivedType? = null): NdsToken {
        /**
         * 下一个元素为空
         */
        val lookahead = this._lookahead ?: throw throw SyntaxException("Unexpected end of input expected: ${T::class.simpleName}")

        /**
         * 期待类型不匹配
         */
        if (lookahead !is T) throw throw SyntaxException("Unexpected end of input expected: ${T::class.simpleName}")

        /**
         * token 子类对应的扩展类型
         */
        val flag = derivedType != null && when (lookahead) {
            is NdsKeywordToken -> lookahead.keywordType == derivedType
            is NdsSymbolToken -> lookahead.symbolType == derivedType
            is NdsNormalLiteralToken -> false
        }

        if (!flag) throw SyntaxException("Unexpected end of input expected: ${T::class.simpleName} $derivedType")

        /**
         * 获取下一个
         */
        this._lookahead = this.lexer.getNextToken()
        return lookahead
    }


    fun expectedMatchNormalLiteral() = expectedMatch<NdsNormalLiteralToken>()
    fun NdsKeywordType.expectedMatch() = expectedMatch<NdsKeywordToken>(this)
    fun NdsSymbolType.expectedMatch() = expectedMatch<NdsSymbolToken>(this)


}


fun main() {

    val text = NdsParse::class.java.classLoader.getResource("t2.txt")?.readText() ?: throw RuntimeException("文件不存在")

    NdsParse(text).run {
        println(NdsKeywordType.NAMESPACE.expectedMatch())
        println(NdsKeywordType.MAPPER.expectedMatch())
        println(NdsSymbolType.ARROW.expectedMatch())
        println(NdsSymbolType.ARROW.expectedMatch())
        println(NdsKeywordType.WHEN.expectedMatch())
    }

}
