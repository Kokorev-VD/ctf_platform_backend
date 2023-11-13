package com.ctf.backend.models.response

data class UserResponse(
    val id: String,
    val name: String,
    val surname: String,
    val rating: Long,
    val email: String,
    val admin: Boolean,
    val cptTeams: Set<String>,
    val teams: Set<String>,
)
