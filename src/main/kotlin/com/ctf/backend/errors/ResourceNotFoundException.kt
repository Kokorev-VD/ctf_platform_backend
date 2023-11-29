package com.ctf.backend.errors

import org.springframework.http.HttpStatus

class ResourceNotFoundException(
    data: Any? = null,
) : ApiError(
    status = HttpStatus.NOT_FOUND,
    message = "Такого ресурса не существует ${data ?: ""}",
    debugMessage = "Resource not found ${data ?: ""}",
)
