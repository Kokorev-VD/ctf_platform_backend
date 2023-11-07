package com.ctf.backend.mappers

import com.ctf.backend.database.entity.UserLoginParams
import com.ctf.backend.models.request.RegistrationRequest
import com.ctf.backend.models.response.UserRegistrationResponse
import org.springframework.stereotype.Component

@Component
class UserLoginParamsMapper {

    fun asEntity(request: RegistrationRequest): UserLoginParams {
        return UserLoginParams(
            email = request.email
        )
    }

    fun asRegistrationResponse(entity: UserLoginParams): UserRegistrationResponse {
        return UserRegistrationResponse(
        )
    }
}