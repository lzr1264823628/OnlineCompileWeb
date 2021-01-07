package com.crm.ssm.pojo.compile



data class CompileResult(
    val id:String,
    val type:CompileResultType,
    val errorText: List<String>?,
    val Text:List<String>?
){
    enum class CompileResultType{
        INFO,ERROR,WARN,SUCCESS
    }
}
