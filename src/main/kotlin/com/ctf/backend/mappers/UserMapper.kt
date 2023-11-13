package com.ctf.backend.mappers

import com.ctf.backend.database.entity.User
import com.ctf.backend.models.response.UserResponse
import org.springframework.stereotype.Component

@Component
class UserMapper {

    fun asUserResponse(entity: User) : UserResponse {
        val teams: MutableSet<Long> = mutableSetOf()
        val cptTeams : MutableSet<Long> = mutableSetOf()
        for(x in entity.team){
            teams.add(x.id)
        }
        for(x in entity.cptTeams){
            cptTeams.add(x.id)
        }
        return UserResponse(
            name = entity.name,
            surname = entity.surname,
            rating = entity.rating,
            email = entity.userLoginParams.email,
            cptTeams = cptTeams,
            teams = teams,
            id = entity.userLoginParams.id.toString(),
            isAdmin = entity.admin,
        )
    }
}