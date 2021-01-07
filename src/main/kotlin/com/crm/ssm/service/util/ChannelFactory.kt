package com.crm.ssm.service.util

import kotlinx.coroutines.channels.Channel
import java.util.*


object ChannelFactory {
    @JvmStatic
    fun getChannel(): Channel<Int> {
        val res = ResourceBundle.getBundle("compile")
        val num = res.getString("concurrentRunNumber")
        return Channel(num.toInt())
    }
}