package com.ctf.backend.errors

import org.springframework.http.HttpStatus

class AlreadyInTeamException: ApiError(
    status = HttpStatus.BAD_REQUEST,
    message = "Вы уже состоите в этой команде",
    debugMessage = "Already in this team",
)
