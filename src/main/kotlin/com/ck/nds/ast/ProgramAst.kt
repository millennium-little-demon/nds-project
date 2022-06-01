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

data class MetadataInfoAst(
    val type: String = "MetadataInfoAst",
    val metadataMap: Map<String, String>,
) : NdsAst

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

data class IfAst(
    val type: String = "IfAst",
    val expression: Expression,
    val consequent: NdsAst,
) : NdsAst

data class Expression(
    val type: String = "Expression",
    val operator: String,
    val left: NdsAst,
    val right: NdsAst,
) : NdsAst


data class LogicalOR(
    val type: String = "LogicalOR",
//    val left: LogicalOR
    val logicalAND: LogicalAND,
) : NdsAst

data class LogicalAND(
    val type: String = "LogicalAND",
    val opt: String,

    ) : NdsAst

data class Equality(
    val type: String = "Equality",

    ) : NdsAst