package com.ctf.backend.service.auth

import com.ctf.backend.database.entity.UserLoginParams
import com.ctf.backend.security.JwtParser
import com.ctf.backend.security.model.Authority
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

@Service
class JwtHelper(
    private val jwt: JwtParser,

    @Value("\${spring.security.jwt.access.lifetime}")
    private val accessTokenLifeTime: Long,
) {

    fun generateAccessToken(user: UserLoginParams): String {
        val authorities = mutableListOf(Authority.USER)
        return jwt.createToken(
            "userId" to user.id,
            "permissions" to emptyList<String>(),
            "authoroties" to authorities,
            expiration = getAccessTokenExpiration()
        )
    }

    private fun getAccessTokenExpiration(): Date =
        Instant.now().plus(accessTokenLifeTime, ChronoUnit.DAYS).let { Date.from(it) }
}