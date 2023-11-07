package com.ctf.backend.models.response

class UserRegistrationResponse(
    val id: Long,
    val email: String,
){
    lateinit var accessJwt: String
}