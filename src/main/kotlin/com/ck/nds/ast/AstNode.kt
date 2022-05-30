package com.ck.nds.ast

/**
 *
 * @author 陈坤
 * 2022/5/29 17:07
 */
internal interface AstNode {

    fun getType(): String? = this::class.simpleName

}
