package com.ctf.backend.models.response

data class TeamResponse(
    val rating: Long,
    val title: String,
    val info: String,
    val contacts: String,
    val preview: String,
    val captainId: Long,
    val members: Set<Long>,
)