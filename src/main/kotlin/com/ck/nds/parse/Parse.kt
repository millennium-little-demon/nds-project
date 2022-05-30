package com.ck.nds.parse

import com.ck.nds.SyntaxException
import com.ck.nds.ast.*
import com.ck.nds.lexer.Lexer
import com.ck.nds.token.Token
import com.ck.nds.token.TokenType
import com.ck.nds.token.TokenType.*

/**
 *
 * @author 陈坤
 * 2022/5/29 14:52
 */
internal class Parse(string: String) {

    private val lexer = Lexer(string)

    var lookahead = lexer.getNextToken()

    /**
     * 期望一个token类型, 如果匹配成功则返回
     * 失败抛出异常 [SyntaxException]
     */
    private fun expectedMatch(tokenType: TokenType): Token {
        val token = this.lookahead
            ?: throw SyntaxException("Unexpected end of input expected: ${tokenType.name}")

        if (tokenType != token.tokenType)
            throw SyntaxException("Unexpected token $token expected: ${tokenType.name}")

        this.lookahead = this.lexer.getNextToken()
        return token
    }

    fun program(): Program {
        return Program(
            statementList()
        )
    }

    private fun statementList(): List<AstNode> {
        val list = mutableListOf(this.statement())
        while (this.lookahead != null) {
            list.add(this.statement())
        }

        return list
    }

    /**
     * statement
     *  : namespace
     *  | metadata
     *  | fragment
     *  | mapper
     *  ;
     */
    private fun statement(): AstNode {
        val look = this.lookahead ?: throw SyntaxException("Judgment was made before coming in, it will not be empty here!")

        return when (look.tokenType) {
            NAMESPACE -> this.namespace()
            METADATA -> this.metadata()
//            FRAGMENT -> fragment()
            MAPPER -> mapper()
            else -> throw SyntaxException("Literal: unexpected literal production")

        }
    }

    /**
     * #namespace com.ck.mapper.UserInfoMapper
     */
    private fun namespace(): Namespace {
        val namespace = expectedMatch(NAMESPACE)
        if (namespace.lineNum != 1) {
            throw SyntaxException("'#namespace' should be on the first line of the file.")
        }

        val nameDotToken = expectedMatch(NAME_DOT_LITERAL)
        return Namespace(nameDotToken.value)
    }

    /**
     * #metadata {
     *      metadataExpression+
     * }
     */
    private fun metadata(): AstNode {
        expectedMatch(METADATA)
        expectedMatch(L_BRACE)

        val metadataExprList = mutableListOf(this.metadataExpression())

        while (this.lookahead != null) {
            if (this.lookahead!!.tokenType != METADATA_KEY) break
            metadataExprList.add(this.metadataExpression())
        }

        expectedMatch(R_BRACE)
        return Metadata(metadataExprList)
    }

    /**
     * $config: "abc"
     */
    private fun metadataExpression(): MetadataKeyVal {
        val key = expectedMatch(METADATA_KEY)
        expectedMatch(COLON)
        val v = expectedMatch(STRING_LITERAL)

        return MetadataKeyVal(key.value, v.value)
    }

    private fun fragment(): AstNode {
        return Namespace("")
    }

    private fun mapper(): AstNode {
        expectedMatch(MAPPER)
        val idStr = expectedMatch(ID)
        expectedMatch(L_BRACE)

        expectedMatch(R_BRACE)
        return MethodMapper(idStr.value)
    }

    private fun mapperBlockStatement() {
        val look = this.lookahead ?: throw SyntaxException("Judgment was made before coming in, it will not be empty here!")

        when (look.tokenType) {
            IF -> {}
            WHEN -> {}
            else -> throw SyntaxException("mapper statement: unexpected mapper statement production")
        }


    }

    private fun expressionStatement() {

    }

}





