package com.ctf.backend.database.entity

import com.ctf.backend.database.entity.common.AbstractCreatedAtEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "UserLoginParamsTable")
class UserLoginParams(
    @Column(name = "email", nullable = false, unique = true)
    var email: String,

    @Column(name = "admin", nullable = false)
    var admin: Boolean = false,

) : AbstractCreatedAtEntity() {

    @Column(name = "hash", nullable = false)
    var hash: String? = null

    @OneToOne(mappedBy = "userLoginParams", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    lateinit var user: User
}
