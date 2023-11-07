package com.ctf.backend.models.response

data class UserResponse(
    val name: String,
    val surname: String,
    val rating: Long,
    val email: String,
    val cptTeams: Set<Long>,
    val teams: Set<Long>,
)
