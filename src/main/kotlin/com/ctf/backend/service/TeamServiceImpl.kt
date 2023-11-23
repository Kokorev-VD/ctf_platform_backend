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
import com.ctf.backend.util.createCode
import com.ctf.backend.util.getPrincipal
import org.springframework.stereotype.Service

@Service
class TeamServiceImpl(
    private val teamRepository: TeamDao,
    private val mapper: TeamMapper,
    private val userRepository: UserDao,
) : TeamService {

    override fun getTeamsByCptId(cptId: Long): Set<TeamResponse> {
        val teams = teamRepository.findTeamsByCaptainId(cptId)
        val res = mutableSetOf<TeamResponse>()
        for (team in teams){
            res.add(mapper.entityToResponse(team))
        }
        return res
    }

    override fun getTeamById(id: Long): TeamResponse {
        return mapper.entityToResponse(teamRepository.findTeamById(id).orElseThrow{ ResourceNotFoundException(id) })
    }

    override fun getTeamsByMemberId(memberId: Long): Set<TeamResponse> {
        val teams = teamRepository.findTeamsByUserId(memberId)
        val res = mutableSetOf<TeamResponse>()
        for (team in teams){
            res.add(mapper.entityToResponse(team))
        }
        return res
    }

    override fun addUserToTeam(teamId: String, code: String): TeamResponse {
        val team = teamRepository.findTeamById(teamId.toLong()).orElseThrow { ResourceNotFoundException(teamId) }
        if (team.code != code){
            throw CorruptedTeamCodeException()
        }
        if (userRepository.findUserByUserLoginParamsId(getPrincipal()).orElseThrow{ ResourceNotFoundException(
                getPrincipal()
            ) } in team.members){
            throw AlreadyInTeamException()
        }
        val newMembers: MutableSet<User> = team.members as MutableSet<User>
        newMembers.add(userRepository.findUserByUserLoginParamsId(getPrincipal()).orElseThrow{ ResourceNotFoundException(
            getPrincipal()
        ) })
        team.members = newMembers
        teamRepository.save(team)
        return mapper.entityToResponse(team)
    }

    override fun cptAddUserToTeam(userId: Long, teamId: Long): TeamResponse {
        check(teamId)
        val team = teamRepository.findTeamById(teamId).orElseThrow { ResourceNotFoundException(teamId) }
        if(team.members.contains(userRepository.findUserByUserLoginParamsId(userId).orElseThrow { ResourceNotFoundException(userId) })){
            throw AlreadyInTeamException()
        }
        val newMembers = (team.members as MutableSet<User>)
        newMembers.add(userRepository.findUserByUserLoginParamsId(userId).orElseThrow { ResourceNotFoundException(userId) })
        team.members = newMembers
        teamRepository.save(team)
        return mapper.entityToResponse(team)
    }

    override fun cptDeleteUserFromTeam(userId: Long, teamId: Long): TeamResponse {
        check(teamId)
        val team = teamRepository.findTeamById(teamId).orElseThrow { ResourceNotFoundException(teamId) }
        if(!team.members.contains(userRepository.findUserByUserLoginParamsId(userId).orElseThrow { ResourceNotFoundException(userId) })){
            throw UserNotInATeamException(userId, teamId)
        }
        team.members = (team.members as MutableSet<User>).apply {
            this.remove(userRepository.findUserByUserLoginParamsId(userId).orElseThrow { ResourceNotFoundException(userId) })
        }
        teamRepository.save(team)
        return mapper.entityToResponse(team)
    }
    override fun check(teamId: Long) {
        if(teamRepository.findTeamById(teamId).orElseThrow{ ResourceNotFoundException(teamId) }.captain.userLoginParams.id != getPrincipal()){
            throw NotEnoughAccessRightsException()
        }
    }


    override fun createTeam(request: TeamCreationRequest): TeamResponse {
        val team = mapper.requestToEntity(request)
        team.members = setOf<User>(userRepository.findUserByUserLoginParamsId(getPrincipal()).orElseThrow { ResourceNotFoundException(getPrincipal())})
        team.captain = userRepository.findUserByUserLoginParamsId(getPrincipal()).orElseThrow{ ResourceNotFoundException(
            getPrincipal()
        ) }
        return mapper.entityToResponse(teamRepository.save(team))
    }

    override fun getMyCptTeams(): Set<CptTeamResponse> {
        val res = mutableSetOf<CptTeamResponse>()
        for(team in teamRepository.findTeamsByCaptainId(userRepository.findUserByUserLoginParamsId(getPrincipal()).orElseThrow{ ResourceNotFoundException(
            getPrincipal()
        ) }.id)){
            res.add(mapper.entityToCptResponse(team))
        }
        return res
    }

    override fun getMyTeams(): Set<TeamResponse> {
        TODO("Not yet implemented")
    }

    override fun getAllTeams(): Set<TeamResponse> {
        val res = mutableSetOf<TeamResponse>()
        for(team in teamRepository.findAll()){
            res.add(mapper.entityToResponse(team))
        }
        return res
    }

    override fun deleteTeam(teamId: Long): TeamDeleteResponse {
        check(teamId)
        teamRepository.deleteById(teamId)
        return TeamDeleteResponse(message = "Вы удалили команду $teamId")
    }

    override fun updateTeam(request: TeamUpdateRequest): TeamResponse {
        TODO()
    }
}