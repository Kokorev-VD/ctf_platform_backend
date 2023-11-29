package com.ctf.backend.database.entity

import com.ctf.backend.database.entity.common.AbstractCreatedAtEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "TeamTable")
class Team(

    @Column(name = "rating", nullable = false)
    var rating: Long = 0,

    @Column(name = "title", nullable = false, unique = true)
    var title: String,

    @Column(name = "info", nullable = false)
    var info: String = "default",

    @Column(name = "contacts", nullable = false)
    var contacts: String = "default",

    @Column(name = "preview", nullable = false)
    var preview: String = "default",

    @Column(name = "code", unique = true, nullable = false)
    var code: String,
) : AbstractCreatedAtEntity() {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "captainId")
    lateinit var captain: User

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "UserToTeamTable",
        joinColumns = [JoinColumn(name = "teamId", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "memberId", referencedColumnName = "id")],
    )
    var members: Set<User> = HashSet()
}
