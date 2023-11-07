package com.ctf.backend.errors

import org.springframework.http.HttpStatus

class UserNotInATeamException(val userId: Long?, val teamId: Long?) : ApiError(
    status = HttpStatus.BAD_REQUEST,
    message = "Пользователя ${userId?:""} нет в команде ${teamId?:""}",
    debugMessage = "User ${userId?:""} not found in team ${teamId?:""}"
)