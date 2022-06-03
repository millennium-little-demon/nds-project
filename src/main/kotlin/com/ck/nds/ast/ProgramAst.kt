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

data class NamespaceAst(
    val type: String = "NamespaceAst",
    val namespace: String,
) : NdsAst

data class MetadataStatementAst(
    val type: String = "MetadataStatementAst",
    val metadataInfo: List<MetadataAst>,
) : NdsAst

data class MetadataAst(
    val type: String = "MetadataInfoAst",
    val metadataKey: String,
    val metadataVal: String,
) : NdsAst

data class FragmentAst(
    val type: String = "FragmentAst",
    val fragmentName: String,
    val fragmentList: List<NdsAst>,
) : NdsAst

data class FragmentRefAst(
    val type: String = "FragmentRefAst",
    val fragmentRefName: String,
) : NdsAst

data class ColonCallFunctionAst(
    val type: String = "ColonCallFunctionAst",
    val paramVariable: String,
    val funcName: String,
    val paramList: List<NdsAst>,
) : NdsAst

data class AndCallFunctionAst(
    val type: String = "AndCallFunctionAst",
    val paramVariable: String,
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

data class MappingAst(
    val type: String = "MappingAst",
    val mappingName: String,
    val body: List<NdsAst>,
) : NdsAst

data class SqlAst(
    val type: String = "SqlAst",
    val sql: String,
) : NdsAst

data class ParamVariableAst(
    val type: String = "ParamVariableAst",
    val varName: String,
) : NdsAst

data class IfStatementAst(
    val type: String = "IfStatementAst",
    val test: NdsAst,
    val consequent: List<NdsAst>,
) : NdsAst

data class WhenStatementAst(
    val type: String = "WhenStatementAst",
    val ifStatementAst: List<NdsAst>,
    val elseStatementAst: List<NdsAst>?,
) : NdsAst

data class ExpressionAst(
    val type: String = "Expression",
    val operator: String,
    val left: NdsAst,
    val right: NdsAst,
) : NdsAst

