package com.crm.ssm.service.util

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset

object Command {
    private fun String.execute(): Process {
        val runtime = Runtime.getRuntime()
        return runtime.exec(this)
    }

    private fun Process.text(charset: Charset = Charset.forName("UTF-8")): String {
        val output: String
        val errorStream = this.errorStream
        val inputStream = this.inputStream
        val isr = InputStreamReader(inputStream, charset)
        val esr = InputStreamReader(errorStream, charset)
        val reader = BufferedReader(isr)
        val errReader = BufferedReader(esr)
        fun BufferedReader.getText(): String {
            var output1 = ""
            var line: String? = ""
            while (line != null) {
                line = this.readLine()
                if (line != null)
                    output1 += if (output1.isEmpty()) line else "\n" + line
            }
            return output1
        }

        val err = errReader.getText()
        output = if (err.isNotEmpty()) {
            "ERROR:$err"
        } else
            reader.getText()
        this.waitFor()
        isr.close()
        esr.close()
        reader.close()
        errReader.close()
        return output
    }

    fun exec(s: String): String {
        return s.execute().text()
    }
}