package com.ctf.backend.controller

import com.ctf.backend.util.API_PUBLIC
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(API_PUBLIC)
class HelloController {

    @GetMapping("/hello")
    fun getHW(): String {
        return "Hello, world!"
    }

}