package com.ctf.backend.service

import com.ctf.backend.database.repo.TeamDao
import com.ctf.backend.database.repo.UserDao
import com.ctf.backend.errors.*
import com.ctf.backend.mappers.TeamMapper
import com.ctf.backend.models.request.TeamCreationRequest
import com.ctf.backend.models.request.TeamUpdateRequest
import com.ctf.backend.models.request.UserUpdateRequest
import com.ctf.backend.models.response.*
import org.springframework.stereotype.Service

@Service
class AdminServiceImpl(
    private val teamRepository: TeamDao,
    private val userRepository: UserDao,
    private val teamMapper: TeamMapper,
    private val teamService: TeamService,
    private val userService: UserService,
) : AdminService{

    override fun addUserToTeam(userId: Long, teamId: Long): CptTeamResponse =
        teamService.addUserToTeam(userId, teamId)

    override fun deleteUserFromTeam(userId: Long, teamId: Long): TeamResponse =
        teamService.deleteUserFromTeam(userId, teamId)

    override fun getTeam(teamId: Long): CptTeamResponse =
        teamMapper.entityToCptResponse(teamRepository.findTeamById(teamId).orElseThrow { ResourceNotFoundException("team $teamId") })

    override fun deleteTeam(teamId: Long): TeamDeleteResponse =
        teamService.deleteTeam(teamId)

    override fun deleteUser(userId: Long): UserDeleteResponse =
        userService.deleteProfile(userId)

    override fun createTeam(request: TeamCreationRequest, userId: Long): CptTeamResponse =
        teamMapper.entityToCptResponse(teamRepository.save(teamMapper.requestToEntity(request).apply {
            captain = userRepository.findUserByUserLoginParamsId(userId).orElseThrow{ ResourceNotFoundException("user $userId") }
            this.members = setOf(captain)
        }))


    override fun updateUser(request: UserUpdateRequest): UserResponse =
        userService.updateUser(request)

    override fun updateTeam(request: TeamUpdateRequest): CptTeamResponse =
        teamService.updateTeam(request)
}