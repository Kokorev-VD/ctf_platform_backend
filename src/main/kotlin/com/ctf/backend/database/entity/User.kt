package com.ctf.backend.database.entity

import com.ctf.backend.database.entity.common.AbstractCreatedAtEntity
import jakarta.persistence.*

@Entity
@Table(name = "UserTable")
class User(

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "surname", nullable = false)
    var surname: String,

    @Column(name = "admin", nullable = false)
    var admin: Boolean = false,

    @Column(name = "rating", nullable = false)
    var rating: Long = 0,

) : AbstractCreatedAtEntity(
){

    @OneToOne(fetch = FetchType.LAZY)
    lateinit var userLoginParams: UserLoginParams

    @OneToMany(mappedBy = "captain")
    var cptTeams : Set<Team> = HashSet<Team>()


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "UserToTeamTable",
        joinColumns = [JoinColumn(name = "memberId", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "teamId", referencedColumnName = "id")],
    )
    var team : Set<Team> = HashSet<Team>()

    override var id : Long = 0
}