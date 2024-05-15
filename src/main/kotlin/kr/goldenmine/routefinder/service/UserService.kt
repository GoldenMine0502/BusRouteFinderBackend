package kr.goldenmine.routefinder.service

import kr.goldenmine.dowayobackend.util.impl.ConflictException
import kr.goldenmine.routefinder.DefaultPasswordEncoder
import kr.goldenmine.routefinder.model.BusStopStationInfo
import kr.goldenmine.routefinder.model.User
import kr.goldenmine.routefinder.model.getUserFromResultSet
import kr.goldenmine.routefinder.request.UserRequest
import kr.goldenmine.routefinder.utils.GlobalConnection.Companion.connection
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class UserService(
    private val jwtService: JwtService,
    private val passwordEncoder: DefaultPasswordEncoder
) {
    fun getUserByAccountId(id: String): User? {
        val sql = "SELECT * FROM user WHERE account_id = ?"

        return connection.prepareStatement(sql).use {
            it.setString(1, id)

            val rs = it.executeQuery()

            if (rs.next()) {
                getUserFromResultSet(rs)
            } else {
                null
            }
        }
    }


    fun getUserById(id: Int): User? {
        val sql = "SELECT * FROM user WHERE id = ?"

        return connection.prepareStatement(sql).use {
            it.setInt(1, id)

            val rs = it.executeQuery()

            if (rs.next()) {
                getUserFromResultSet(rs)
            } else {
                null
            }
        }
    }

    fun getUserByNickname(nickname: String): User? {
        val sql = "SELECT * FROM user WHERE nickname = ?"

        return connection.prepareStatement(sql).use {
            it.setString(1, nickname)

            val rs = it.executeQuery()

            if (rs.next()) {
                getUserFromResultSet(rs)
            } else {
                null
            }
        }
    }

    fun getUserByTokenWithCheckingExpire(accessToken: String): User? {
        if (!accessToken.startsWith("Bearer ")) {
            throw BadCredentialsException("token is not started with Bearer")
        }

        val accessTokenWithoutBearer = accessToken.substring(7)
        if (jwtService.isTokenExpired(accessTokenWithoutBearer)) {
            throw BadCredentialsException("token is expired")
        }

        return getUserByAccountId(jwtService.extractUsername(accessTokenWithoutBearer))
    }

    fun register(user: User) {
        if (getUserByAccountId(user.accountId) != null) throw ConflictException("the account id from user already exists")
        if (getUserByNickname(user.nickname) != null) throw ConflictException("the nickname from user already exists")

        // 비밀번호 암호화
        user.password = passwordEncoder.encode(user.password)
        insertUser(user)
    }

    fun getUsers(): List<User> {
        val sql = "SELECT * FROM user"

        return connection.prepareStatement(sql).use {
            val rs = it.executeQuery()

            val list = ArrayList<User>()
            while (rs.next()) {
                list.add(getUserFromResultSet(rs))
            }

            list
        }
    }

    fun editUser(user: UserRequest) {
        val sql = "UPDATE user SET account_id = ?, password = ?, nickname = ?, is_admin = ? WHERE id = ?;"

        user.password = passwordEncoder.encode(user.password)

        connection.prepareStatement(sql).use {
            it.setString(1, user.accountId)
            it.setString(2, user.password)
            it.setString(3, user.nickname)
            it.setBoolean(4, user.isAdmin)
            it.setInt(5, user.id)

            it.executeUpdate()
        }
    }

    fun deleteUser(id: Int) {
        val sql = "DELETE FROM user WHERE id = ?;"

        connection.prepareStatement(sql).use {
            it.setInt(1, id)
            it.executeUpdate()
        }
    }

    fun insertUser(user: User) {
        val sql = "INSERT INTO user VALUES (?, ?, ?, ?, ?)"
        connection.prepareStatement(sql).use {
            it.setInt(1, user.id)
            it.setString(2, user.accountId)
            it.setString(3, user.password)
            it.setString(4, user.nickname)
            it.setBoolean(5, user.isAdmin)

            it.executeUpdate()
        }
    }


    fun addUser(user: UserRequest) {
        user.password = passwordEncoder.encode(user.password)

        val sql = "INSERT INTO user VALUES (?, ?, ?, ?, ?)"
        connection.prepareStatement(sql).use {
            it.setInt(1, user.id)
            it.setString(2, user.accountId)
            it.setString(3, user.password)
            it.setString(4, user.nickname)
            it.setBoolean(5, user.isAdmin)

            it.executeUpdate()
        }
    }

    fun processHeader(header: String): User {
        if (!header.startsWith("Bearer ")) {
            throw BadCredentialsException("token is not started with Bearer")
        }

        val accessTokenWithoutBearer = header.substring(7)
        if (jwtService.isTokenExpired(accessTokenWithoutBearer)) {
            throw BadCredentialsException("token is expired")
        }

        val user = getUserByAccountId(jwtService.extractUsername(accessTokenWithoutBearer))
            ?: throw BadCredentialsException("the user does not exist")

        return user
    }

    fun authenticate(id: String, password: String): User {
        val user = getUserByAccountId(id) ?: throw BadCredentialsException("The id is not registered. id: $id")

        if (!passwordEncoder.matches(password, user.password)) {
            throw BadCredentialsException("The password is not matched! id: $id")
        }

        return user
    }
}