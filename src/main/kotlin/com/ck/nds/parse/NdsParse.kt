package com.ck.nds.parse

import com.ck.nds.SyntaxException
import com.ck.nds.ast.*
import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.lexer.NdsLexer
import com.ck.nds.token.*
import com.ck.nds.token.NdsFixedPrefixType.*
import com.ck.nds.token.NdsKeywordType.*
import com.ck.nds.token.NdsLiteralType.*
import com.ck.nds.token.NdsNormalLiteralType.GENERIC
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
        if (lookahead !is T) throw SyntaxException("Unexpected end of input expected: ${T::class.simpleName} - ${derivedType ?: ""} actual type: $lookahead")

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
    private inline fun <reified T : NdsToken> checkTokenType(derivedType: NdsDerivedType? = null, lineNumber: Int? = null): Boolean {
        val lookahead = this._lookahead ?: return false
        if (lookahead !is T) return false
        if (lineNumber != null && lookahead.lineNumber != lineNumber) return false

        if (derivedType == null) {
            return true
        }
        if (lookahead.tokenType == derivedType) {
            return true
        }

        return false
    }

    private fun expectedMatchNormalLiteral() = GENERIC.expectedMatch()
    private fun NdsNormalLiteralType.expectedMatch() = expectedMatch<NdsNormalLiteralToken>(this)
    private fun NdsNormalLiteralType.checkTokenType(tokenLine: Int? = null) = checkTokenType<NdsNormalLiteralToken>(this, tokenLine)

    private fun NdsLiteralType.expectedMatch() = expectedMatch<NdsLiteralToken>(this)
