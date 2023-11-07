package com.ctf.backend.controller

import com.ctf.backend.service.UserServiceImpl
import com.ctf.backend.util.API_VERSION_1
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$API_VERSION_1/user")
class UserController(
    private val userService: UserServiceImpl,
) {

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long) = userService.getUserById(id)

    @GetMapping("/me")
    fun getMyProfile() = userService.getMyProfile()
}