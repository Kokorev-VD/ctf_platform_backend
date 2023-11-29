package com.ctf.backend.controller

import com.ctf.backend.models.request.LoginRequest
import com.ctf.backend.models.request.RegistrationRequest
import com.ctf.backend.models.response.LoginResponse
import com.ctf.backend.models.response.UserRegistrationResponse
import com.ctf.backend.service.auth.AuthService
import com.ctf.backend.util.API_PUBLIC
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$API_PUBLIC/auth")
class AuthController(
    private val service: AuthService,
) {

    @PostMapping("/registration")
    fun register(
        @Valid @RequestBody
        request: RegistrationRequest,
    ): UserRegistrationResponse {
        return service.registration(request)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): LoginResponse {
        return service.login(request)
    }
}
