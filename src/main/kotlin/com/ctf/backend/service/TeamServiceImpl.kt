package com.ctf.backend.service

import com.ctf.backend.database.entity.User
import com.ctf.backend.database.repo.TeamDao
import com.ctf.backend.database.repo.UserDao
import com.ctf.backend.errors.AlreadyInTeamException
import com.ctf.backend.errors.ResourceNotFoundException
import com.ctf.backend.mappers.TeamMapper
import com.ctf.backend.models.request.TeamCreationRequest
import com.ctf.backend.models.response.CptTeamResponse
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

    override fun addUserToTeam(code: String): TeamResponse {
        val team = teamRepository.findTeamByCode(code).orElseThrow { ResourceNotFoundException(code) }
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
        return mapper.entityToResponse(team)
    }



    override fun createTeam(request: TeamCreationRequest): TeamResponse {
        val team = mapper.requestToEntity(request)
        team.code = createCode()
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
}