package com.crm.ssm.controller

import com.crm.ssm.pojo.compile.CompileRequest
import com.crm.ssm.pojo.compile.CompileResult
import com.crm.ssm.service.CompileService

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.lang.Exception

@Controller
@RequestMapping("/compile")
open class Compile {
    @Autowired
    val compileService: CompileService?=null

    @ExperimentalCoroutinesApi
    @PostMapping()
    @ResponseBody
    open fun compile(@RequestBody cq:CompileRequest): CompileResult? {
        if (cq.compileType!=null){
            return compileService?.run(cq.code, cq.compileType)
        }else{
            throw Exception("compileType 参数错误")
        }
    }

    @RequestMapping(method = [RequestMethod.OPTIONS])
    @ResponseBody
    open fun options(): Array<CompileRequest.CompileType> {
        return CompileRequest.CompileType.values()
    }

}