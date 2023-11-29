package com.ctf.backend.controller

import com.ctf.backend.config.ControllerTestConfig
import com.ctf.backend.util.API_PUBLIC
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
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
class HelloControllerTest {

    var testRestTemplate = TestRestTemplate()

    @LocalServerPort
    var serverPort: Int = 0

    private fun applicationUrl() = "http://localhost:$serverPort"

    @Test
    fun helloWorldTest() {
        val result = testRestTemplate.getForEntity(
            URI(applicationUrl() + "$API_PUBLIC/hello"),
            String::class.java,
        )

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals("Hello, world!", result.body)
    }
}
