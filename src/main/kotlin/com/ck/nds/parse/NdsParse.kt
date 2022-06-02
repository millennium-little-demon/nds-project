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
     * 否则抛出异常
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
            throw SyntaxException("Unexpected end of input expected: ${T::class.simpleName} - $derivedType actual type: $lookahead")
        }

        /**
         * 获取下一个
         */
        this._lookahead = this.lexer.getNextToken()
        return lookahead
    }

    /**
     * 对下一个token进行匹配, 成功返回true
     */
    private inline fun <reified T : NdsToken> checkTokenType(derivedType: NdsDerivedType? = null): Boolean {
        val lookahead = this._lookahead ?: return false
        if (lookahead !is T) return false

        if (derivedType == null) {
            return true
        }
        if (lookahead.tokenType == derivedType) {
            return true
        }

        return false
    }

    private fun expectedMatchNormalLiteral() = NdsNormalLiteralType.GENERIC.expectedMatch()
    private fun NdsNormalLiteralType.expectedMatch() = expectedMatch<NdsNormalLiteralToken>(this)
    private fun NdsNormalLiteralType.checkTokenType() = checkTokenType<NdsNormalLiteralToken>(this)

    private fun NdsLiteralType.expectedMatch() = expectedMatch<NdsLiteralToken>(this)
    private fun NdsLiteralType.checkTokenType() = checkTokenType<NdsLiteralToken>(this)

    private fun NdsFixedPrefixType.expectedMatch() = expectedMatch<NdsFixedPrefixToken>(this)
    private fun NdsFixedPrefixType.checkTokenType() = checkTokenType<NdsFixedPrefixToken>(this)


    private fun NdsKeywordType.expectedMatch() = expectedMatch<NdsKeywordToken>(this)
    private fun NdsKeywordType.checkTokenType() = checkTokenType<NdsKeywordToken>(this)

    private fun NdsSymbolType.expectedMatch() = expectedMatch<NdsSymbolToken>(this)
    private fun NdsSymbolType.checkTokenType() = checkTokenType<NdsSymbolToken>(this)


    fun programAst(): NdsAst {
        return ProgramAst(
            body = this.statementList()
        )
    }

    private fun statementList(): List<NdsAst> {
        val namespaceToken = NAMESPACE.expectedMatch()
        if (!(namespaceToken.lineNumber == 1 && namespaceToken.lineIndex == 0)) {
            throw SyntaxException("#namespace must be on the first line of the file!")
        }
        val resultList = mutableListOf<NdsAst>()


        val normalLiteral = this.expectedMatchNormalLiteral()
        resultList.add(NamespaceAst(namespace = normalLiteral.string))
        this.metadataStatement()?.let { resultList.add(it) }
        this.fragmentStatement()?.let { resultList.add(it) }

        resultList.add(andCallFunctionStatement())
//        resultList.add(this.mapperStatement())
        return resultList
    }

    /**
     * :userName?.isNotNull(x, x, ...)
     */
    private fun colonCallFunctionStatement(): NdsAst {
        val variableExpr = NdsFixedPrefixType.COLON_PREFIX.expectedMatch()
        QUESTION_DOT.expectedMatch()
        val funName = this.expectedMatchNormalLiteral()
        val paramList = this.functionParamStatement()

        return ColonCallFunctionAst(
            paramVariable = variableExpr.string,
            funcName = funName.string,
            paramList = paramList
        )
    }

    /**
     * &userName?.isNotNull(x, x, ...)
     */
    private fun andCallFunctionStatement(): NdsAst {
        val variableExpr = NdsFixedPrefixType.AND_PREFIX.expectedMatch()
        QUESTION_DOT.expectedMatch()

        val funName = this.expectedMatchNormalLiteral()
        val paramList = this.functionParamStatement()
        return AndCallFunctionAst(
            paramVariable = variableExpr.string,
            funcName = funName.string,
            paramList = paramList
        )
    }

    /**
     * 函数参数信息
     */
    private fun functionParamStatement(): List<NdsAst> {
        if (!L_PAREN.checkTokenType()) return emptyList()

        val paramList = mutableListOf<NdsAst>()
        L_PAREN.expectedMatch()

        while (true) {
            if (!checkTokenType<NdsLiteralToken>()) {
                break
            }
            val literalToken = this.literal()
            paramList.add(literalToken)
            if (COMMA.checkTokenType()) COMMA.expectedMatch() else break
        }


        R_PAREN.expectedMatch()

        return paramList
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
        if (lookahead !is NdsFixedPrefixToken) return null
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
                is NdsFixedPrefixToken -> {
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

    private fun relationalExpression() {

    }


    private fun paramVariableStatement() {

    }

    private fun literal(): NdsAst {
        if (!checkTokenType<NdsLiteralToken>()) {
            throw SyntaxException("Literal: unexpected literal production")
        }

        val literalToken = expectedMatch<NdsLiteralToken>()
        val string = literalToken.string
        return when (literalToken.tokenType) {
            NdsLiteralType.STRING_LITERAL -> StringAst(value = string)
            NdsLiteralType.NUMERIC_LITERAL -> NumberAst(value = string.toInt())
            NdsLiteralType.BOOLEAN_LITERAL -> BooleanAst(value = string.toBooleanStrict())
            NdsLiteralType.NULL_LITERAL -> NullAst
        }
    }


    private fun ifStatement(): NdsAst? {

        return null
    }

}

