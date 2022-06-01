package com.ck.nds.lexer

import com.ck.nds.token.NdsToken

/**
 *
 * @author 陈坤
 * 2022/5/31
 */
fun interface NdsMatcher {

    fun match(lineNumberCharArray: LineNumberCharArray): NdsToken?

}