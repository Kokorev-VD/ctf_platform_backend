package com.ctf.backend.controller

import com.ctf.backend.config.ControllerTestConfig
import com.ctf.backend.database.repo.UserLoginParamsDao
import com.ctf.backend.errors.AlreadyExistsException
import com.ctf.backend.errors.AlreadyInTeamException
import com.ctf.backend.model.DataApiError
import com.ctf.backend.models.request.LoginRequest
import com.ctf.backend.models.request.RegistrationRequest
import com.ctf.backend.models.request.TeamCreationRequest
import com.ctf.backend.models.request.TeamUpdateRequest
import com.ctf.backend.models.response.LoginResponse
import com.ctf.backend.models.response.TeamDeleteResponse
import com.ctf.backend.models.response.TeamResponse
import com.ctf.backend.models.response.UserRegistrationResponse
import com.ctf.backend.util.API_PUBLIC
import com.ctf.backend.util.API_VERSION_1
import com.ctf.backend.util.createCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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
class TeamControllerTest {

    var testRestTemplate = TestRestTemplate()

    @LocalServerPort
    var serverPort: Int = 0

    @Autowired private lateinit var userLPRepo: UserLoginParamsDao

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
    fun testCreationAndDeletion() {
        createUserAndSetAuthorisationHeader()

        val teamTitle = "test_team_title_${createCode()}"

        val id = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/team"),
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
            TeamResponse::class.java,
        ).body!!.id

