package com.ctf.backend.service

import com.ctf.backend.database.entity.Team
import com.ctf.backend.database.entity.User
import com.ctf.backend.database.entity.UserLoginParams
import com.ctf.backend.database.repo.TeamDao
import com.ctf.backend.database.repo.UserDao
import com.ctf.backend.database.repo.UserLoginParamsDao
import com.ctf.backend.errors.*
import com.ctf.backend.mappers.TeamMapper
import com.ctf.backend.mappers.UserMapper
import com.ctf.backend.models.request.TeamCreationRequest
import com.ctf.backend.models.request.TeamUpdateRequest
import com.ctf.backend.models.request.UserUpdateRequest
import com.ctf.backend.models.response.*
import com.ctf.backend.util.getPrincipal
import org.hibernate.ObjectDeletedException
import org.springframework.stereotype.Service

@Service
class AdminServiceImpl(
    private val teamRepository: TeamDao,
    private val userRepository: UserDao,
    private val userLPRepository: UserLoginParamsDao,
    private val userMapper: UserMapper,
    private val teamMapper: TeamMapper,
) : AdminService{


    override fun addUserToTeam(userId: Long, teamId: Long): TeamResponse {
        check()
        val team = teamRepository.findTeamById(teamId).orElseThrow { ResourceNotFoundException(teamId) }
        if(team.members.contains(userRepository.findUserByUserLoginParamsId(userId).orElseThrow { ResourceNotFoundException(userId) })){
            throw AlreadyInTeamException()
        }
        team.members = (team.members as MutableSet<User>).apply {
            this.add(userRepository.findUserByUserLoginParamsId(userId).orElseThrow { ResourceNotFoundException(userId) })
        }
        teamRepository.save(team)
        return teamMapper.entityToResponse(team)
    }

    override fun deleteUserFromTeam(userId: Long, teamId: Long): TeamResponse {
        check()
        val team = teamRepository.findTeamById(teamId).orElseThrow { ResourceNotFoundException(teamId) }
        if(!team.members.contains(userRepository.findUserByUserLoginParamsId(userId).orElseThrow { ResourceNotFoundException(userId) })){
            throw UserNotInATeamException(userId, teamId)
        }
        team.members = (team.members as MutableSet<User>).apply {
            this.remove(userRepository.findUserByUserLoginParamsId(userId).orElseThrow { ResourceNotFoundException(userId) })
        }
        if(team.captain.id == userId && team.members.isNotEmpty()){
            team.captain = team.members.first()
        }
        teamRepository.save(team)
        if (team.members.isEmpty()){
            deleteTeam(team.id)
            throw DeletedObjectResponse("team ${team.id}")
        }
        return teamMapper.entityToResponse(team)
    }

    override fun getTeam(teamId: Long): CptTeamResponse {
        check()
        return teamMapper.entityToCptResponse(teamRepository.findTeamById(teamId).orElseThrow { ResourceNotFoundException(
            teamId
        ) })
    }

    override fun check() {
        if (!userRepository.findUserByUserLoginParamsId(getPrincipal()).orElseThrow { ResourceNotFoundException(
                getPrincipal()
            ) }.admin){
            throw NotEnoughAccessRightsException()
        }
    }

    override fun deleteTeam(teamId: Long): TeamDeleteResponse {
        check()
        teamRepository.findTeamById(teamId).orElseThrow{ResourceNotFoundException(teamId)}
        teamRepository.deleteById(teamId)
        return TeamDeleteResponse(message = "Вы удалили команду $teamId")
    }

    override fun deleteUser(userId: Long): UserDeleteResponse {
        check()
        val user = userRepository.findUserByUserLoginParamsId(userId).orElseThrow{ ResourceNotFoundException(userId) }
        for(t in user.team){
            deleteUserFromTeam(userId = user.id, teamId = t.id)
        }
        userRepository.delete(user)
        userLPRepository.deleteById(userId)
        return UserDeleteResponse(message = "Вы удалили пользователя $userId")
    }

    override fun createTeam(request: TeamCreationRequest, userId: Long): CptTeamResponse {
        check()
        return teamMapper.entityToCptResponse(teamRepository.save(teamMapper.requestToEntity(request).apply {
            captain = userRepository.findUserByUserLoginParamsId(userId).orElseThrow{ ResourceNotFoundException(userId) }
            this.members = setOf(captain)
        }))
    }

    override fun updateUser(request: UserUpdateRequest, userId: Long): UserResponse {
        check()
        val user : User = userRepository.findUserByUserLoginParamsId(userId).orElseThrow { ResourceNotFoundException(userId) }
        val newUser = userMapper.asEntity(request)
        return userMapper.asUserResponse(userRepository.save(userMapper.updateEntity(user, newUser)))
    }

    override fun updateTeam(request: TeamUpdateRequest, teamId: Long): CptTeamResponse {
        check()
        val team = teamRepository.findTeamById(teamId).orElseThrow{ ResourceNotFoundException(teamId) }
        val newTeam = teamMapper.updateRequestToEntity(request)
        if (newTeam.members.isEmpty()){
            deleteTeam(teamId)
            throw DeletedObjectResponse("team $teamId")
        }
        return teamMapper.entityToCptResponse(teamRepository.save(teamMapper.updateEntity(team, newTeam)))
    }

}