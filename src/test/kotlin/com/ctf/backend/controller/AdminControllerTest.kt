package com.ctf.backend.controller

import com.ctf.backend.config.ControllerTestConfig
import com.ctf.backend.database.repo.UserLoginParamsDao
import com.ctf.backend.model.DataApiError
import com.ctf.backend.models.request.LoginRequest
import com.ctf.backend.models.request.RegistrationRequest
import com.ctf.backend.models.request.TeamCreationRequest
import com.ctf.backend.models.response.CptTeamResponse
import com.ctf.backend.models.response.LoginResponse
import com.ctf.backend.models.response.TeamDeleteResponse
import com.ctf.backend.models.response.UserRegistrationResponse
import com.ctf.backend.util.API_ADMIN
import com.ctf.backend.util.API_PUBLIC
import com.ctf.backend.util.createCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.Commit
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
@ActiveProfiles(value = ["test"])
class AdminControllerTest {

    var testRestTemplate = TestRestTemplate()

    @LocalServerPort
    var serverPort: Int = 0

    @Autowired
    private lateinit var userLPRepo: UserLoginParamsDao

    private val headers = HttpHeaders()

    private fun applicationUrl() = "http://localhost:$serverPort"

    private var adminId = -1L

    @Commit
    fun createAdminAndSetAuthorizationHeader() {
        testRestTemplate.exchange(
            URI(applicationUrl() + "$API_PUBLIC/auth/registration"),
            HttpMethod.POST,
            HttpEntity(
                RegistrationRequest(
                    email = "admin@test.test",
                    password = "12345678",
                    name = "admin_name",
                    surname = "admin_surname",
                ),
            ),
            UserRegistrationResponse::class.java,
        )
        val adminLP = userLPRepo.findByEmail("admin@test.test").orElseThrow()
        adminId = adminLP.id
        adminLP.admin = true
        println(userLPRepo.save(adminLP).admin)
        val token = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_PUBLIC/auth/login"),
            HttpMethod.POST,
            HttpEntity(
                LoginRequest(
                    email = "admin@test.test",
                    password = "12345678",
                ),
            ),
            LoginResponse::class.java,
        ).body!!.accessJwt
        headers.clear()
        headers.add("Authorization", "Bearer $token")
    }

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
    fun accessTest() {
        createAdminAndSetAuthorizationHeader()

        val teamTitle = "test_team_title_${createCode()}"

        val adminResult = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_ADMIN/cpt/$adminId"),
            HttpMethod.POST,
            HttpEntity(
                TeamCreationRequest(
                    title = teamTitle,
                    info = "test_info",
                    contacts = "test_contacts",
                    preview = "test_preview",
                ),
                headers,
            ),
            CptTeamResponse::class.java,
        )

        assertEquals(HttpStatus.OK, adminResult.statusCode)

        createUserAndSetAuthorisationHeader()

        val userResult = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_ADMIN/cpt/$adminId"),
            HttpMethod.POST,
            HttpEntity(
                TeamCreationRequest(
                    title = teamTitle,
                    info = "test_info",
                    contacts = "test_contacts",
                    preview = "test_preview",
                ),
                headers,
            ),
            DataApiError::class.java,
        )
        assertEquals(HttpStatus.FORBIDDEN, userResult.statusCode)
        createAdminAndSetAuthorizationHeader()
        testRestTemplate.exchange(
            URI(applicationUrl() + "$API_ADMIN/team/${adminResult.body!!.id}"),
            HttpMethod.DELETE,
            HttpEntity<HttpHeaders>(headers),
            TeamDeleteResponse::class.java,
        )
    }
}
