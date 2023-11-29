package com.ctf.backend.service

import com.ctf.backend.database.entity.User
import com.ctf.backend.database.repo.TeamDao
import com.ctf.backend.database.repo.UserDao
import com.ctf.backend.errors.*
import com.ctf.backend.mappers.TeamMapper
import com.ctf.backend.models.request.TeamCreationRequest
import com.ctf.backend.models.request.TeamUpdateRequest
import com.ctf.backend.models.response.CptTeamResponse
import com.ctf.backend.models.response.TeamDeleteResponse
import com.ctf.backend.models.response.TeamResponse
import com.ctf.backend.util.getPrincipal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TeamServiceImpl(
    private val teamRepository: TeamDao,
    private val mapper: TeamMapper,
    private val userRepository: UserDao,
) : TeamService {

    override fun getTeamsByCptId(cptId: Long): List<TeamResponse> {
        userRepository.findUserByUserLoginParamsId(cptId).orElseThrow { ResourceNotFoundException("user $cptId") }
        return teamRepository.findTeamsByUserId(cptId).map { mapper.entityToResponse(it) }
    }

    override fun getTeamById(id: Long): TeamResponse =
        mapper.entityToResponse(teamRepository.findTeamById(id).orElseThrow { ResourceNotFoundException("team $id") })

    override fun getTeamsByMemberId(memberId: Long): List<TeamResponse> {
        userRepository.findUserByUserLoginParamsId(memberId).orElseThrow { ResourceNotFoundException("user $memberId") }
        return teamRepository.findTeamsByUserId(memberId).map { mapper.entityToResponse(it) }
    }

    override fun getMyCptTeams(): List<CptTeamResponse> =
        teamRepository.findTeamsByCaptainId(getPrincipal()).map { mapper.entityToCptResponse(it) }

    override fun getMyTeams(): List<CptTeamResponse> =
        getTeamsByMemberId(getPrincipal()).map { mapper.responseToCptResponse(it) }

    override fun getAllTeams(): List<TeamResponse> =
        teamRepository.findAll().map { mapper.entityToResponse(it) }

    override fun cptDeleteTeam(teamId: Long): TeamDeleteResponse {
        check(teamId)
        return deleteTeam(teamId)
    }

    override fun cptUpdateTeam(request: TeamUpdateRequest): CptTeamResponse {
        check(request.id.toLong())
        return updateTeam(request)
    }

    override fun cptDeleteUserFromTeam(userId: Long, teamId: Long): TeamResponse {
        check(teamId)
        return deleteUserFromTeam(userId, teamId)
    }

    override fun cptAddUserToTeam(userId: Long, teamId: Long): CptTeamResponse {
        check(teamId)
        return addUserToTeam(userId, teamId)
    }

    override fun joinTeam(teamId: String, code: String): TeamResponse {
        val team = teamRepository.findTeamById(teamId.toLong()).orElseThrow { ResourceNotFoundException("team $teamId") }
        if (team.code != code) {
            throw CorruptedTeamCodeException()
        }
        if (userRepository.findUserByUserLoginParamsId(getPrincipal()).orElseThrow {
                ResourceNotFoundException(
                    getPrincipal(),
                )
            } in team.members
        ) {
            throw AlreadyInTeamException(userId = getPrincipal().toString(), teamId = teamId)
        }
        val newMembers: MutableSet<User> = team.members as MutableSet<User>
        newMembers.add(userRepository.findUserByUserLoginParamsId(getPrincipal()).orElseThrow { ResourceNotFoundException("user ${getPrincipal()}") },)
        team.members = newMembers
        teamRepository.save(team)
        return mapper.entityToResponse(team)
    }

    override fun addUserToTeam(userId: Long, teamId: Long): CptTeamResponse {
        val team = teamRepository.findTeamById(teamId).orElseThrow { ResourceNotFoundException("team $teamId") }
        val user = userRepository.findUserByUserLoginParamsId(userId).orElseThrow { ResourceNotFoundException("user $userId") }
        if (team.members.contains(user)) {
            throw AlreadyInTeamException(userId = userId.toString(), teamId = teamId.toString())
        }
        team.members = (team.members as MutableSet<User>).apply {
            this.add(user)
        }
        teamRepository.save(team)
        return mapper.entityToCptResponse(team)
    }

    override fun deleteUserFromTeam(userId: Long, teamId: Long): TeamResponse {
        val team = teamRepository.findTeamById(teamId).orElseThrow { ResourceNotFoundException("team $teamId") }
        val user = userRepository.findUserByUserLoginParamsId(userId).orElseThrow { ResourceNotFoundException("user $userId") }
        if (!team.members.contains(user)) {
            throw UserNotInATeamException(userId, teamId)
        }
        team.members = (team.members as MutableSet<User>).apply {
            this.remove(user)
        }
        if (team.captain.id == userId && team.members.isNotEmpty()) {
            team.captain = team.members.first()
        }
        teamRepository.save(team)
        if (team.members.isEmpty()) {
            deleteTeam(team.id)
        }
        return mapper.entityToResponse(team)
    }

    override fun check(teamId: Long) {
        if (teamRepository.findTeamById(teamId).orElseThrow { ResourceNotFoundException("team $teamId") }.captain.userLoginParams.id != getPrincipal()) {
            throw NotEnoughAccessRightsException()
        }
    }

    override fun createTeam(request: TeamCreationRequest): TeamResponse {
        val team = mapper.requestToEntity(request)
        team.members = setOf<User>(userRepository.findUserByUserLoginParamsId(getPrincipal()).orElseThrow { ResourceNotFoundException("user ${getPrincipal()}") })
        team.captain = userRepository.findUserByUserLoginParamsId(getPrincipal()).orElseThrow { ResourceNotFoundException("user ${getPrincipal()}") }
        return mapper.entityToResponse(teamRepository.save(team))
    }

    override fun deleteTeam(teamId: Long): TeamDeleteResponse {
        teamRepository.findTeamById(teamId).orElseThrow { ResourceNotFoundException("team $teamId") }
        teamRepository.deleteById(teamId)
        return TeamDeleteResponse(message = "Вы удалили команду $teamId")
    }

    override fun updateTeam(request: TeamUpdateRequest): CptTeamResponse {
        val teamId = request.id.toLong()
        val team = teamRepository.findTeamById(teamId).orElseThrow { ResourceNotFoundException("team $teamId") }
        if (request.members.isEmpty()) {
            deleteTeam(teamId)
            throw DeletedObjectResponse("team $teamId")
        }
        if (!request.members.contains(request.captainId)) {
            throw CaptainNotInATeamException(request.captainId, request.id)
        }
        val newTeam = mapper.updateRequestToEntity(request)
        return mapper.entityToCptResponse(teamRepository.save(mapper.updateEntity(team, newTeam)))
    }
}
