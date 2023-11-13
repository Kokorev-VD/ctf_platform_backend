package com.ctf.backend.models.request

data class AddUserRequest(
    val teamId: String,
    val code: String,
)