        var teams = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/team/all"),
            HttpMethod.GET,
            HttpEntity<HttpHeaders>(headers),
            List::class.java,
        )
        var flag = false
        for (team in teams.body!!) {
            if ((team as LinkedHashMap<String, Any>)["title"] == teamTitle) {
                flag = true
            }
        }
        assert(flag)

        val alreadyExistingTeamResult = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/team"),
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

        assertEquals(HttpStatus.BAD_REQUEST, alreadyExistingTeamResult.statusCode)

        testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/team/$id"),
            HttpMethod.DELETE,
            HttpEntity<HttpHeaders>(headers),
            TeamDeleteResponse::class.java,
        )

        teams = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/team/all"),
            HttpMethod.GET,
            HttpEntity<HttpHeaders>(headers),
            List::class.java,
        )
        flag = false
        for (team in teams.body!!) {
            if ((team as LinkedHashMap<String, Any>)["title"] == teamTitle) {
                flag = true
            }
        }
        assert(!flag)
    }

    @Test
    fun testAddUserToTeamAndDeleteUserFromTeam() {
        createUserAndSetAuthorisationHeader()

        testRestTemplate.postForObject(
            URI(applicationUrl() + "$API_PUBLIC/auth/registration"),
            HttpEntity(
                RegistrationRequest(
                    email = "test2@test.test",
                    password = "12345678",
                    name = "test_name",
                    surname = "test_surname",
                ),
            ),
            UserRegistrationResponse::class.java,
        )

        val userId = userLPRepo.findByEmail("test2@test.test").orElseThrow().id.toString()

        val teamTitle = "test_team_title_${createCode()}"

        val teamId = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/team"),
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
            TeamResponse::class.java,
        ).body!!.id

        var result = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/team/$teamId/user/$userId"),
            HttpMethod.POST,
            HttpEntity<HttpHeaders>(
                headers,
            ),
            TeamResponse::class.java,
        )
        assertEquals(HttpStatus.OK, result.statusCode)

        assert(result.body!!.members.contains(userId))

        val alreadyInTeamResult = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/team/$teamId/user/$userId"),
            HttpMethod.POST,
            HttpEntity<HttpHeaders>(
                headers,
            ),
            DataApiError::class.java,
        )
        assertEquals(HttpStatus.BAD_REQUEST, alreadyInTeamResult.statusCode)
        assertEquals(AlreadyInTeamException(userId = userId, teamId = teamId).debugMessage, alreadyInTeamResult.body!!.debugMessage)
        result = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/team/$teamId/user/$userId"),
            HttpMethod.DELETE,
            HttpEntity<HttpHeaders>(
                headers,
            ),
            TeamResponse::class.java,
        )
        assertEquals(HttpStatus.OK, result.statusCode)
        assert(!result.body!!.members.contains(userId))
        testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/team/$teamId"),
            HttpMethod.DELETE,
            HttpEntity<HttpHeaders>(headers),
            TeamDeleteResponse::class.java,
        )
    }

    @Test
    fun updateTest() {
        createUserAndSetAuthorisationHeader()

        val teamTitle = "test_team_title_${createCode()}"

        val team = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/team"),
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
            TeamResponse::class.java,
        ).body!!

        testRestTemplate.postForObject(
            URI(applicationUrl() + "$API_PUBLIC/auth/registration"),
            HttpEntity(
                RegistrationRequest(
                    email = "test2@test.test",
                    password = "12345678",
                    name = "test_name",
                    surname = "test_surname",
                ),
            ),
            UserRegistrationResponse::class.java,
        )

        val userId = userLPRepo.findByEmail("test2@test.test").orElseThrow().id.toString()

        testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/team"),
            HttpMethod.PUT,
            HttpEntity(
                TeamUpdateRequest(
                    id = team.id,
                    rating = 1,
                    title = "changed_$teamTitle",
                    info = "changed_test_info",
                    contacts = "changed_test_contacts",
                    preview = "changed_test_preview",
                    captainId = userId,
                    members = (team.members as MutableSet<String>).apply { this.add(userId) },
                ),
                headers,
            ),
            TeamResponse::class.java,
        )

        val newUserCptTeams = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/team/cpt/$userId"),
            HttpMethod.GET,
            HttpEntity<HttpHeaders>(
                headers,
            ),
            List::class.java,
        ).body!! as List<LinkedHashMap<String, Any>>

        assertNotNull(newUserCptTeams.find { it["id"] == team.id })

        val newTeams = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/team/${team.id}"),
            HttpMethod.GET,
            HttpEntity<HttpHeaders>(
                headers,
            ),
            TeamResponse::class.java,
        )

        assertEquals(newTeams.body!!.title, "changed_$teamTitle")

        val newTeam = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/team"),
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
            TeamResponse::class.java,
        ).body!!

        val badTitleResult = testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/team"),
            HttpMethod.PUT,
            HttpEntity(
                TeamUpdateRequest(
                    id = newTeam.id,
                    rating = 1,
                    title = "changed_$teamTitle",
                    info = "changed_test_info",
                    contacts = "changed_test_contacts",
                    preview = "changed_test_preview",
                    captainId = newTeam.captainId,
                    members = (team.members as MutableSet<String>).apply { this.add(userId) },
                ),
                headers,
            ),
            DataApiError::class.java,
        )

        assertEquals(HttpStatus.BAD_REQUEST, badTitleResult.statusCode)
        assertEquals(AlreadyExistsException("team with title changed_$teamTitle").debugMessage, badTitleResult.body!!.debugMessage)

        testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/team/${newTeam.id}"),
            HttpMethod.DELETE,
            HttpEntity<HttpHeaders>(headers),
            TeamDeleteResponse::class.java,
        )
        val token = testRestTemplate.postForEntity(
            applicationUrl() + "$API_PUBLIC/auth/login",
            HttpEntity(
                LoginRequest(
                    email = "test2@test.test",
                    password = "12345678",
                ),
            ),
            LoginResponse::class.java,
        )
        headers.clear()
        headers.add("Authorization", "Bearer ${token.body!!.accessJwt}")

        testRestTemplate.exchange(
            URI(applicationUrl() + "$API_VERSION_1/team/${team.id}"),
            HttpMethod.DELETE,
            HttpEntity<HttpHeaders>(headers),
            TeamDeleteResponse::class.java,
        )
        headers.clear()
    }
}
