package com.ck.nds.m2.lexer

import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.m2.NdsToken

/**
 *
 * @author 陈坤
 * 2022/5/31
 */
fun interface NdsMatcher {

    fun match(lineNumberCharArray: LineNumberCharArray): NdsToken?

}