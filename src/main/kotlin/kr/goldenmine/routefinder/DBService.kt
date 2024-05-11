package kr.goldenmine.routefinder

import kr.goldenmine.routefinder.models.BusInfo
import kr.goldenmine.routefinder.models.BusStopStationInfo
import kr.goldenmine.routefinder.models.BusThroughInfo
import kr.goldenmine.routefinder.models.getBusStopStationInfoByResultSet
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.sql.Connection
import java.sql.DriverManager

@Service
class DBService {
    private val logger: Logger = LoggerFactory.getLogger(DBService::class.java)

    private val con: Connection

    init {
        Class.forName("com.mysql.cj.jdbc.Driver")

        val url =
            "jdbc:mysql://localhost:3306/bus_improvement?useSSL=false&useUnicode=true&serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true"
        val user = "dev"
        val password = "qqwwee11@@"

        con = DriverManager.getConnection(url, user, password)
    }

    fun getShortIdByStationName(stationName: String): String? {
        val sql = "SELECT short_id FROM bus_stop_station_info AS sta WHERE sta.name = ?"

        val shortId = con.prepareStatement(sql).use {
            it.setString(1, stationName)

            val rs = it.executeQuery()

            if(rs.next()) {
                rs.getString("short_id")
            } else {
                null
            }
        }

        return shortId
    }

    fun getAllStations(): List<BusStopStationInfo> {
        val sql = "SELECT * FROM bus_stop_station_info;"

        val rs = con.prepareStatement(sql).executeQuery()

        val list = ArrayList<BusStopStationInfo>()
        while (rs.next()) {
            val obj = getBusStopStationInfoByResultSet(rs)
            list.add(obj)
        }

        return list
    }

    fun getAllBusThroughInfo(): List<BusThroughInfo> {
        val sql = "SELECT * FROM bus_through_info;"

        val res = con.prepareStatement(sql).executeQuery()

        val list = ArrayList<BusThroughInfo>()
        while (res.next()) {
            val obj = BusThroughInfo(
                res.getInt("id"),
                res.getString("route_id"),
                res.getInt("bus_stop_station_id"),
                res.getInt("bus_stop_sequence"),
            )
            list.add(obj)
        }

        return list
    }

    fun getStationByShortId(id: Int): BusStopStationInfo? {
        val sql = "SELECT * FROM bus_stop_station_info WHERE bus_stop_station_info.short_id = ?;"

        return con.prepareStatement(sql).use {
            it.setInt(1, id)

            val rs = it.executeQuery()

            if(rs.next()) {
                getBusStopStationInfoByResultSet(rs)
            } else {
                null
            }
        }
    }

    fun getStationById(id: Int): BusStopStationInfo? {
        val sql = "SELECT * FROM bus_stop_station_info WHERE id = ?;"

        return con.prepareStatement(sql).use {
            it.setInt(1, id)

            val rs = it.executeQuery()

            if(rs.next()) {
                getBusStopStationInfoByResultSet(rs)
            } else {
                null
            }
        }
    }

    fun getAllThroughsWithStation(): List<Pair<BusStopStationInfo, BusThroughInfo>> {
        val sql = "SELECT * FROM bus_through_info INNER JOIN bus_stop_station_info ON bus_through_info.bus_stop_station_id = bus_stop_station_info.id;"

        val rs = con.prepareStatement(sql).executeQuery()

        val list = ArrayList<Pair<BusStopStationInfo, BusThroughInfo>>()
        while (rs.next()) {
            val obj = BusStopStationInfo(
                rs.getInt("bus_stop_station_info.id"),
                rs.getString("name"),
                rs.getDouble("pos_x"),
                rs.getDouble("pos_y"),
                rs.getInt("short_id"),
                rs.getString("admin_name"),
            )

            val obj2 = BusThroughInfo(
                rs.getInt("bus_through_info.id"),
                rs.getString("route_id"),
                rs.getInt("bus_stop_station_id"),
                rs.getInt("bus_stop_sequence"),
            )

            list.add(Pair(obj, obj2))
        }

        return list
    }

    fun getBusInfoById(routeId: String): BusInfo? {
        val sql = "SELECT * FROM bus_info WHERE route_id = ?;"

        val res = con.prepareStatement(sql).use {
            it.setString(1, routeId)
            val rs = it.executeQuery()
            if(rs.next()) {
                val busInfo = BusInfo(
                    rs.getString("route_id"),
                    rs.getInt("route_len"),
                    rs.getString("route_no"),
                    rs.getInt("origin_bus_stop_id"),
                    rs.getInt("dest_bus_stop_id"),
                    rs.getString("bus_start_time"),
                    rs.getString("bus_finish_time"),
                    rs.getInt("max_allocation_gap"),
                    rs.getInt("min_allocation_gap"),
                    rs.getInt("route_type"),
                    rs.getInt("turn_bus_stop_id"),
                )

                busInfo
            } else {
                null
            }
        }

        return res
    }
}