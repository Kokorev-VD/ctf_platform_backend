package com.ctf.backend.service.auth

import com.ctf.backend.models.request.LoginRequest
import com.ctf.backend.models.request.RegistrationRequest
import com.ctf.backend.models.response.LoginResponse
import com.ctf.backend.models.response.UserRegistrationResponse
import com.ctf.backend.models.response.UserResponse

interface AuthService {

    fun registration (request: RegistrationRequest) : UserRegistrationResponse

    fun login (request: LoginRequest) : LoginResponse

}