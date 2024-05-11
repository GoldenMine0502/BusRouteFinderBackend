package kr.goldenmine.routefinder.model

import jakarta.persistence.Column
import jakarta.persistence.Id
import java.sql.ResultSet

class User(
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "account_id")
    val accountId: String,

    @Column(name = "password")
    var password: String,

    @Column(name = "nickname")
    val nickname: String,

    @Column(name = "is_admin")
    val isAdmin: Boolean,
) {
}

fun getUserFromResultSet(rs: ResultSet): User {
    return User(
        rs.getInt("id"),
        rs.getString("account_id"),
        rs.getString("password"),
        rs.getString("nickname"),
        rs.getBoolean("is_admin")
    )
}