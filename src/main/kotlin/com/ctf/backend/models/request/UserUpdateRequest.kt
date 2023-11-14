package com.ctf.backend.models.request

data class UserUpdateRequest(
    val name: String,
    val surname: String,
    val rating: Long,
    val email: String,
    val admin: Boolean,
    val cptTeams: Set<String>,
    val teams: Set<String>,
)