package com.ctf.backend.service

import com.ctf.backend.models.request.TeamCreationRequest
import com.ctf.backend.models.request.TeamUpdateRequest
import com.ctf.backend.models.response.CptTeamResponse
import com.ctf.backend.models.response.TeamDeleteResponse
import com.ctf.backend.models.response.TeamResponse

interface TeamService {

    fun getTeamsByCptId(cptId: Long) : List<TeamResponse>

    fun getTeamById(id: Long) : TeamResponse

    fun getTeamsByMemberId(memberId: Long) : List<TeamResponse>

    fun getMyCptTeams() : List<CptTeamResponse>

    fun getMyTeams() : List<CptTeamResponse>

    fun getAllTeams() : List<TeamResponse>

    fun cptDeleteTeam(teamId: Long) : TeamDeleteResponse

    fun cptUpdateTeam(request: TeamUpdateRequest) : CptTeamResponse

    fun cptDeleteUserFromTeam(userId: Long, teamId: Long) : TeamResponse

    fun cptAddUserToTeam(userId: Long, teamId: Long) : CptTeamResponse

    fun joinTeam(teamId: String, code: String) : TeamResponse

    fun createTeam(request: TeamCreationRequest) : TeamResponse

    /*
      Realisations
    */
    fun addUserToTeam(userId: Long, teamId: Long) : CptTeamResponse

    fun deleteUserFromTeam(userId: Long, teamId: Long) : TeamResponse

    fun check(teamId:Long)

    fun deleteTeam(teamId: Long) : TeamDeleteResponse

    fun updateTeam(request: TeamUpdateRequest) : CptTeamResponse
}
