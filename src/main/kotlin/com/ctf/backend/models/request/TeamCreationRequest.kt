package com.ctf.backend.models.request

import jakarta.validation.constraints.Size

data class TeamCreationRequest(
    @field:Size(min = 1, max = 32)
    val title: String,
    @field:Size(max = 300)
    val info: String,
    @field:Size(min = 3, max = 20)
    val contacts: String,
    @field:Size(max = 300)
    val preview: String,
)
