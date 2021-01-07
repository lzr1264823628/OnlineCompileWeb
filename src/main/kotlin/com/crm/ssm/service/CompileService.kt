package com.crm.ssm.service

import com.crm.ssm.pojo.compile.CompileRequest
import com.crm.ssm.pojo.compile.CompileResult
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct


@Service()
open class CompileService {
    @Value("\${concurrentRunNumber}")
    private val compileNumber:Int =  -1
    @Value("\${waitTimeOutMsg}")
    private val waitTimeOutMsg:String = ""
    @Autowired
    private val _channel:Channel<Int>?=null
    private val channel:Channel<Int>
        get() {
            return _channel?:throw NullPointerException("channel failed to create")
        }

    @Autowired
    private val _runCompiler:RunCompiler?=null
    private val runCompiler:RunCompiler
        get() {
            return _runCompiler?:throw NullPointerException()
        }


    @PostConstruct
    fun init(){
        GlobalScope.launch {
            println("初始化管道${compileNumber}")
            repeat(compileNumber){
                channel.send(it)
                println("初始化${it}")
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun run(s:String,compileType: CompileRequest.CompileType) = runBlocking<CompileResult>{
        if (channel.isEmpty) println("服务器繁忙")

        val cId:Int
        try {
            withTimeout(runCompiler.timeOut){
                println("等待资源")
                cId = channel.receive()
                println("cId: $cId")
                println("已获取资源")
            }
        }catch (e:TimeoutCancellationException){
            return@runBlocking CompileResult("",CompileResult.CompileResultType.WARN, listOf(waitTimeOutMsg), null)
        }
        val res:CompileResult
        try {
             res = runCompiler.run(s,compileType)
        }finally {
            channel.send(cId)
        }
        return@runBlocking res
    }
}