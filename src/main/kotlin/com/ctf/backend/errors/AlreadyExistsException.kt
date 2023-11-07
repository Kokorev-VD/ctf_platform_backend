package com.ctf.backend.errors

import org.springframework.http.HttpStatus

class AlreadyExistsException(
    data: Any? = null
): ApiError(
    status = HttpStatus.BAD_REQUEST,
    message = "Уже существует ${data?:""}",
    debugMessage = "Already exists ${data?:""}",
)