package com.ctf.backend.service

import com.ctf.backend.database.entity.BlackList
import com.ctf.backend.database.entity.User
import com.ctf.backend.database.repo.BlackListDTO
import com.ctf.backend.database.repo.UserDao
import com.ctf.backend.database.repo.UserLoginParamsDao
import com.ctf.backend.errors.ResourceNotFoundException
import com.ctf.backend.mappers.UserMapper
import com.ctf.backend.models.request.UserUpdateRequest
import com.ctf.backend.models.response.TeamResponse
import com.ctf.backend.models.response.UserDeleteResponse
import com.ctf.backend.models.response.UserResponse
import com.ctf.backend.util.getPrincipal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserServiceImpl(
    val userRepository: UserDao,
    val userMapper: UserMapper,
    val userLPRepository: UserLoginParamsDao,
    val blackListRepository: BlackListDTO,
    val teamService: TeamService,
) : UserService {
    override fun getUserById(id: Long): UserResponse =
        userMapper.asUserResponse(userRepository.findUserByUserLoginParamsId(id).orElseThrow { ResourceNotFoundException("user $id") })

    override fun getMyProfile(): UserResponse =
        userMapper.asUserResponse(userRepository.findUserByUserLoginParamsId(getPrincipal()).orElseThrow { ResourceNotFoundException() })

    override fun createUser(user: User): User =
        userRepository.save(user)

    override fun deleteMyProfile(): UserDeleteResponse =
        deleteProfile(getPrincipal())

    override fun updateMyProfile(request: UserUpdateRequest): UserResponse =
        updateUser(request.apply { this.id = getPrincipal().toString() })

    override fun leaveFromTeam(teamId: Long): TeamResponse =
        teamService.deleteUserFromTeam(getPrincipal(), teamId)

    override fun getAllUsers(): Set<UserResponse> =
        userRepository.findAll().map { userMapper.asUserResponse(it) }.toSet()

    override fun deleteProfile(userId: Long): UserDeleteResponse {
        val user = userRepository.findUserById(userId).orElseThrow { ResourceNotFoundException("user $userId") }
        for (t in user.team) {
            teamService.deleteUserFromTeam(userId = user.id, teamId = t.id)
        }
        userRepository.delete(user)
        userLPRepository.deleteById(userId)
        blackListRepository.save(BlackList(userId))
        return UserDeleteResponse(message = "Вы удалили пользователя $userId")
    }

    override fun updateUser(request: UserUpdateRequest): UserResponse {
        val userId = request.id.toLong()
        val user = userRepository.findUserById(userId).orElseThrow { ResourceNotFoundException("user $userId") }
        val newUser = userMapper.asEntity(request)
        newUser.id = userId
        return userMapper.asUserResponse(userRepository.save(userMapper.updateEntity(user, newUser)))
    }
}
