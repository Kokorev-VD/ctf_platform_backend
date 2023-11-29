package com.ctf.backend.errors

import org.springframework.http.HttpStatus

class CorruptedTokenException : ApiError(
    status = HttpStatus.UNAUTHORIZED,
    message = "Войдите в аккаунт",
    debugMessage = "Log into your account",
)
