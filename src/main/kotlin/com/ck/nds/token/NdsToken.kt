package com.ck.nds.token

import com.ck.nds.lexer.LineNumberCharArray

/**
 * 词法解析 token 接口
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

    /**
     * token所在行的开始位置
     */
    val lineIndex: Int

    /**
     * token 类型, 具体类型 [NdsDerivedType] 子枚举类
     */
    val tokenType: NdsDerivedType
}

sealed class NdsAbstractToken(lineNumberCharArray: LineNumberCharArray) : NdsToken {

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
    override val tokenType: NdsNormalLiteralType,
    private val lineNumberCharArray: LineNumberCharArray,
) : NdsAbstractToken(lineNumberCharArray) {

    override fun toString(): String {
        return "NdsNormalLiteralToken(string='$string', ${super.lineToString()}, tokenType=$tokenType)"
    }
}

/**
 *
 */
data class NdsLiteralToken(
    override val string: String,
    override val tokenType: NdsLiteralType,
    private val lineNumberCharArray: LineNumberCharArray,
) : NdsAbstractToken(lineNumberCharArray) {

    override fun toString() = "StringToken(string='$string', ${super.lineToString()}, ndsTokenType=$tokenType)"
}

/**
 * 关键字类型的Token
 *
 * @author 陈坤
 * 2022/5/31
 */
data class NdsKeywordToken(
    override val string: String,
    override val tokenType: NdsKeywordType,
    private val lineNumberCharArray: LineNumberCharArray,
) : NdsAbstractToken(lineNumberCharArray) {

    override fun toString() = "NdsKeywordToken(string='$string', ${super.lineToString()}, keywordType=$tokenType)"
}

/**
 * 符号类型token
 *
 * @author 陈坤
 * 2022/5/31
 */
data class NdsSymbolToken(
    override val string: String,
    override val tokenType: NdsSymbolType,
    private val lineNumberCharArray: LineNumberCharArray,
) : NdsAbstractToken(lineNumberCharArray) {

    override fun toString() = "NdsSymbolToken(string='$string', ${super.lineToString()}, symbolType=$tokenType)"
}

/**
 * 固定前缀类型
 *
 * @author 陈坤
 * 2022/6/2
 */
data class NdsFixedPreFixToken(
    override val string: String,
    override val tokenType: NdsFixedPrefixType,
    private val lineNumberCharArray: LineNumberCharArray,
) : NdsAbstractToken(lineNumberCharArray) {

    override fun toString() = "NdsFixedPreFixToken(string='$string', ${super.lineToString()}, ndsTokenType=$tokenType)"
}