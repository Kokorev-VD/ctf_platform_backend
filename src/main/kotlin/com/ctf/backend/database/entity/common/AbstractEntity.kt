package com.ctf.backend.database.entity.common

import jakarta.persistence.*
import java.io.Serializable

@MappedSuperclass
abstract class AbstractEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    open var id: Long = 0,
) : Serializable