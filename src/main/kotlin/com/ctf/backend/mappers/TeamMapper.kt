package com.ctf.backend.mappers

import com.ctf.backend.database.entity.Team
import com.ctf.backend.database.entity.User
import com.ctf.backend.database.repo.TeamDao
import com.ctf.backend.database.repo.UserDao
import com.ctf.backend.errors.AlreadyExistsException
import com.ctf.backend.errors.ResourceNotFoundException
import com.ctf.backend.models.request.TeamCreationRequest
import com.ctf.backend.models.request.TeamUpdateRequest
import com.ctf.backend.models.response.CptTeamResponse
import com.ctf.backend.models.response.TeamResponse
import com.ctf.backend.util.createCode
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Component
class TeamMapper (
    private val userRepository: UserDao,
    private val teamRepository: TeamDao,
) {

    fun entityToResponse(entity: Team) : TeamResponse{
        val members = mutableSetOf<String>()
        for(member in entity.members) {
            members.add(member.userLoginParams.id.toString())
        }
        return TeamResponse(
            rating = entity.rating,
            title = entity.title,
            info = entity.info,
            contacts = entity.contacts,
            preview = entity.preview,
            captainId = entity.captain.userLoginParams.id.toString(),
            members = members,
            id = entity.id.toString()
        )
    }

    fun entityToCptResponse(entity: Team) : CptTeamResponse{
        val members = mutableSetOf<String>()
        for(member in entity.members) {
            members.add(member.userLoginParams.id.toString())
        }
        return CptTeamResponse(
            rating = entity.rating,
            title = entity.title,
            info = entity.info,
            contacts = entity.contacts,
            preview = entity.preview,
            code = entity.code,
            captainId = entity.captain.userLoginParams.id.toString(),
            members = members,
            id = entity.id.toString()
        )
    }

    fun requestToEntity(request: TeamCreationRequest) :  Team{
        var code = createCode()
        while (!teamRepository.findTeamByCode(code).isEmpty){
            code = createCode()
        }
        if (!teamRepository.findTeamByTitle(request.title).isEmpty) {
            throw AlreadyExistsException("team with title ${request.title}")
        }
        return Team(
            rating = 0,
            title = request.title,
            info = request.info,
            contacts = request.contacts,
            preview = request.preview,
            code = code,
        )
    }

    fun updateRequestToEntity(request: TeamUpdateRequest) : Team{
        if (!teamRepository.findTeamByTitle(request.title).isEmpty) {
            throw AlreadyExistsException("team with title ${request.title}")
        }
        val members = mutableSetOf<User>()
        for (m in request.members){
            members.add(userRepository.findUserByUserLoginParamsId(m.toLong()).orElseThrow { ResourceNotFoundException(m) })
        }
        val code = teamRepository.findTeamById(request.id.toLong()).orElseThrow { ResourceNotFoundException(request.id) }.code
        return Team(
            rating = request.rating,
            title = request.title,
            info = request.info,
            contacts = request.contacts,
            preview = request.preview,
            code = code
        ).apply {
            this.captain = userRepository.findUserByUserLoginParamsId(request.captainId.toLong()).orElseThrow{ ResourceNotFoundException(request.captainId) }
            this.members = members
        }
    }

    fun updateEntity(team: Team, newTeam: Team): Team {
        team.members =  newTeam.members
        team.info = newTeam.info
        team.captain = newTeam.captain
        team.contacts = newTeam.contacts
        team.preview = newTeam.preview
        team.rating = newTeam.rating
        team.title = newTeam.title
        return team
    }
}