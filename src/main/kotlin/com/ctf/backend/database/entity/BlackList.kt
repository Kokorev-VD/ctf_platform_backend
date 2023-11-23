package com.ctf.backend.database.entity

import com.ctf.backend.database.entity.common.AbstractCreatedAtEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "BlackListTable")
class BlackList(
    @Column(name = "deletedUserId", nullable = false)
    val deletedUserId: Long,
): AbstractCreatedAtEntity()