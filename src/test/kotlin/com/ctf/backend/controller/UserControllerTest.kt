package com.ctf.backend.controller

import com.ctf.backend.config.ControllerTestConfig
import com.ctf.backend.model.DataApiError
import com.ctf.backend.models.request.LoginRequest
import com.ctf.backend.models.request.RegistrationRequest
import com.ctf.backend.models.response.LoginResponse
import com.ctf.backend.models.response.UserDeleteResponse
import com.ctf.backend.models.response.UserRegistrationResponse
import com.ctf.backend.models.response.UserResponse
import com.ctf.backend.util.API_PUBLIC
import com.ctf.backend.util.API_VERSION_1
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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
class UserControllerTest {

    var testRestTemplate = TestRestTemplate()

    @LocalServerPort
    var serverPort: Int = 0

    private val headers = HttpHeaders()

    private fun applicationUrl() = "http://localhost:$serverPort"

    fun createUserAndSetAuthorisationHeader() {
        testRestTemplate.postForObject(
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
        val token = testRestTemplate.postForEntity(
            applicationUrl() + "$API_PUBLIC/auth/login",
            HttpEntity(
                LoginRequest(
                    email = "test@test.test",
                    password = "12345678",
                ),
            ),
            LoginResponse::class.java,
        )
        headers.clear()
        headers.add("Authorization", "Bearer ${token.body!!.accessJwt}")
    }

    @Test
    fun getProfileTest() {
        createUserAndSetAuthorisationHeader()

        val myProfile = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/user/me"),
            HttpMethod.GET,
            HttpEntity<HttpHeaders>(headers),
            UserResponse::class.java,
        )

        assertEquals(HttpStatus.OK, myProfile.statusCode)
        assertEquals("test_name", myProfile.body!!.name)

        val myProfileById = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/user/${myProfile.body!!.id}"),
            HttpMethod.GET,
            HttpEntity<HttpHeaders>(headers),
            UserResponse::class.java,
        )

        assertEquals(HttpStatus.OK, myProfileById.statusCode)
        assertEquals(myProfile.body, myProfileById.body)

        val allProfiles = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/user/all"),
            HttpMethod.GET,
            HttpEntity<HttpHeaders>(headers),
            List::class.java,
        )

        assertEquals(HttpStatus.OK, allProfiles.statusCode)
        assertNotNull((allProfiles.body!! as List<LinkedHashMap<String, Any>>).find { it["email"] == myProfile.body!!.email })
    }

    @Test
    fun deleteProfileTest() {
        createUserAndSetAuthorisationHeader()

        val result = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/user/me"),
            HttpMethod.DELETE,
            HttpEntity<HttpHeaders>(headers),
            UserDeleteResponse::class.java,
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val checkAccess = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/user/all"),
            HttpMethod.GET,
            HttpEntity<HttpHeaders>(headers),
            DataApiError::class.java,
        )

        assertEquals(HttpStatus.UNAUTHORIZED, checkAccess.statusCode)
        assertEquals("Your account has been deleted", checkAccess.body!!.debugMessage)
    }
}
