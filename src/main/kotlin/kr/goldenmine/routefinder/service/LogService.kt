package kr.goldenmine.routefinder.service

import kr.goldenmine.routefinder.model.BusStopStationInfo
import kr.goldenmine.routefinder.model.SearchLog
import kr.goldenmine.routefinder.model.User
import kr.goldenmine.routefinder.utils.GlobalConnection.Companion.connection
import org.springframework.stereotype.Service

@Service
class LogService {
    fun writeLog(user: User, from: String, fromId: Int, to: String, toId: Int) {
        val sql = "INSERT INTO search_log VALUES (?, ?, ?, ?, ?, ?)"
        connection.prepareStatement(sql).use {
            it.setInt(1, 0)
            it.setString(2, from)
            it.setInt(3, fromId)
            it.setString(4, to)
            it.setInt(5, toId)
            it.setInt(6, user.id)

            it.executeUpdate()
        }
    }

    fun getLog(user: User): List<SearchLog> {
        val sql = "SELECT * FROM search_log WHERE user_id = ? ORDER BY id DESC LIMIT 5"
        return connection.prepareStatement(sql).use {
            it.setInt(1, user.id)

            val rs = it.executeQuery()

            val logs = mutableListOf<SearchLog>()

            while(rs.next()) {
                val searchLog = SearchLog(
                    rs.getInt("id"),
                    rs.getString("start"),
                    rs.getInt("start_id"),
                    rs.getString("end"),
                    rs.getInt("end_id"),
                    rs.getInt("user_id"),
                )

                logs.add(searchLog)
            }

            logs
        }
    }

    fun deleteLog(id: Int) {
        val sql = "DELETE FROM search_log WHERE id = ?"
        connection.prepareStatement(sql).use {
            it.setInt(1, id)
            it.executeUpdate()
        }
    }

    fun getLogAdmin(): List<SearchLog> {
        val sql = "SELECT * FROM search_log"

        return connection.prepareStatement(sql).use {
            val rs = it.executeQuery()

            val logs = mutableListOf<SearchLog>()

            while(rs.next()) {
                val searchLog = SearchLog(
                    rs.getInt("id"),
                    rs.getString("start"),
                    rs.getInt("start_id"),
                    rs.getString("end"),
                    rs.getInt("end_id"),
                    rs.getInt("user_id"),
                )

                logs.add(searchLog)
            }

            logs
        }
    }
}