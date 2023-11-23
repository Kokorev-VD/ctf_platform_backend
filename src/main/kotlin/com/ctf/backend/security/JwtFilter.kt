package com.ctf.backend.security

import com.ctf.backend.database.repo.BlackListDTO
import com.ctf.backend.errors.ApiError
import com.ctf.backend.errors.DeletedUserException
import com.ctf.backend.errors.ExceptionResolver
import com.ctf.backend.errors.NotEnoughAccessRightsException
import com.ctf.backend.security.model.Authority
import com.ctf.backend.util.*
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    @Lazy
    private val jwtParser: JwtParser,
    private val exceptionResolver: ExceptionResolver,
    private val blackListRepository: BlackListDTO,
): OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest) : Boolean {
        return request.requestURI.containsAnyPath(
            API_PUBLIC,
        )
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val header = request.getHeader("Authorization")
                ?:throw ApiError(HttpStatus.UNAUTHORIZED, "Вы неавторизованы", "Authorization header not found")
            SecurityContextHolder.getContext().authentication = jwtParser.createAuthToken(header)
            if (blackListRepository.existsByDeletedUserId(getPrincipal())){
                blackListRepository.deleteExpiredDeletedUser()
                throw DeletedUserException()
            }
            if (request.requestURI.containsAnyPath((API_ADMIN))){
                val authorities = getAuthorities()
                var admin = false
                for (x in authorities){
                    if(x.authority == Authority.ADMIN.authority.authority){
                        admin = true
                        break
                    }
                }
                if (!admin){
                    throw NotEnoughAccessRightsException()
                }
            }
            filterChain.doFilter(request, response)
        } catch (exception: ApiError) {
            exceptionResolver.resolveException(request, response, exception)
            return
        }
    }

}