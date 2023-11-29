package com.ctf.backend.controller

import com.ctf.backend.config.ControllerTestConfig
import com.ctf.backend.models.request.LoginRequest
import com.ctf.backend.models.request.RegistrationRequest
import com.ctf.backend.models.response.LoginResponse
import com.ctf.backend.models.response.UserRegistrationResponse
import com.ctf.backend.util.API_PUBLIC
import com.ctf.backend.util.API_VERSION_1
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
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
class CheckControllerTest {

    var testRestTemplate = TestRestTemplate()

    @LocalServerPort
    var serverPort: Int = 0

    private fun applicationUrl() = "http://localhost:$serverPort"

    fun registerTestUser() {
        testRestTemplate.postForEntity(
            URI(applicationUrl() + "$API_PUBLIC/auth/registration"),
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
    fun checkAuthorisationTest() {
        registerTestUser()

        val token = testRestTemplate.postForEntity(
            URI(applicationUrl() + "$API_PUBLIC/auth/login"),
            HttpEntity(
                LoginRequest(
                    email = "test@test.test",
                    password = "12345678",
                ),
            ),
            LoginResponse::class.java,
        )
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer ${token.body!!.accessJwt}")
        val result = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/checkOut"),
            HttpMethod.GET,
            HttpEntity<HttpHeaders>(headers),
            Boolean::class.java,
        )
        assertEquals(HttpStatus.OK, result.statusCode)
        assert(result.body!!)
    }
}
