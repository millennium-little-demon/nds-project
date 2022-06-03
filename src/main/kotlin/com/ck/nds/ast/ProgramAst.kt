package com.ck.nds.ast

/**
 *
 * @author 陈坤
 * 2022/6/1
 */
data class ProgramAst(
    val type: String = "ProgramAst",
    val body: List<NdsAst>?,
) : NdsAst

/**
 * #namespace com.ck.mapper.UserInfoMapper
 */
data class NamespaceAst(
    val type: String = "NamespaceAst",
    val namespace: String,
) : NdsAst

/**
 * #metadata {
 * [MetadataAst]
 * }
 */
data class MetadataStatementAst(
    val type: String = "MetadataStatementAst",
    val metadataInfo: List<MetadataAst>,
) : NdsAst

data class MetadataAst(
    val type: String = "MetadataAst",
    val metadataKey: String,
    val metadataVal: String,
) : NdsAst

data class FragmentAst(
    val type: String = "FragmentAst",
    val fragmentName: String,
    val fragmentList: List<NdsAst>,
) : NdsAst

/**
 * #mapper xxx {
 * }
 */
data class MappingAst(
    val type: String = "MappingAst",
    val mappingName: String,
    val body: List<NdsAst>,
) : NdsAst

/**
 * #fragmentRef xxx
 */
data class FragmentRefAst(
    val type: String = "FragmentRefAst",
    val fragmentRefName: String,
) : NdsAst

/**
 * userName?.test(31, "1", true, null)
 */
data class CallFunctionAst(
    val type: String = "CallFunctionAst",
    val paramExpr: String,
    val funcName: String,
    val paramList: List<NdsAst>,
) : NdsAst


data class BooleanAst(
    val type: String = "BooleanAst",
    val value: Boolean,
) : NdsAst

data class NumberAst(
    val type: String = "NumberAst",
    val value: Number,
) : NdsAst

data class StringAst(
    val type: String = "StringAst",
    val value: String,
) : NdsAst

@Suppress("unused")
object NullAst : NdsAst {
    fun getType() = "NullAst"
}

data class SqlAst(
    val type: String = "SqlAst",
    val sql: String,
) : NdsAst

/**
 * :userInfo.userName
 */
data class ParamVariableAst(
    val type: String = "ParamVariableAst",
    val paramExpr: String,
) : NdsAst

/**
 * #if [ExpressionAst] {
 * }
 */
data class IfStatementAst(
    val type: String = "IfStatementAst",
    val test: NdsAst,
    val block: List<NdsAst>,
) : NdsAst

/**
 * #when {
 * [IfStatementAst]
 * [IfStatementAst]
 * [IfStatementAst]
 * ...
 * else -> {}
 * }
 */
data class WhenStatementAst(
    val type: String = "WhenStatementAst",
    val whenBranchList: List<IfStatementAst>,
    val elseBlock: List<NdsAst>?,
) : NdsAst

/**
 * 表达式
 */
data class ExpressionAst(
    val type: String = "Expression",
    val operator: String,
    val left: NdsAst,
    val right: NdsAst,
) : NdsAst

