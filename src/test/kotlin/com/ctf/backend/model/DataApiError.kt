package com.ctf.backend.model

import com.fasterxml.jackson.annotation.JsonFormat
data class DataApiError(
    val message: String,
    val status: String,
    val debugMessage: String,
    @JsonFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss[.SSS]")
    val timestamp: String,
)
