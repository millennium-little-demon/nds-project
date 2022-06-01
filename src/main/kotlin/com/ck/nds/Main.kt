package com.ck.nds

import com.ck.nds.parse.NdsParse
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper

/**
 *
 * @author 陈坤
 * 2022/5/29 16:17
 */

class Main


fun main() {
    val text = Main::class.java.classLoader.getResource("t2.txt")?.readText()
        ?: throw RuntimeException("文件不存在")

    val om = ObjectMapper()
    om.setSerializationInclusion(JsonInclude.Include.NON_NULL)

    val programAst = NdsParse(text).programAst()

    om.writerWithDefaultPrettyPrinter()
        .writeValueAsString(programAst)
        .also { println(it) }


}