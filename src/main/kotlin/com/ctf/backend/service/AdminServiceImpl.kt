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
import com.ctf.backend.security.model.Authority
import com.ctf.backend.util.getAuthorities
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
        val team = teamRepository.findTeamById(teamId).orElseThrow { ResourceNotFoundException("team $teamId") }
        val user = userRepository.findUserByUserLoginParamsId(userId).orElseThrow { ResourceNotFoundException("user $userId") }
        if(team.members.contains(user)){
            throw AlreadyInTeamException()
        }
        team.members = (team.members as MutableSet<User>).apply {
            this.add(user)
        }
        teamRepository.save(team)
        return teamMapper.entityToResponse(team)
    }

    override fun deleteUserFromTeam(userId: Long, teamId: Long): TeamResponse {
        check()
        val team = teamRepository.findTeamById(teamId).orElseThrow { ResourceNotFoundException("team $teamId") }
        val user = userRepository.findUserByUserLoginParamsId(userId).orElseThrow { ResourceNotFoundException("user $userId") }
        if(!team.members.contains(user)){
            throw UserNotInATeamException(userId, teamId)
        }
        team.members = (team.members as MutableSet<User>).apply {
            this.remove(user)
        }
        if(team.captain.id == userId && team.members.isNotEmpty()){
            team.captain = team.members.first()
        }
        teamRepository.save(team)
        if (team.members.isEmpty()){
            deleteTeam(team.id)
        }
        return teamMapper.entityToResponse(team)
    }

    override fun getTeam(teamId: Long): CptTeamResponse {
        check()
        return teamMapper.entityToCptResponse(teamRepository.findTeamById(teamId).orElseThrow { ResourceNotFoundException("team $teamId") })
    }

    override fun check() {
        val authorities = getAuthorities()
        var admin = false
        for (x in authorities){
            if(x.authority == Authority.ADMIN.authority.authority){
                admin = true
                break
            }
        }
        if (!admin){
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
        val user = userRepository.findUserByUserLoginParamsId(userId).orElseThrow{ ResourceNotFoundException("user $userId") }
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
            captain = userRepository.findUserByUserLoginParamsId(userId).orElseThrow{ ResourceNotFoundException("user $userId") }
            this.members = setOf(captain)
        }))
    }

    override fun updateUser(request: UserUpdateRequest): UserResponse {
        check()
        val userId = request.id.toLong()
        val user = userRepository.findUserByUserLoginParamsId(userId).orElseThrow { ResourceNotFoundException("user $userId") }
        val newUser = userMapper.asEntity(request)
        newUser.id = userId
        return userMapper.asUserResponse(userRepository.save(userMapper.updateEntity(user, newUser)))
    }

    override fun updateTeam(request: TeamUpdateRequest): CptTeamResponse {
        check()
        val teamId = request.id.toLong()
        val team = teamRepository.findTeamById(teamId).orElseThrow{ ResourceNotFoundException("team $teamId") }
        if(request.members.isEmpty()){
            deleteTeam(teamId)
            throw DeletedObjectResponse("team $teamId")
        }
        if (!request.members.contains(request.captainId)) {
            throw CaptainNotInATeamException(request.captainId, request.id)
        }
        val newTeam = teamMapper.updateRequestToEntity(request)
        return teamMapper.entityToCptResponse(teamRepository.save(teamMapper.updateEntity(team, newTeam)))
    }

}