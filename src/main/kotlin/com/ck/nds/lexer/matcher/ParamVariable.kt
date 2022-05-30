package com.ck.nds.lexer.matcher

import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.lexer.Matcher
import com.ck.nds.token.Token
import com.ck.nds.token.TokenType

/**
 * :userInfo.userName
 *
 * @author 陈坤
 * 2022/5/30 15:15
 */
internal object ParamVariable : Matcher {


    override fun match(charArray: LineNumberCharArray): Token? {
        // 第一个字符必须是 :
        // 第二个字符必须是字符
        if (!(charArray.peek(0) == ':' && charArray.peek(1).isLetter())) return null

        var temp = 2
        var ch = charArray.peek(temp)
        while (ch.isLetterOrDigit() || ch == '.' || ch == '_') {
            temp += 1
            ch = charArray.peek(temp)
        }

        ch = charArray.peek(temp - 1)
        if (!(ch.isLetterOrDigit() || ch == '_')) return null

        val readStr = charArray.read(temp)
        return Token(readStr.substring(1), charArray, TokenType.PARAM_VARIABLE)
    }
}

