package com.ctf.backend.controller

import com.ctf.backend.models.request.TeamCreationRequest
import com.ctf.backend.models.request.TeamUpdateRequest
import com.ctf.backend.models.request.UserUpdateRequest
import com.ctf.backend.service.AdminService
import com.ctf.backend.util.API_VERSION_1
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$API_VERSION_1/admin")
class AdminController(
    private val adminService: AdminService,
) {

    @PostMapping("/team/{teamId}/user/{userId}")
    fun addUserToTeam(@PathVariable("userId") userId: Long, @PathVariable("teamId") teamId: Long) = adminService.addUserToTeam(userId, teamId)

    @DeleteMapping("/team/{teamId}/user/{userId}")
    fun deleteUserFromTeam(@PathVariable("userId") userId: Long, @PathVariable("teamId") teamId: Long) = adminService.deleteUserFromTeam(userId, teamId)

    @GetMapping("/team/{teamId}")
    fun getTeam(@PathVariable("teamId") teamId: Long) = adminService.getTeam(teamId)

    @DeleteMapping("/team/{teamId}")
    fun deleteTeam(@PathVariable("teamId") teamId: Long) = adminService.deleteTeam(teamId)

    @DeleteMapping("/user/{userId}")
    fun deleteUser(@PathVariable("userId") userId: Long) = adminService.deleteUser(userId)

    @PostMapping("/cpt/{userId}")
    fun createTeam(@RequestBody request: TeamCreationRequest, @PathVariable("userId") userId: Long) = adminService.createTeam(request, userId)

    @PutMapping("/user")
    fun updateUser(@RequestBody request: UserUpdateRequest) = adminService.updateUser(request)

    @PutMapping("/team")
    fun updateTeam(@RequestBody request: TeamUpdateRequest) = adminService.updateTeam(request)
}
