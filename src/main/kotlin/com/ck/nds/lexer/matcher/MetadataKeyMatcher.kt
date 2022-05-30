package com.ck.nds.lexer.matcher

import com.ck.nds.lexer.LineNumberCharArray
import com.ck.nds.lexer.Matcher
import com.ck.nds.token.Token
import com.ck.nds.token.TokenType

/**
 *
 * @author 陈坤
 * 2022/5/30 11:01
 */
internal object MetadataKeyMatcher: Matcher {
    
    override fun match(charArray: LineNumberCharArray): Token? {
        if (charArray.peek(0) != '$') return null
        
        var temp = 1
        while (charArray.peek(temp).isLetter()) {
            temp += 1
        }

        val readStr = charArray.read(temp).substring(1)
        
        return Token(readStr, charArray, TokenType.METADATA_KEY)
    }
}