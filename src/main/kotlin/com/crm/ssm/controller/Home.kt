package com.crm.ssm.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
open class Home {

    @GetMapping("/PYTHON3")
    open fun toPython():String{
        return "/python/index.html"
    }

    @GetMapping("/KOTLIN")
    open fun toKoltin():String{
        return "/kotlin/index.html"
    }
}