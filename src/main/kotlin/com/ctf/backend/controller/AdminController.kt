package com.ctf.backend.controller

import com.ctf.backend.service.AdminService
import com.ctf.backend.util.API_VERSION_1
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$API_VERSION_1/admin")
class AdminController(
    private val adminService: AdminService
) {

    @PostMapping("/add/user/{userId}/team/{teamId}")
    fun addUserToTeam(@PathVariable("userId") userId: Long, @PathVariable("teamId") teamId: Long) = adminService.addUserToTeam(userId, teamId)

    @DeleteMapping("/delete/user/{userId}/team/{teamId}")
    fun deleteUserFromTeam(@PathVariable("userId") userId: Long, @PathVariable("teamId") teamId: Long) = adminService.deleteUserFromTeam(userId, teamId)

    @GetMapping("/team/{teamId}")
    fun getTeam(@PathVariable("teamId") teamId: Long) = adminService.getTeam(teamId)

}