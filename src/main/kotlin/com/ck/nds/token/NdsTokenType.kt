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

    //    ELSE("else"),
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
    TRUE("true"),
    FALSE("false"),
    L_BRACE("{"),
    R_BRACE("}"),
    L_PAREN("("),
    R_PAREN(")"),
    COMMA(","),
    DOT("."),
    COLON(":"),
    QUESTION_DOT("?."),
    ARROW("->"),
    GT(">"),
    LT("<"),
    COLON_COLON("::"),
    EQUAL("=="),
    NOT_EQUAL("!="),
    LE("<="),
    GE(">="),
    AND("&&"),
    OR("||"),
    BANG("!"),
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

}

/**
 * 固定前缀类型
 */
enum class NdsFixedPrefixType(val prefixText: String) : NdsDerivedType {

    COLON_PREFIX(":"),

    DOLLAR_PREFIX("$")

}