package com.ctf.backend.service

import com.ctf.backend.database.entity.Team
import com.ctf.backend.models.request.TeamCreationRequest
import com.ctf.backend.models.response.CptTeamResponse
import com.ctf.backend.models.response.TeamResponse

interface TeamService {

    fun getTeamsByCptId(cptId: Long) : Set<TeamResponse>

    fun getTeamById(id: Long) : TeamResponse

    fun getTeamsByMemberId(memberId: Long) : Set<TeamResponse>

    fun addUserToTeam(teamId: String, code: String) : TeamResponse

    fun cptAddUserToTeam(userId: Long, teamId: Long) : TeamResponse

    fun cptDeleteUserFromTeam(userId: Long, teamId: Long) : TeamResponse

    fun check(teamId:Long):Unit

    fun createTeam(request: TeamCreationRequest) : TeamResponse

    fun getMyCptTeams() : Set<CptTeamResponse>

    fun getAllTeams() : Set<TeamResponse>
}
