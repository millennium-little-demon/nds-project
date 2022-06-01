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
    private inline fun <reified T : NdsToken> expectedMatch(derivedType: NdsDerivedType? = null): NdsToken {
        /**
         * 下一个元素为空
         */
        val lookahead = this._lookahead ?: throw throw SyntaxException("Unexpected end of input expected: ${T::class.simpleName}")

        /**
         * 期待类型不匹配
         */
        if (lookahead !is T) throw throw SyntaxException("Unexpected end of input expected: ${T::class.simpleName} actual type: $lookahead")

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

        val flag = when (lookahead) {
            is NdsKeywordToken -> lookahead.keywordType == derivedType
            is NdsSymbolToken -> lookahead.symbolType == derivedType
            else -> false
        }

        if (!flag) throw SyntaxException("Unexpected end of input expected: ${T::class.simpleName} $derivedType")

        /**
         * 获取下一个
         */
        this._lookahead = this.lexer.getNextToken()
        return lookahead
    }


    private fun expectedMatchNormalLiteral() = expectedMatch<NdsNormalLiteralToken>()
    private fun expectedMatchString() = expectedMatch<NdsStringToken>()
    private fun expectedMatchParamVariable() = expectedMatch<NdsParamVariableToken>()
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


        val normalLiteral = expectedMatchNormalLiteral()
        resultList.add(NamespaceAst(namespace = normalLiteral.string))
        this.metadataStatement()?.let { resultList.add(it) }
        this.fragmentStatement()?.let { resultList.add(it) }

        resultList.add(this.mapperStatement())
        return resultList
    }

    private fun metadataStatement(): NdsAst? {
        val lookahead = this._lookahead ?: throw SyntaxException("文件信息不完整")

        if (lookahead !is NdsKeywordToken) return null
        if (lookahead.keywordType != METADATA) return null

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
        if (this._lookahead == null || this._lookahead !is NdsNormalLiteralToken) return null

        val key = this.expectedMatchNormalLiteral()
        if (!key.string.startsWith("$") || key.string.length < 2) throw SyntaxException("#metadata key 字符必须以[\$]开头并且字符长度大于2!")

        COLON.expectedMatch()
        val value = this.expectedMatchString()
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
                    when (lookahead.keywordType) {
                        FRAGMENT -> TODO()
                        IF -> TODO()
                        WHEN -> TODO()
                        else -> break
                    }

                }
                is NdsParamVariableToken -> {
                    val paramVariable = this.expectedMatchParamVariable()
                    blockAstList.add(ParamVariableAst(varName = paramVariable.string))
                }
                else -> {
                    break
                }
            }

        }

        return mutableListOf<NdsAst>().apply {
            var tempIndex: Int
            for (ndsAst in blockAstList) {
                tempIndex = size - 1
                if (ndsAst is SqlAst && this[tempIndex] is SqlAst) {
                    val sql = (this[tempIndex] as SqlAst).sql + " " + ndsAst.sql
                    this[tempIndex] = SqlAst(sql = sql)
                } else {
                    this.add(ndsAst)
                }
            }
        }
    }


    private fun paramVariableStatement() {

    }


    private fun ifStatement(): NdsAst? {

        return null
    }

}

