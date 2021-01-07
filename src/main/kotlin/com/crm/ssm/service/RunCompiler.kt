package com.crm.ssm.service

import com.crm.ssm.pojo.compile.CompileRequest
import com.crm.ssm.pojo.compile.CompileResult
import com.crm.ssm.service.util.Command
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.util.*
import kotlin.jvm.Throws
import java.util.ResourceBundle

@Service
class RunCompiler {
    private val resource = ResourceBundle.getBundle("compile")

    @Value("\${memoryLimit}")
    private var memoryLimit: String = ""

    @Value("\${cpuLimit}")
    private var cpuLimit: String = ""

    @Value("\${dockerRun}")
    private var _dockerRun: String = ""
    private val dockerRun: String
        get() {
            return _dockerRun.format(
                memoryLimit, cpuLimit
            )
        }

    @Value("\${dockerDel}")
    private var dockerDel: String = ""

    @Value("\${timeOut}")
    val timeOut: Long = -1L

    @Value("\${runTimeOutMsg}")
    private var timeOutMsg: String = ""

    @Value("\${fileCatchPath}")
    private var fileCatchPath: String = ""

    @Throws(FileAlreadyExistsException::class)
    fun run(s: String, compileType: CompileRequest.CompileType, uuid: String? = null, path: String? = null) =
        runBlocking<CompileResult> {
            // 1. 创建代码文件
            val id: String = uuid ?: UUID.randomUUID().toString()
            val f = File((path ?: fileCatchPath) + "/$id")
            val dir = File(path ?: fileCatchPath)
            if (!dir.exists()) dir.mkdirs()
            f.createNewFile()
            f.writeText(s, charset("UTF-8"))

            // 2. 使用命令行调用docker
            var result = ""
            try {
                withTimeout(timeOut) {
                    launch {
                        result = Command.exec(
                            dockerRun.replace("{filepath}", f.canonicalPath).replace("{containerName}", id)
                                .replace("{runPath}", resource.getString(compileType.name.toLowerCase() + "RunPath"))
                                .replace(
                                    "{imageName}",
                                    resource.getString(compileType.name.toLowerCase() + "ImageName")
                                )
                        )
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Command.exec(dockerDel.replace("{containerName}", id))
                return@runBlocking CompileResult(id, CompileResult.CompileResultType.WARN, listOf(timeOutMsg), null)
            } finally {
                f.delete()
            }

            // 3. 判断运行结果是否报错
            val r = Regex("^ERROR_FLAG\n(.*)",RegexOption.DOT_MATCHES_ALL)
            val res = r.matchEntire(result)

            if (res != null) {
                return@runBlocking CompileResult(id, CompileResult.CompileResultType.ERROR, listOf(res.groupValues[1]), null)
            }
            return@runBlocking CompileResult(id, CompileResult.CompileResultType.SUCCESS, null, listOf(result.replace(Regex("^SUCCESS_FLAG\n"),"")))
        }
}