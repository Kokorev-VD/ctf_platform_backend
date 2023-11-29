package com.ctf.backend.models.request

data class UserUpdateRequest(
    var id: String,
    val name: String,
    val surname: String,
    val rating: Long,
)
