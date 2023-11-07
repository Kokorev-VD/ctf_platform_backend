package com.ctf.backend.database.repo

import com.ctf.backend.database.entity.User
import com.ctf.backend.database.entity.UserLoginParams
import java.util.*

interface UserDao : AppRepository<User> {

    fun findUserByUserLoginParamsId(userLoginParamsId: Long): Optional<User>


}