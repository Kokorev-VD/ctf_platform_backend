package com.ctf.backend.errors

import org.springframework.http.HttpStatus

class AlreadyInTeamException(userId: String, teamId: String) : ApiError(
    status = HttpStatus.BAD_REQUEST,
    message = "Пользователь $userId уже состоит в команде $teamId",
    debugMessage = "User $userId already in team $teamId",
)
