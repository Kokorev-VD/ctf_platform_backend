package com.ctf.backend.database.entity

import com.ctf.backend.database.entity.common.AbstractCreatedAtEntity
import jakarta.persistence.*

@Entity
@Table(name = "UserTable")
class User(

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "surname", nullable = false)
    val surname: String,

    @Column(name = "admin", nullable = false)
    val admin: Boolean = false,

    @Column(name = "rating", nullable = false)
    var rating: Long = 0,

) : AbstractCreatedAtEntity(){

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    lateinit var userLoginParams: UserLoginParams

    @OneToMany(mappedBy = "captain")
    var cptTeams : Set<Team> = HashSet<Team>()


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "UserToTeamTable",
        joinColumns = [JoinColumn(name = "memberId", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "teamId", referencedColumnName = "id")],
    )
    var team : Set<Team> = HashSet<Team>()

}