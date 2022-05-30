package com.ck.nds

import com.ck.nds.parse.Parse
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

/**
 *
 * @author 陈坤
 * 2022/5/29 16:17
 */

class Main

fun main() {

    val om = ObjectMapper().apply {
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }


    val code = Main::class.java.classLoader.getResource("T.txt")?.readText()?: throw RuntimeException("文件不存在")

    val parse = Parse(code)

    parse.program()
//        .also { println(it) }
        .also {
            println(
                om.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(it)
            )
        }
}