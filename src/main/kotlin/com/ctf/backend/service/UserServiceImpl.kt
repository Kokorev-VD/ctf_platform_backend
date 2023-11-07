package com.ctf.backend.service

import com.ctf.backend.database.entity.User
import com.ctf.backend.database.repo.UserDao
import com.ctf.backend.errors.ResourceNotFoundException
import com.ctf.backend.mappers.UserMapper
import com.ctf.backend.models.response.UserResponse
import com.ctf.backend.util.getPrincipal
import org.springframework.stereotype.Service

@Service
class  UserServiceImpl(
    val userRepository: UserDao,
    val userMapper: UserMapper,
) : UserService {
    override fun getUserById(id: Long) : UserResponse {
        return userMapper.asUserResponse(userRepository.findUserByUserLoginParamsId(id).orElseThrow{ ResourceNotFoundException(id)})
    }

    override fun getMyProfile() : UserResponse {
        return userMapper.asUserResponse(userRepository.findUserByUserLoginParamsId(getPrincipal()).orElseThrow { ResourceNotFoundException() })
    }

    override fun createUser(user: User) : User {
        return userRepository.save(user)
    }

}