package com.ctf.backend.database.repo

import com.ctf.backend.database.entity.UserLoginParams
import java.util.*

interface UserLoginParamsDao : AppRepository<UserLoginParams> {

    fun findByEmail(email: String): Optional<UserLoginParams>

    fun existsByEmail(email: String): Boolean
}
