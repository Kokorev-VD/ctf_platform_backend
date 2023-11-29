package com.ctf.backend.models.request

import com.ctf.backend.util.EMAIL_REGEX
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RegistrationRequest(
    @field:Pattern(regexp = EMAIL_REGEX)
    @field:Size(min = 4, max = 120)
    val email: String,
    @field:Size(min = 8, max = 255)
    val password: String,
    val name: String,
    val surname: String,
)
