package com.ctf.backend.database.repo

import com.ctf.backend.database.entity.common.AbstractEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface AppRepository<T : AbstractEntity> : CrudRepository<T, Long>
