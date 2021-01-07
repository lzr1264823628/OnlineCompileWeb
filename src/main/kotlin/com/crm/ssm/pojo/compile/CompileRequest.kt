package com.crm.ssm.pojo.compile

data class CompileRequest(
    val code:String = "",
    val compileType:CompileType?=null
){
    enum class CompileType(val type:Int ){
        KOTLIN(0),PYTHON3(1)
    }
}