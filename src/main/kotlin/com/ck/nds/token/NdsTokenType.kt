package com.ck.nds.token

/**
 * 词法分析得到的token类型
 *
 * @author 陈坤
 * 2022/5/31
 */
interface NdsDerivedType

/**
 * 关键字
 *
 *  @author 陈坤
 * * 2022/5/31
 */
enum class NdsKeywordType(val keyword: String) : NdsDerivedType {
    NAMESPACE("#namespace"),
    METADATA("#metadata"),
    FRAGMENT("#fragment"),
    MAPPER("#mapper"),
    IF("#if"),
    ELSE("else"),
    WHEN("#when"),
    ;

}

/**
 * 符号
 *
 *  @author 陈坤
 * * 2022/5/31
 */
enum class NdsSymbolType(val symbolText: String) : NdsDerivedType {
    L_BRACE("{"),
    R_BRACE("}"),
    L_PAREN("("),
    R_PAREN(")"),
    COMMA(","),
    COLON(":"),
    QUESTION_DOT("?."),
    ARROW("->"),

    BANG("!"),
    GT(">"),
    LT("<"),
    LE("<="),
    GE(">="),

    EQUAL("=="),
    NOT_EQUAL("!="),

    AND("&&"),
    OR("||"),

}


enum class NdsNormalLiteralType : NdsDerivedType {

    GENERIC,

    PACKAGE

}

/**
 * 常见字面量类型
 */
enum class NdsLiteralType : NdsDerivedType {

    /**
     * '' 或 ""
     */
    STRING_LITERAL,

    /**
     * 数字
     */
    NUMERIC_LITERAL,

    /**
     * boolean
     */
    BOOLEAN_LITERAL,

    /**
     * null
     */
    NULL_LITERAL,

}

/**
 * 固定前缀类型
 */
enum class NdsFixedPrefixType(val prefixText: String, val containChar: CharArray) : NdsDerivedType {


    COLON_PREFIX(":", charArrayOf('.', '_')),

    COLON_COLON_PREFIX("::", charArrayOf('.', '_')),

    DOLLAR_PREFIX("$", charArrayOf('_')),

    AND_PREFIX("&", charArrayOf('.', '_'));

    fun prefixSize() = this.prefixText.length


}