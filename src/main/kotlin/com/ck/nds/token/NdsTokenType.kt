package com.ck.nds.token

/**
 * 词法分析得到的token类型
 *
 * @author 陈坤
 * 2022/5/31
 */
enum class NdsTokenType {

    /**
     * 普通字符
     */
    NORMAL_LITERAL,

    /**
     * 内置关键字相关 [NdsKeywordType]
     */
    KEYWORD,

    /**
     * 内置使用符号 [NdsSymbolType]
     */
    SYMBOL,

    /**
     * 字符字面量 '' 或 ""
     */
    STRING_LITERAL,

    /**
     * 数字字面量
     */
    NUMERIC_LITERAL,

    /**
     * 映射参数, 以冒号开头
     * 对映射方法中的形参进行取值
     *
     * :userInfo.userName
     * :map.keyName
     * :userInfoList[0].userName
     *
     */
    PARAM_VARIABLE,
}

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
