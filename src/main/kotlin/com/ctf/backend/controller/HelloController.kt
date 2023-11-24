package com.ctf.backend.controller

import com.ctf.backend.util.API_VERSION_1
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(API_VERSION_1)
class HelloController {

    @GetMapping("/public/hello")
    fun getHW(): String {
        return "Hello, world!"
    }
}