package com.ctf.backend.config

import com.ctf.backend.BackendApplication
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class ControllerTestConfig {
    @Bean
    fun app(): BackendApplication = mockk()
}
