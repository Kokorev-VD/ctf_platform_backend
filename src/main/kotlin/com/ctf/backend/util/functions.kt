package com.ctf.backend.util

import com.ctf.backend.security.JwtParser
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

fun getPrincipal(): Long = (SecurityContextHolder.getContext().authentication.principal as Long)

fun getAuthorities(): MutableCollection<out GrantedAuthority> = (SecurityContextHolder.getContext().authentication.authorities!!)

fun createCode(): String{
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..10).
            map{
                allowedChars.random()
            }.joinToString("")
}