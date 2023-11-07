package com.ctf.backend.service

import com.ctf.backend.models.response.TeamResponse

interface AdminService {

    fun addUserToTeam(userId: Long, teamId: Long) : TeamResponse

    fun deleteUserFromTeam(userId: Long, teamId: Long) : TeamResponse

}