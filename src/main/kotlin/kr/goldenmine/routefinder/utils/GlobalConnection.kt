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
            val user = "dev"
            val password = "qqwwee11@@"

            connection = DriverManager.getConnection(url, user, password)
        }
    }
}