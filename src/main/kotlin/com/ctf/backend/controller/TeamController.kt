package com.ctf.backend.controller

import com.ctf.backend.models.request.AddUserRequest
import com.ctf.backend.models.request.TeamCreationRequest
import com.ctf.backend.models.response.TeamResponse
import com.ctf.backend.service.TeamService
import com.ctf.backend.util.API_VERSION_1
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("$API_VERSION_1/team")
class TeamController(
    private val teamService: TeamService,
) {

    @GetMapping("/cpt/{cptId}")
    fun getTeamsByCptId(@PathVariable("cptId") cptId: Long): Set<TeamResponse> = teamService.getTeamsByCptId(cptId)

    @GetMapping("/all")
    fun getAllTeams() = teamService.getAllTeams()

    @GetMapping("/{id}")
    fun getTeamById(@PathVariable("id") id: Long) = teamService.getTeamById(id)

    @GetMapping("/member/{memberId}")
    fun getTeamsByMemberId(@PathVariable("memberId") memberId: Long) = teamService.getTeamsByMemberId(memberId)

    @GetMapping("/my")
    fun getMyCptTeams() = teamService.getMyCptTeams()

    @PostMapping("/add")
    fun addUserToTeam(@RequestBody addUserRequest: AddUserRequest) = teamService.addUserToTeam(addUserRequest.code)

    @PostMapping("/new")
    fun createTeam(@RequestBody teamCreationRequest: TeamCreationRequest) = teamService.createTeam(teamCreationRequest)

    @PostMapping("/add/user/{userId}/team/{teamId}")
    fun cptAddUserToTeam(@PathVariable("userId") userId: Long, @PathVariable("teamId") teamId: Long) = teamService.cptAddUserToTeam(userId, teamId)

    @DeleteMapping("/delete/user/{userId}/team/{teamId}")
    fun cptDeleteUserFromTeam(@PathVariable("userId") userId: Long, @PathVariable("teamId") teamId: Long) = teamService.cptDeleteUserFromTeam(userId, teamId)


}