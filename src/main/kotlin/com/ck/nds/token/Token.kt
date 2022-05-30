package com.ck.nds.token

import com.ck.nds.lexer.LineNumberCharArray
import com.fasterxml.jackson.annotation.JsonIgnore

/**
 *
 * @author 陈坤
 * 2022/5/29 15:04
 */
internal data class Token(
    val value: String,
    val position: Int,
    val lineNum: Int,
    @JsonIgnore
    val tokenType: TokenType,
) {

    constructor(value: String, lnc: LineNumberCharArray, tokenType: TokenType)
            : this(value, lnc.lastCursor, lnc.lineNumber, tokenType)


}
