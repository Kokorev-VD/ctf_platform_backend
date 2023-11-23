package com.ctf.backend.service

import com.ctf.backend.models.request.TeamCreationRequest
import com.ctf.backend.models.request.TeamUpdateRequest
import com.ctf.backend.models.request.UserUpdateRequest
import com.ctf.backend.models.response.*

interface AdminService {

    fun addUserToTeam(userId: Long, teamId: Long) : TeamResponse

    fun deleteUserFromTeam(userId: Long, teamId: Long) : TeamResponse

    fun getTeam(teamId:Long) : CptTeamResponse

    fun deleteTeam(teamId: Long) : TeamDeleteResponse

    fun deleteUser(userId: Long) : UserDeleteResponse

    fun createTeam(request:TeamCreationRequest, userId: Long): CptTeamResponse

    fun updateUser(request:UserUpdateRequest) :  UserResponse

    fun updateTeam(request: TeamUpdateRequest) : CptTeamResponse

}