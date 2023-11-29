package com.ctf.backend.service

import com.ctf.backend.database.entity.User
import com.ctf.backend.database.entity.UserLoginParams
import com.ctf.backend.database.repo.BlackListDTO
import com.ctf.backend.database.repo.UserDao
import com.ctf.backend.database.repo.UserLoginParamsDao
import com.ctf.backend.errors.ApiError
import com.ctf.backend.errors.ResourceNotFoundException
import com.ctf.backend.models.request.UserUpdateRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@Rollback
@Transactional
@SpringBootTest
class UserServiceTest {

    @Autowired private lateinit var userService: UserService

    @Autowired private lateinit var userRepo: UserDao

    @Autowired private lateinit var userLPRepo: UserLoginParamsDao

    @Autowired private lateinit var blackListRepo: BlackListDTO

    @Test
    fun checkCreation() {
        val userLP = userLPRepo.save(UserLoginParams(email = "test@test.test").apply { hash = "test_hash"; admin = false })
        val res = userService.createUser(
            User(
                name = "test_name",
                surname = "test_surname",
                rating = 0,
            ).apply {
                userLoginParams = userLP
            },
        )

        assertEquals(userRepo.findUserById(res.id).orElseThrow().name, res.name)
        assertEquals(userRepo.findUserById(res.id).orElseThrow().surname, res.surname) // exists
    }

    @Test
    fun checkDeletion() {
        assertThrows<ResourceNotFoundException> { userService.deleteProfile(-1) } // incorrect id

        val userLP = userLPRepo.save(UserLoginParams(email = "test@test.test").apply { hash = "test_hash"; admin = false })

        val res = userService.createUser(
            User(
                name = "test_name",
                surname = "test_surname",
                rating = 0,
            ).apply {
                userLoginParams = userLP
            },
        )

        userService.deleteProfile(res.id)

        assertThrows<ResourceNotFoundException> { userService.getUserById(res.id) } // deleted
        assert(blackListRepo.existsByDeletedUserId(res.id))
    }

    @Test
    fun checkUpdate() {
        val userLP = userLPRepo.save(UserLoginParams(email = "test@test.test").apply { hash = "test_hash"; admin = false })

        val user = userService.createUser(
            User(
                name = "test_name",
                surname = "test_surname",
                rating = 0,
            ).apply { userLoginParams = userLP },
        )

        val badIdUserUpdateRequest = UserUpdateRequest(
            id = "-1",
            name = "changed_test_name",
            surname = "changed_test_surname",
            rating = 1,
        )

        assertThrows<ApiError> { userService.updateUser(badIdUserUpdateRequest) } // checks is id valid

        val userUpdateRequest = UserUpdateRequest(
            id = user.id.toString(),
            name = "changed_test_name",
            surname = "changed_test_surname",
            rating = 1,
        )

        println(user.id)
        val newUser = userService.updateUser(userUpdateRequest)
        val dbNewUser = userRepo.findUserById(newUser.id.toLong()).orElseThrow { ResourceNotFoundException("user ${user.id}") }
        assertEquals(newUser.name, dbNewUser.name)
        assertEquals(newUser.surname, dbNewUser.surname)
        assertEquals(newUser.rating, dbNewUser.rating)
    }
}
