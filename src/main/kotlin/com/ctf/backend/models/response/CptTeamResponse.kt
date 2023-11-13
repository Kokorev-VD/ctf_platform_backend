package com.ctf.backend.models.response

data class CptTeamResponse(
    val id: String,
    val rating: Long,
    val title: String,
    val info: String,
    val contacts: String,
    val preview: String,
    val captainId: String,
    val code: String,
    val members: Set<String>,
)
