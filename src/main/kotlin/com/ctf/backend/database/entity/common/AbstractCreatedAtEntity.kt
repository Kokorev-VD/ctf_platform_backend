package com.ctf.backend.database.entity.common

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import java.time.LocalDateTime

@MappedSuperclass
abstract class AbstractCreatedAtEntity () : AbstractEntity() {
    @CreationTimestamp
    @Column(nullable = false)
    lateinit var createdAt: LocalDateTime
}