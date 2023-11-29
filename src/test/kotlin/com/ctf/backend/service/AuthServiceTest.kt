package com.ctf.backend.service

import com.ctf.backend.errors.AlreadyExistsException
import com.ctf.backend.models.request.RegistrationRequest
import com.ctf.backend.service.auth.AuthService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@Rollback
@Transactional
@SpringBootTest
class AuthServiceTest {

    @Autowired private lateinit var authService: AuthService

    @Test
    fun checkRegistration() {
        val goodRequest = RegistrationRequest(
            email = "test@test.test",
            password = "12345678",
            name = "test_name",
            surname = "test_surname",
        )
        authService.registration(goodRequest)
        val existingEmailRequest = RegistrationRequest(
            email = "test@test.test",
            password = "12345678",
            name = "changed_test_name",
            surname = "test_surname",
        )
        assertThrows<AlreadyExistsException> { authService.registration(existingEmailRequest) }
    }
}
