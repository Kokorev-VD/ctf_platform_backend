package com.ctf.backend.controller

import com.ctf.backend.models.request.AddUserRequest
import com.ctf.backend.models.request.TeamCreationRequest
import com.ctf.backend.models.request.TeamUpdateRequest
import com.ctf.backend.service.TeamService
import com.ctf.backend.util.API_VERSION_1
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("$API_VERSION_1/team")
class TeamController(
    private val teamService: TeamService,
) {

    @GetMapping("/cpt/{cptId}")
    fun getTeamsByCptId(@PathVariable("cptId") cptId: Long) = teamService.getTeamsByCptId(cptId)

    @GetMapping("/{id}")
    fun getTeamById(@PathVariable("id") id: Long) = teamService.getTeamById(id)

    @GetMapping("/member/{memberId}")
    fun getTeamsByMemberId(@PathVariable("memberId") memberId: Long) = teamService.getTeamsByMemberId(memberId)

    @GetMapping("/my/cpt")
    fun getMyCptTeams() = teamService.getMyCptTeams()

    @GetMapping("/my")
    fun getMyTeams() = teamService.getMyTeams()

    @GetMapping("/all")
    fun getAllTeams() = teamService.getAllTeams()

    @DeleteMapping("/{teamId}")
    fun cptDeleteTeam(@PathVariable("teamId") teamId: Long) = teamService.cptDeleteTeam(teamId)

    @PutMapping("")
    fun cptUpdateTeam(@RequestBody request: TeamUpdateRequest) = teamService.cptUpdateTeam(request)

    @DeleteMapping("/{teamId}/user/{userId}")
    fun cptDeleteUserFromTeam(@PathVariable("teamId") teamId: Long, @PathVariable("userId") userId: Long) = teamService.cptDeleteUserFromTeam(userId, teamId)

    @PostMapping("/{teamId}/user/{userId}")
    fun cptAddUserToTeam(@PathVariable("userId") userId: Long, @PathVariable("teamId") teamId: Long) = teamService.cptAddUserToTeam(userId, teamId)

    @PostMapping("/join")
    fun joinTeam(@RequestBody addUserRequest: AddUserRequest) = teamService.joinTeam(code = addUserRequest.code, teamId = addUserRequest.teamId)

    @PostMapping("")
    fun createTeam(@RequestBody teamCreationRequest: TeamCreationRequest) = teamService.createTeam(teamCreationRequest)

}