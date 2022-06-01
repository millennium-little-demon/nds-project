package com.ck.nds.lexer.matcher

import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.lexer.NdsMatcher
import com.ck.nds.token.NdsToken

/**
 * 需要跳过的字符匹配器
 *
 * @author 陈坤
 * 2022/5/31
 */
object NdsSkipCharMatcher : NdsMatcher {

    override fun match(lineNumberCharArray: LineNumberCharArray): NdsToken? {
        skipWhitespace(lineNumberCharArray)
        skipComments(lineNumberCharArray)

        return null
    }

    /**
     * 跳过空白字符
     * ' ', '\n', '\t'
     */
    private fun skipWhitespace(lineNumberCharArray: LineNumberCharArray) {
        while (lineNumberCharArray.peek(0).isWhitespace())
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
             * 注释信息之后可能还会存在空白字符
             */
            match(lineNumberCharArray)
        }
    }


}