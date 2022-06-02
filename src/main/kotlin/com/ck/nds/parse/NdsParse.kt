package com.ck.nds.parse

import com.ck.nds.SyntaxException
import com.ck.nds.ast.*
import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.lexer.NdsLexer
import com.ck.nds.token.*
import com.ck.nds.token.NdsKeywordType.*
import com.ck.nds.token.NdsSymbolType.*

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
    private inline fun <reified T : NdsToken> expectedMatch(derivedType: NdsDerivedType? = null): T {
        /**
         * 下一个元素为空
         */
        val lookahead = this._lookahead ?: throw SyntaxException("Unexpected end of input expected: ${T::class.simpleName}")

        /**
         * 期待类型不匹配
         */
        if (lookahead !is T) throw SyntaxException("Unexpected end of input expected: ${T::class.simpleName} actual type: $lookahead")

        /**
         * token 子类对应的扩展类型为空直接返回
         */
        if (derivedType == null) {
            /**
             * 获取下一个
             */
            this._lookahead = this.lexer.getNextToken()
            return lookahead
        }

        if (lookahead.tokenType != derivedType) {
            throw SyntaxException("Unexpected end of input expected: ${T::class.simpleName} actual type: $lookahead")
        }

        /**
         * 获取下一个
         */
        this._lookahead = this.lexer.getNextToken()
        return lookahead
    }

    private fun expectedMatchNormalLiteral() = NdsNormalLiteralType.GENERIC.expectedMatch()
    private fun NdsNormalLiteralType.expectedMatch() = expectedMatch<NdsNormalLiteralToken>(this)
    private fun NdsLiteralType.expectedMatch() = expectedMatch<NdsLiteralToken>(this)
    private fun NdsFixedPrefixType.expectedMatch() = expectedMatch<NdsFixedPreFixToken>(this)
    private fun NdsKeywordType.expectedMatch() = expectedMatch<NdsKeywordToken>(this)
    private fun NdsSymbolType.expectedMatch() = expectedMatch<NdsSymbolToken>(this)


    fun programAst(): NdsAst {
        return ProgramAst(
            body = this.statement()
        )
    }

    private fun statement(): List<NdsAst> {
        val namespaceToken = NAMESPACE.expectedMatch()
        if (!(namespaceToken.lineNumber == 1 && namespaceToken.lineIndex == 0)) {
            throw SyntaxException("#namespace must be on the first line of the file!")
        }
        val resultList = mutableListOf<NdsAst>()


        val normalLiteral = this.expectedMatchNormalLiteral()
        resultList.add(NamespaceAst(namespace = normalLiteral.string))
        this.metadataStatement()?.let { resultList.add(it) }
        this.fragmentStatement()?.let { resultList.add(it) }

        resultList.add(this.mapperStatement())
        return resultList
    }

    private fun metadataStatement(): NdsAst? {
        val lookahead = this._lookahead ?: throw SyntaxException("文件信息不完整")

        if (lookahead !is NdsKeywordToken) return null
        if (lookahead.tokenType != METADATA) return null

        METADATA.expectedMatch()
        L_BRACE.expectedMatch()

        // 获取metadata信息
        val metadataInfoMap = mutableMapOf<String, String>()
        while (true) {
            val metadataInfo = this.metadataInfo() ?: break
            metadataInfoMap[metadataInfo.first] = metadataInfo.second
        }
        R_BRACE.expectedMatch()
        return MetadataInfoAst(metadataMap = metadataInfoMap)
    }

    private fun metadataInfo(): Pair<String, String>? {
        val lookahead = this._lookahead ?: return null
        if (lookahead !is NdsFixedPreFixToken) return null
        if (lookahead.tokenType != NdsFixedPrefixType.DOLLAR_PREFIX) return null

        val key = NdsFixedPrefixType.DOLLAR_PREFIX.expectedMatch()
        COLON.expectedMatch()
        val value = NdsLiteralType.STRING_LITERAL.expectedMatch()
        return key.string to value.string
    }

    private fun fragmentStatement(): NdsAst? {
        return null
    }

    private fun mapperStatement(): MappingAst {
        MAPPER.expectedMatch()
        val mappingName = this.expectedMatchNormalLiteral()
        L_BRACE.expectedMatch()

        val blockAstList = this.mapperStatementBlock()
        R_BRACE.expectedMatch()
        return MappingAst(mappingName = mappingName.string, body = blockAstList)
    }

    private fun mapperStatementBlock(isLoadMetadata: Boolean = true): List<NdsAst> {
        val blockAstList = mutableListOf<NdsAst>()

        if (isLoadMetadata) this.metadataStatement()?.let { blockAstList.add(it) }

        while (true) {
            val lookahead = this._lookahead ?: break

            when (lookahead) {
                is NdsNormalLiteralToken -> {
                    val normalLiteral = expectedMatchNormalLiteral()
                    blockAstList.add(SqlAst(sql = normalLiteral.string))
                }
                is NdsKeywordToken -> {
                    when (lookahead.tokenType) {
                        FRAGMENT -> TODO()
                        IF -> TODO()
                        WHEN -> TODO()
                        else -> break
                    }
                }
                is NdsFixedPreFixToken -> {
                    val paramVariable = NdsFixedPrefixType.COLON_PREFIX.expectedMatch()
                    blockAstList.add(ParamVariableAst(varName = paramVariable.string))
                }
                else -> {
                    break
                }
            }

        }

        return blockAstList
    }


    private fun paramVariableStatement() {

    }


    private fun ifStatement(): NdsAst? {

        return null
    }

}

