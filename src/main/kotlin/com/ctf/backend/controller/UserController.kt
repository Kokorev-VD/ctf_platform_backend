package com.ctf.backend.controller

import com.ctf.backend.models.request.UserUpdateRequest
import com.ctf.backend.service.UserServiceImpl
import com.ctf.backend.util.API_VERSION_1
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("$API_VERSION_1/user")
class  UserController(
    private val userService: UserServiceImpl,
) {

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long) = userService.getUserById(id)

    @GetMapping("/me")
    fun getMyProfile() = userService.getMyProfile()

    @DeleteMapping("/me")
    fun deleteMyProfile() = userService.deleteMyProfile()

    @PutMapping("/me")
    fun updateMyProfile(@RequestBody request: UserUpdateRequest) = userService.updateMyProfile(request)

    @DeleteMapping("/leave/{teamId}")
    fun leaveFromTeam(@PathVariable("teamId") teamId: Long) = userService.leaveFromTeam(teamId)

    @GetMapping("/all")
    fun getAllUsers() = userService.getAllUsers()
}