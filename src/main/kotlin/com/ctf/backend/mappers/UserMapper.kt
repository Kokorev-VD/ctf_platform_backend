package com.ctf.backend.mappers

import com.ctf.backend.database.entity.User
import com.ctf.backend.database.repo.TeamDao
import com.ctf.backend.models.request.UserUpdateRequest
import com.ctf.backend.models.response.UserResponse
import org.springframework.stereotype.Component

@Component
class UserMapper(
    val teamRepository: TeamDao,
) {

    fun asUserResponse(entity: User): UserResponse {
        val teams: MutableSet<String> = mutableSetOf()
        val cptTeams: MutableSet<String> = mutableSetOf()
        for (x in entity.team) {
            teams.add(x.id.toString())
        }
        for (x in entity.cptTeams) {
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
            admin = entity.userLoginParams.admin,
        )
    }

    fun asEntity(request: UserUpdateRequest): User {
        return User(
            name = request.name,
            surname = request.surname,
            rating = request.rating,
        ).apply {
            this.id = request.id.toLong()
        }
    }
    fun updateEntity(user: User, newUser: User): User {
        user.name = newUser.name
        user.surname = newUser.surname
        user.rating = newUser.rating
        return user
    }
}
