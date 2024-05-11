package kr.goldenmine.routefinder.model

import jakarta.persistence.Column
import jakarta.persistence.Id

class User(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "account_id")
    val accountId: String,

    @Column(name = "password")
    val password: String,

    @Column(name = "nickname")
    val nickname: String,

    @Column(name = "is_admin")
    val isAdmin: Boolean,
) {
}