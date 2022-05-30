package com.ck.nds.token

/**
 *
 * @author 陈坤
 * 2022/5/30 9:06
 */
internal enum class TokenType(val keyword: String) {

    NAMESPACE("#namespace"),
    METADATA("#metadata"),
    FRAGMENT("#fragment"),
    MAPPER("#mapper"),
    IF("#if"),
    WHEN("#when"),
    ID("id")
    ,
    
    NAME_DOT_LITERAL("name_dot_literal"),
    STRING_LITERAL("string_literal"),
    NUMERIC_LITERAL("numeric_literal"),
    PARAM_VARIABLE("param_variable"),

    METADATA_KEY("metadataKey"),
    
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


    ;
}