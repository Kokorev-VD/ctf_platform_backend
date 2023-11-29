package com.ctf.backend.controller

import com.ctf.backend.config.ControllerTestConfig
import com.ctf.backend.errors.AlreadyExistsException
import com.ctf.backend.errors.ResourceNotFoundException
import com.ctf.backend.model.DataApiError
import com.ctf.backend.models.request.LoginRequest
import com.ctf.backend.models.request.RegistrationRequest
import com.ctf.backend.models.response.LoginResponse
import com.ctf.backend.models.response.UserRegistrationResponse
import com.ctf.backend.util.API_PUBLIC
import com.ctf.backend.util.createCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import java.net.URI

@ExtendWith(SpringExtension::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [ControllerTestConfig::class],
)
@Transactional
@Rollback
@ActiveProfiles(value = ["test"])
class AuthControllerTest {

    var testRestTemplate = TestRestTemplate()

    @LocalServerPort
    var serverPort: Int = 0

    private fun applicationUrl() = "http://localhost:$serverPort"

    fun registerTestUser() {
        testRestTemplate.exchange(
            URI(applicationUrl() + "$API_PUBLIC/auth/registration"),
            HttpMethod.POST,
            HttpEntity(
                RegistrationRequest(
                    email = "test@test.test",
                    password = "12345678",
                    name = "test_name",
                    surname = "test_surname",
                ),
            ),
            UserRegistrationResponse::class.java,
        )
    }

    @Test
    fun registrationTest() {
        registerTestUser()

        val badEmailResult = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_PUBLIC/auth/registration"),
            HttpMethod.POST,
            HttpEntity(
                RegistrationRequest(
                    email = "test.test",
                    password = "12345678",
                    name = "test_name",
                    surname = "test_surname",
                ),
            ),
            DataApiError::class.java,
        )

        assertEquals(HttpStatus.BAD_REQUEST, badEmailResult.statusCode)
        assertEquals("BAD_REQUEST", badEmailResult.body!!.debugMessage)

        val alreadyExistsResult = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_PUBLIC/auth/registration"),
            HttpMethod.POST,
            HttpEntity(
                RegistrationRequest(
                    email = "test@test.test",
                    password = "12345678",
                    name = "test_name",
                    surname = "test_surname",
                ),
            ),
            DataApiError::class.java,
        )

        assertEquals(HttpStatus.BAD_REQUEST, badEmailResult.statusCode)
        assertEquals(AlreadyExistsException("test@test.test").debugMessage, alreadyExistsResult.body!!.debugMessage)
    }

    @Test
    fun loginTest() {
        registerTestUser()

        val result = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_PUBLIC/auth/login"),
            HttpMethod.POST,
            HttpEntity(
                LoginRequest(
                    email = "test@test.test",
                    password = "12345678",
                ),
            ),
            LoginResponse::class.java,
        )

        assertEquals(HttpStatus.OK, result.statusCode)
    }

    @Test
    fun notRegisteredUserTest() {
        registerTestUser()

        val notRegisteredEmail = "${createCode()}@test.test"
        val emailNotFoundResult = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_PUBLIC/auth/login"),
            HttpMethod.POST,
            HttpEntity(
                LoginRequest(
                    email = notRegisteredEmail,
                    password = "12345678",
                ),
            ),
            DataApiError::class.java,
        )

        assertEquals(HttpStatus.NOT_FOUND, emailNotFoundResult.statusCode)
        assertEquals(
            ResourceNotFoundException(notRegisteredEmail).debugMessage,
            emailNotFoundResult.body!!.debugMessage,
        )
    }
}
