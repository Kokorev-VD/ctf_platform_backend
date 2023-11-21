package com.ctf.backend.errors

import org.springframework.http.HttpStatus

class CaptainNotInATeamException(
    userId: String,
    teamId: String,
) : ApiError(
    status = HttpStatus.BAD_REQUEST,
    message = "Капитан $userId не состоит в команде $teamId",
    debugMessage = "Captain $userId not in a team $teamId",
)