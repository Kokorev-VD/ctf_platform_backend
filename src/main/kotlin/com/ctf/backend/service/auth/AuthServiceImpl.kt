package com.ctf.backend.service.auth

import com.ctf.backend.database.entity.User
import com.ctf.backend.database.repo.UserLoginParamsDao
import com.ctf.backend.errors.AlreadyExistsException
import com.ctf.backend.errors.ApiError
import com.ctf.backend.errors.ResourceNotFoundException
import com.ctf.backend.mappers.UserLoginParamsMapper
import com.ctf.backend.models.request.LoginRequest
import com.ctf.backend.models.request.RegistrationRequest
import com.ctf.backend.models.response.LoginResponse
import com.ctf.backend.models.response.UserRegistrationResponse
import com.ctf.backend.service.UserServiceImpl
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.Modifying
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val dao: UserLoginParamsDao,
    private val jwtHelper: JwtHelper,
    private val mapper: UserLoginParamsMapper,
    private val encoder: PasswordEncoder,
    private val userService: UserServiceImpl,
) : AuthService {

    @Modifying
    @Transactional
    override fun registration(request: RegistrationRequest): UserRegistrationResponse {
        if (dao.existsByEmail(request.email)) {
            throw AlreadyExistsException(request.email)
        }
        val userLP = mapper.asEntity(request).apply {
            hash = encoder.encode(request.password)
            dao.save(this)
        }
        val user = userService.createUser(User(request.name, request.surname))
        user.userLoginParams = userLP
        return mapper.asRegistrationResponse(userLP).apply { this.accessJwt = login(LoginRequest(request.email, request.password)).accessJwt }
    }

    @Transactional
    override fun login(request: LoginRequest): LoginResponse {
        val user = dao.findByEmail(request.email).orElseThrow { ResourceNotFoundException(request.email) }
        if (!encoder.matches(request.password, user.hash)){
            throw ApiError(HttpStatus.UNAUTHORIZED, "Неправильный пароль")
        }
        return LoginResponse(jwtHelper.generateAccessToken(user))
    }
}