package com.ctf.backend.controller

import com.ctf.backend.errors.ApiError
import com.ctf.backend.util.API_VERSION_1
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$API_VERSION_1/throw")
class ThrowController {

    @GetMapping
    fun throwError() {
        throw ApiError(status = HttpStatus.INTERNAL_SERVER_ERROR, message = "TEST")
    }
}
