package com.ck.nds.token

import com.ck.nds.lexer.LineNumberCharArray

/**
 *
 * @author 陈坤
 * 2022/5/31
 */
sealed interface NdsToken {
    /**
     * token 文本
     */
    val string: String

    /**
     * token 所在的行号
     */
    val lineNumber: Int
    val lineIndex: Int
    val ndsTokenType: NdsTokenType
}

sealed class NdsAbstractToken(
    lineNumberCharArray: LineNumberCharArray,
) : NdsToken {

    /**
     * 创建对象时获取字符位置
     * 不是在使用 [lineIndex] 时调用 [LineNumberCharArray.linePosition]
     * 否则可能数据不是正确的
     */
    private val _lineIndex = lineNumberCharArray.linePosition

    override val lineNumber = lineNumberCharArray.lineNumber
    override val lineIndex
        get() = _lineIndex - string.length

    protected fun lineToString() = "lineNumber='$lineNumber', lineIndex='$lineIndex'"
}

/**
 * 普通文本类型Token
 *
 * @author 陈坤
 * 2022/5/31
 */
data class NdsNormalLiteralToken(
    override val string: String,
    private val lineNumberCharArray: LineNumberCharArray,
) : NdsAbstractToken(lineNumberCharArray) {

    override val ndsTokenType: NdsTokenType = NdsTokenType.NORMAL_LITERAL
    override fun toString() = "NdsNormalLiteralToken(string='$string', ${super.lineToString()}, ndsTokenType=$ndsTokenType)"
}

data class NdsStringToken(
    override val string: String,
    private val lineNumberCharArray: LineNumberCharArray,
) : NdsAbstractToken(lineNumberCharArray) {

    override val ndsTokenType = NdsTokenType.STRING_LITERAL
    override fun toString(): String {
        return "StringToken(string='$string', ndsTokenType=$ndsTokenType)"
    }

}

data class NdsParamVariableToken(
    override val string: String,
    private val lineNumberCharArray: LineNumberCharArray,
) : NdsAbstractToken(lineNumberCharArray) {
    override val ndsTokenType = NdsTokenType.PARAM_VARIABLE

}

/**
 * 关键字类型的Token
 *
 * @author 陈坤
 * 2022/5/31
 */
data class NdsKeywordToken(
    override val string: String,
    val keywordType: NdsKeywordType,
    private val lineNumberCharArray: LineNumberCharArray,
) : NdsAbstractToken(lineNumberCharArray) {

    override val ndsTokenType: NdsTokenType = NdsTokenType.KEYWORD
    override fun toString() = "NdsKeywordToken(string='$string', ${super.lineToString()}, keywordType=$keywordType, ndsTokenType=$ndsTokenType)"
}

/**
 * 符号类型token
 *
 * @author 陈坤
 * 2022/5/31
 */
data class NdsSymbolToken(
    override val string: String,
    val symbolType: NdsSymbolType,
    private val lineNumberCharArray: LineNumberCharArray,
) : NdsAbstractToken(lineNumberCharArray) {

    override val ndsTokenType: NdsTokenType = NdsTokenType.SYMBOL
    override fun toString() = "NdsSymbolToken(string='$string',  ${super.lineToString()}, symbolType=$symbolType, ndsTokenType=$ndsTokenType)"
}