//    private fun NdsLiteralType.checkTokenType() = checkTokenType<NdsLiteralToken>(this)

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
        val statementList = mutableListOf<NdsAst>()

        // namespace
        statementList.add(this.namespaceStatement())

        // metadata/fragment 可以为空 
        this.metadataStatement()?.let { statementList.add(it) }
        this.fragmentStatement()?.let { statementList.add(it) }

        // 至少有一个 #mapper
        statementList.add(this.mapperStatement())
        while (this._lookahead != null) {
            statementList.add(this.mapperStatement())
        }

        return statementList
    }

    private fun namespaceStatement(): NamespaceAst {
        val namespaceToken = NAMESPACE.expectedMatch()
        if (!(namespaceToken.lineNumber == 1 && namespaceToken.lineIndex == 0)) {
            throw SyntaxException("#namespace must be on the first line of the file!")
        }
        val normalLiteral = this.expectedMatchNormalLiteral()

        return NamespaceAst(namespace = normalLiteral.string)
    }

    private fun metadataStatement(): NdsAst? {
        val lookahead = this._lookahead ?: throw SyntaxException("文件信息不完整")

        if (lookahead !is NdsKeywordToken) return null
        if (lookahead.tokenType != METADATA) return null

        METADATA.expectedMatch()
        L_BRACE.expectedMatch()

        val metadataAstList = mutableListOf<MetadataAst>()

        // 获取metadata信息
        while (DOLLAR_PREFIX.checkTokenType()) {
            metadataAstList.add(this.metadataInfo())
        }

        R_BRACE.expectedMatch()
        return MetadataStatementAst(metadataInfo = metadataAstList)
    }

    private fun metadataInfo(): MetadataAst {
        val key = DOLLAR_PREFIX.expectedMatch()
        COLON.expectedMatch()
        val value = STRING_LITERAL.expectedMatch()
        return MetadataAst(
            metadataKey = key.string,
            metadataVal = value.string
        )
    }

    private fun fragmentStatement(): NdsAst? {
        if (!FRAGMENT.checkTokenType()) {
            return null
        }

        FRAGMENT.expectedMatch()
        val fragmentName = this.expectedMatchNormalLiteral()
        L_BRACE.expectedMatch()
        val mapperStatementBlock = this.mapperStatementBlock()

        R_BRACE.expectedMatch()

        return FragmentAst(
            fragmentName = fragmentName.string,
            fragmentList = mapperStatementBlock
        )
    }

    private fun fragmentRefStatement(): FragmentRefAst {
        FRAGMENT_REF.expectedMatch()
        val fragmentRefName = this.expectedMatchNormalLiteral()

        return FragmentRefAst(fragmentRefName = fragmentRefName.string)
    }

    private fun mapperStatement(): MappingAst {
        MAPPER.expectedMatch()
        val mappingName = this.expectedMatchNormalLiteral()
        L_BRACE.expectedMatch()

        val blockAstList = this.mapperStatementBlock(true)
        R_BRACE.expectedMatch()
        return MappingAst(mappingName = mappingName.string, body = blockAstList)
    }

    /**
     * sql
     * #metadata
     * #fragment_ref
     * #if
     * #when
     */
    private fun mapperStatementBlock(isReadMetadata: Boolean = false): List<NdsAst> {
        val blockList = mutableListOf<NdsAst>()

        if (isReadMetadata) {
            this.metadataStatement()?.let { blockList.add(it) }
        }

        while (!R_BRACE.checkTokenType()) {
            val blockAst = when {
                GENERIC.checkTokenType() -> this.sqlStatement()
                COLON_PREFIX.checkTokenType() -> this.paramVariableStatement()
                IF.checkTokenType() -> this.ifStatement()
                WHEN.checkTokenType() -> this.whenStatement()
                checkTokenType<NdsLiteralToken>() -> this.literal()
                FRAGMENT_REF.checkTokenType() -> this.fragmentRefStatement()
                else -> throw SyntaxException("mapper 中语法错误, 无法处理: ${this._lookahead}")
            }

            blockList.add(blockAst)
        }

        return blockList
    }

    private fun sqlStatement(): NdsAst {
        val token = GENERIC.expectedMatch()
        val tokenList = mutableListOf<NdsToken>(token)

        while (GENERIC.checkTokenType(token.lineNumber)) {
            val expectedMatch = GENERIC.expectedMatch()
            tokenList.add(expectedMatch)
        }

        val sql = tokenList.joinToString(" ") { it.string }
        val sqlAst = SqlAst(sql = sql)

        // 如果当前行存在 判断赋值时
        if (COLON_COLON_PREFIX.checkTokenType()) {
            val func = this.colonColonCallFunctionStatement()
            return IfStatementAst(
                test = func,
                block = listOf(sqlAst, ParamVariableAst(paramExpr = func.paramExpr))
            )
        }
        return sqlAst
    }

    private fun ifStatement(): NdsAst {
        IF.expectedMatch()
        val test = this.expression()

        L_BRACE.expectedMatch()
        val block = this.mapperStatementBlock(false)
        R_BRACE.expectedMatch()

        return IfStatementAst(
            test = test,
            block = block
        )
    }

    private fun whenStatement(): NdsAst {
        WHEN.expectedMatch()
        L_BRACE.expectedMatch()

        val ifStatementAstList = mutableListOf<IfStatementAst>()

        while (!(ELSE.checkTokenType() || R_BRACE.checkTokenType())) {
            ifStatementAstList.add(this.whenBlockStatement())
        }

        val elseAst = this.elseBlockStatement()
        R_BRACE.expectedMatch()

        return WhenStatementAst(
            whenBranchList = ifStatementAstList,
            elseBlock = elseAst
        )

    }

    private fun elseBlockStatement(): List<NdsAst>? {
        if (!ELSE.checkTokenType()) {
            return null
        }

        ELSE.expectedMatch()
        ARROW.expectedMatch()
        L_BRACE.expectedMatch()
        val block = this.mapperStatementBlock(false)
        R_BRACE.expectedMatch()
        return block
    }

    private fun whenBlockStatement(): IfStatementAst {
        val expression = expression()
        ARROW.expectedMatch()
        L_BRACE.expectedMatch()
        val blockList = this.mapperStatementBlock(false)
        R_BRACE.expectedMatch()

        return IfStatementAst(
            test = expression,
            block = blockList
        )

    }

    private fun expression(): NdsAst {
        return this.logicalOrExpression()
    }

    /**
     * x || y
     */
    private fun logicalOrExpression() = commonExpr({ OR.checkTokenType() }) { this.logicalAndExpression() }

    /**
     * x && y
     */
    private fun logicalAndExpression() = commonExpr({ AND.checkTokenType() }) { this.equalityExpression() }

    /**
     * ==, !=
     */
    private fun equalityExpression() = commonExpr({ EQUAL.checkTokenType() || NOT_EQUAL.checkTokenType() }) { this.relationalExpression() }

    /**
     * >, >=, <, <=
     */
    private fun relationalExpression() = commonExpr({
        GT.checkTokenType()
                || LT.checkTokenType()
                || LE.checkTokenType()
                || GE.checkTokenType()
    }) { this.primaryExpression() }


    private fun commonExpr(b: () -> Boolean, call: () -> NdsAst): NdsAst {
        var left = call()
        while (b()) {
            val operator = this.expectedMatch<NdsSymbolToken>()
            val right = call()

            left = ExpressionAst(
                operator = operator.string,
                left = left,
                right = right
            )
        }
        return left
    }

    private fun primaryExpression() = when {
        checkTokenType<NdsLiteralToken>() -> this.literal()
        COLON_PREFIX.checkTokenType() -> this.paramVariableStatement()
        AND_PREFIX.checkTokenType() -> this.andCallFunctionStatement()
        else -> throw SyntaxException("非条件表达式")
    }

    private fun literal(): NdsAst {
        val literalToken = expectedMatch<NdsLiteralToken>()
        val string = literalToken.string
        return when (literalToken.tokenType) {
            STRING_LITERAL -> StringAst(value = string)
            NUMERIC_LITERAL -> NumberAst(value = string.toInt())
            BOOLEAN_LITERAL -> BooleanAst(value = string.toBooleanStrict())
            NULL_LITERAL -> NullAst
        }
    }

    /**
     * :userInfo.userName
     */
    private fun paramVariableStatement(): ParamVariableAst {
        val fixedPrefixToken = COLON_PREFIX.expectedMatch()
        return ParamVariableAst(paramExpr = fixedPrefixToken.string.substring(1))
    }


    /**
     * ::userName?.isNotNull(x, x, ...)
     */
    private fun colonColonCallFunctionStatement(): CallFunctionAst {
        val variableExpr = COLON_COLON_PREFIX.expectedMatch()

        QUESTION_DOT.expectedMatch()
        val funName = this.expectedMatchNormalLiteral()
        val paramList = this.functionParamStatement()

        return CallFunctionAst(
            paramExpr = variableExpr.string.substring(2),
            funcName = funName.string,
            paramList = paramList
        )
    }

    /**
     * &userName?.isNotNull(x, x, ...)
     */
    private fun andCallFunctionStatement(): NdsAst {
        val variableExpr = AND_PREFIX.expectedMatch()
        QUESTION_DOT.expectedMatch()

        val funName = this.expectedMatchNormalLiteral()
        val paramList = this.functionParamStatement()
        return CallFunctionAst(
            paramExpr = variableExpr.string.substring(1),
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


}

