package com.ctf.backend.errors

import org.springframework.http.HttpStatus

class NotEnoughAccessRightsException() : ApiError(
    status = HttpStatus.FORBIDDEN,
    message = "У вас недостаточно прав доступа",
    debugMessage = "You don't have enough access rights"
)
