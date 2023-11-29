package com.ctf.backend.database.repo

import com.ctf.backend.database.entity.BlackList
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface BlackListDTO : AppRepository<BlackList> {

    fun existsByDeletedUserId(deletedUserId: Long): Boolean

    @Transactional
    @Modifying
    @Query("delete from BlackListTable where createdAt < (now() - interval '1 days')", nativeQuery = true)
    fun deleteExpiredDeletedUser()
}
