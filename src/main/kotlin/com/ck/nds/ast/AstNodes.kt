package com.ck.nds.ast

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 *
 * @author 陈坤
 * 2022/5/29 17:07
 */
internal class Program(
    val value: List<AstNode>?,
) : AstNode

internal class Namespace(
    val value: String,
) : AstNode

internal class Metadata(
    val value: List<MetadataKeyVal>,
) : AstNode

internal class MetadataKeyVal(
    val key: String,
    val value: String,
) : AstNode {

    @JsonIgnore
    override fun getType(): String? {
        return super.getType()
    }
}

internal class MethodMapper(
    val methodName: String,
) : AstNode