package com.ctf.backend.service

import com.ctf.backend.database.entity.User
import com.ctf.backend.models.request.UserUpdateRequest
import com.ctf.backend.models.response.UserDeleteResponse
import com.ctf.backend.models.response.UserResponse

interface UserService {

    fun getUserById(id: Long) : UserResponse

    fun getMyProfile() : UserResponse

    fun createUser(user: User) : User

    fun getAllUsers() : Set<UserResponse>

    fun deleteMyProfile() : UserDeleteResponse

    fun updateMyProfile(request: UserUpdateRequest) : UserResponse

}