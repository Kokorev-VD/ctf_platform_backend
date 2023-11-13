package com.ctf.backend.mappers

import com.ctf.backend.database.entity.Team
import com.ctf.backend.models.request.TeamCreationRequest
import com.ctf.backend.models.response.CptTeamResponse
import com.ctf.backend.models.response.TeamResponse
import org.springframework.stereotype.Component

@Component
class TeamMapper {

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
        return Team(
            rating = 0,
            title = request.title,
            info = request.info,
            contacts = request.contacts,
            preview = request.preview,
            code = "0",
        )
    }
}