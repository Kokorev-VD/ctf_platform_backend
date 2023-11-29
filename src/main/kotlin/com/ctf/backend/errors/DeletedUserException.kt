package com.ctf.backend.errors

import org.springframework.http.HttpStatus

class DeletedUserException : ApiError(
    status = HttpStatus.UNAUTHORIZED,
    message = "Ваш аккаунт удален",
    debugMessage = "Your account has been deleted",
)
