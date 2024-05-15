package kr.goldenmine.routefinder.utils

import java.sql.Connection
import java.sql.DriverManager

class GlobalConnection {
    companion object {
        val connection: Connection

        init {
            Class.forName("com.mysql.cj.jdbc.Driver")

            val url =
                "jdbc:mysql://localhost:3306/bus_improvement?useSSL=false&useUnicode=true&serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true"
            val user = "202200922user"
            val password = "202200922pw"

            connection = DriverManager.getConnection(url, user, password)
        }
    }
}