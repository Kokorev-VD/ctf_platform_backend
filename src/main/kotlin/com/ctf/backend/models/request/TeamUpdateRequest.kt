package com.ctf.backend.models.request

data class TeamUpdateRequest(
    val id: String,
    val rating: Long,
    val title: String,
    val info: String,
    val contacts: String,
    val preview: String,
    val captainId: String,
    val members: Set<String>,
)
