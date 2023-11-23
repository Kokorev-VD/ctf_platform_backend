package com.ctf.backend.errors

import org.springframework.http.HttpStatus

class CorruptedTeamCodeException : ApiError(
    status = HttpStatus.BAD_REQUEST,
    message = "Код введен неверно",
    debugMessage = "Wrong code",
)