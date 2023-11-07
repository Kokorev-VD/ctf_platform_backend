package com.ctf.backend.database.repo

import com.ctf.backend.database.entity.Team
import com.ctf.backend.database.entity.User
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface TeamDao: AppRepository<Team> {

    fun findTeamsByCaptainId(captainId: Long): Set<Team>

    fun findTeamById(id: Long) : Optional<Team>

    @Query("select t from Team t join t.members u where u.id = :userId")
    fun findTeamsByUserId(@Param("userId") userId:Long) : Set<Team>

    fun findTeamByCode(code: String) : Optional<Team>

}