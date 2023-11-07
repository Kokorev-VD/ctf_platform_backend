package com.ctf.backend.service

import com.ctf.backend.database.entity.User
import com.ctf.backend.database.repo.TeamDao
import com.ctf.backend.database.repo.UserDao
import com.ctf.backend.errors.AlreadyInTeamException
import com.ctf.backend.errors.NotEnoughAccessRightsException
import com.ctf.backend.errors.ResourceNotFoundException
import com.ctf.backend.errors.UserNotInATeamException
import com.ctf.backend.mappers.TeamMapper
import com.ctf.backend.mappers.UserMapper
import com.ctf.backend.models.response.CptTeamResponse
import com.ctf.backend.models.response.TeamResponse
import com.ctf.backend.util.getPrincipal
import org.springframework.stereotype.Service

@Service
class AdminServiceImpl(
    private val teamRepository: TeamDao,
    private val userRepository: UserDao,
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
        teamRepository.save(team)
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
}