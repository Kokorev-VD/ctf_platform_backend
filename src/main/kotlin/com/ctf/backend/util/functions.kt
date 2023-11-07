package com.ctf.backend.util

import org.springframework.security.core.context.SecurityContextHolder

fun getPrincipal(): Long = (SecurityContextHolder.getContext().authentication.principal as Long)

fun getAuthorities() : String = SecurityContextHolder.getContext().authentication.authorities.toString()

fun createCode(): String{
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..10).
            map{
                allowedChars.random()
            }.joinToString("")
}