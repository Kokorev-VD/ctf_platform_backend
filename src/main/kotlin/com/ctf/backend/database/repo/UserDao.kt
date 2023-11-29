package com.ctf.backend.database.repo

import com.ctf.backend.database.entity.User
import java.util.*

interface UserDao : AppRepository<User> {

    fun findUserByUserLoginParamsId(userLoginParamsId: Long): Optional<User>

    fun findUserById(id: Long): Optional<User>
}
