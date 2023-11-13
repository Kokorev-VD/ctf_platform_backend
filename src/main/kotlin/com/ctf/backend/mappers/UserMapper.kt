package com.ctf.backend.mappers

import com.ctf.backend.database.entity.Team
import com.ctf.backend.database.entity.User
import com.ctf.backend.database.repo.TeamDao
import com.ctf.backend.errors.ResourceNotFoundException
import com.ctf.backend.models.request.UserUpdateRequest
import com.ctf.backend.models.response.UserResponse
import org.springframework.stereotype.Component

@Component
class UserMapper(
    val teamRepository: TeamDao,
) {

    fun asUserResponse(entity: User) : UserResponse {
        val teams: MutableSet<String> = mutableSetOf()
        val cptTeams : MutableSet<String> = mutableSetOf()
        for(x in entity.team){
            teams.add(x.id.toString())
        }
        for(x in entity.cptTeams){
            cptTeams.add(x.id.toString())
        }
        return UserResponse(
            name = entity.name,
            surname = entity.surname,
            rating = entity.rating,
            email = entity.userLoginParams.email,
            cptTeams = cptTeams,
            teams = teams,
            id = entity.userLoginParams.id.toString(),
            admin = entity.admin,
        )
    }

    fun asEntity(request: UserUpdateRequest) : User{
        val team = mutableSetOf<Team>()
        val cptTeam = mutableSetOf<Team>()
        for(t in request.teams){
            team.add(teamRepository.findTeamById(t.toLong()).orElseThrow { ResourceNotFoundException(t) })
        }
        for(t in request.cptTeams){
            cptTeam.add(teamRepository.findTeamById(t.toLong()).orElseThrow { ResourceNotFoundException(t) })
        }
        return User(
            name = request.name,
            surname = request.surname,
            rating = request.rating,
        ).apply {
            this.team = team
            this.cptTeams = cptTeam
        }
    }
    fun updateEntity(user:User, newUser: User) : User{
        user.team = newUser.team
        user.cptTeams = newUser.cptTeams
        for(t in user.cptTeams) {
            t.captain = user
            teamRepository.save(t)
        }
        user.name = newUser.name
        user.admin = newUser.admin
        user.surname = newUser.surname
        user.rating = newUser.rating
        return user
    }
}