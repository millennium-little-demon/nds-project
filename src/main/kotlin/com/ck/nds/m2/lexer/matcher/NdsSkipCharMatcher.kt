package com.ck.nds.m2.lexer.matcher

import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.m2.NdsToken
import com.ck.nds.m2.lexer.NdsMatcher

/**
 * 需要跳过的字符匹配器
 *
 * @author 陈坤
 * 2022/5/31
 */
object NdsSkipCharMatcher : NdsMatcher {

    private val skipCharArr = charArrayOf(
        ' ', '\n', '\t'
    )

    override fun match(lineNumberCharArray: LineNumberCharArray): NdsToken? {
        skipChar(lineNumberCharArray)
        skipComments(lineNumberCharArray)

        return null
    }

    /**
     * 跳过简单字符 [skipCharArr]
     */
    private fun skipChar(lineNumberCharArray: LineNumberCharArray) {
        while (lineNumberCharArray.peek(0) in skipCharArr)
            lineNumberCharArray.read(1)
    }

    /**
     * 跳过注释信息
     */
    private fun skipComments(lineNumberCharArray: LineNumberCharArray) {
        var temp = 2
        while (lineNumberCharArray.peek(0) == '/' && lineNumberCharArray.peek(1) == '/') {
            while (!lineNumberCharArray.match(temp, LineNumberCharArray.EMPTY) && !lineNumberCharArray.match(temp, '\n')) temp += 1
            lineNumberCharArray.read(temp + 1)

            /**
             * 注释信息之后可能还会存在 [skipCharArr] 字符
             */
            match(lineNumberCharArray)
        }
    }


}