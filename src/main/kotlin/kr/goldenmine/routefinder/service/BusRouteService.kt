package kr.goldenmine.routefinder.service

import kr.goldenmine.routefinder.model.BusInfo
import kr.goldenmine.routefinder.model.BusStopStationInfo
import kr.goldenmine.routefinder.model.BusThroughInfo
import kr.goldenmine.routefinder.model.getBusStopStationInfoByResultSet
import kr.goldenmine.routefinder.utils.GlobalConnection.Companion.connection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BusRouteService {
    private val logger: Logger = LoggerFactory.getLogger(BusRouteService::class.java)


    fun getShortIdByStationName(stationName: String): String? {
        val sql = "SELECT short_id FROM bus_stop_station_info AS sta WHERE sta.name = ?"

        val shortId = connection.prepareStatement(sql).use {
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

        val rs = connection.prepareStatement(sql).executeQuery()

        val list = ArrayList<BusStopStationInfo>()
        while (rs.next()) {
            val obj = BusStopStationInfo(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("pos_x"),
                rs.getDouble("pos_y"),
                rs.getInt("short_id"),
                rs.getString("admin_name"),
            )
            list.add(obj)
        }

        return list
    }

    fun addStation(busInfo: BusStopStationInfo) {
        val sql = "INSERT INTO bus_stop_station_info VALUES(?, ?, ?, ?, ?, ?)"

        connection.prepareStatement(sql).use {
            it.setInt(1, busInfo.id)
            it.setString(2, busInfo.name)
            it.setDouble(3, busInfo.posX)
            it.setDouble(4, busInfo.posY)
            it.setInt(5, busInfo.shortId)
            it.setString(6, busInfo.adminName)

            it.executeUpdate()
        }
    }


    fun editStation(busInfo: BusStopStationInfo) {
        val sql = "UPDATE bus_stop_station_info SET name = ?, pos_x = ?, pos_y = ?, short_id = ?, admin_name = ? WHERE id = ?;"

        connection.prepareStatement(sql).use {
            it.setString(1, busInfo.name)
            it.setDouble(2, busInfo.posX)
            it.setDouble(3, busInfo.posY)
            it.setInt(4, busInfo.shortId)
            it.setString(5, busInfo.adminName)
            it.setInt(6, busInfo.id)

            it.executeUpdate()
        }
    }

    fun deleteStation(id: Int) {
        val sql = "DELETE FROM bus_stop_station_info WHERE id = ?;"

        connection.prepareStatement(sql).use {
            it.setInt(1, id)
            it.executeUpdate()
        }
    }

    fun getAllBusThroughInfo(): List<BusThroughInfo> {
        val sql = "SELECT * FROM bus_through_info;"

        val res = connection.prepareStatement(sql).executeQuery()

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

    fun addBusThrough(busInfo: BusThroughInfo) {
        val sql = "INSERT INTO bus_through_info VALUES(?, ?, ?, ?)"

        connection.prepareStatement(sql).use {
            it.setInt(1, busInfo.id)
            it.setString(2, busInfo.routeId)
            it.setInt(3, busInfo.busStopStationId)
            it.setInt(4, busInfo.busStopSequence)

            it.executeUpdate()
        }
    }


    fun editBusThrough(busInfo: BusThroughInfo) {
        val sql = "UPDATE bus_through_info SET route_id = ?, bus_stop_station_id = ?, bus_stop_sequence = ? WHERE id = ?;"

        connection.prepareStatement(sql).use {
            it.setString(1, busInfo.routeId)
            it.setInt(2, busInfo.busStopStationId)
            it.setInt(3, busInfo.busStopSequence)
            it.setInt(4, busInfo.id)

            it.executeUpdate()
        }
    }

    fun deleteBusThrough(id: Int) {
        val sql = "DELETE FROM bus_through_info WHERE id = ?;"

        connection.prepareStatement(sql).use {
            it.setInt(1, id)
            it.executeUpdate()
        }
    }

    fun getStationByShortId(id: Int): BusStopStationInfo? {
        val sql = "SELECT * FROM bus_stop_station_info WHERE bus_stop_station_info.short_id = ?;"

        return connection.prepareStatement(sql).use {
            it.setInt(1, id)

            val rs = it.executeQuery()

            if(rs.next()) {
                getBusStopStationInfoByResultSet(rs)
            } else {
                null
            }
        }
    }

    fun getBusRoutesByRouteNo(routeNo: String): List<Pair<BusStopStationInfo, BusThroughInfo>> {
        val sql = "SELECT bus_through_info.*, bus_stop_station_info.*\n" +
                "FROM bus_through_info \n" +
                "INNER JOIN bus_info \n" +
                "    ON bus_through_info.route_id = bus_info.route_id \n" +
                "INNER JOIN bus_stop_station_info \n" +
                "\tON bus_stop_station_info.id = bus_through_info.bus_stop_station_id\n" +
                "WHERE bus_info.route_no = ?\n" +
                "ORDER BY bus_stop_sequence ASC; "

        return connection.prepareStatement(sql).use {
            it.setString(1, routeNo)

            val rs = it.executeQuery()
            val list = ArrayList<Pair<BusStopStationInfo, BusThroughInfo>>()
            while (rs.next()) {
                val obj = BusStopStationInfo(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("pos_x"),
                    rs.getDouble("pos_y"),
                    rs.getInt("short_id"),
                    rs.getString("admin_name"),
                )
                val obj2 = BusThroughInfo(
                    rs.getInt("bus_through_info.id"),
                    rs.getString("bus_through_info.route_id"),
                    rs.getInt("bus_through_info.bus_stop_station_id"),
                    rs.getInt("bus_through_info.bus_stop_sequence"),
                )

                list.add(obj to obj2)
            }
            list
        }
    }

    fun getStationById(id: Int): BusStopStationInfo? {
        val sql = "SELECT * FROM bus_stop_station_info WHERE id = ?;"

        return connection.prepareStatement(sql).use {
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

        val rs = connection.prepareStatement(sql).executeQuery()

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

        val res = connection.prepareStatement(sql).use {
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

    fun getAllBus(): List<BusInfo> {
        val sql = "SELECT * FROM bus_info;"

        return connection.prepareStatement(sql).use {
            val rs = it.executeQuery()

            val busInfoList = ArrayList<BusInfo>()
            while(rs.next()) {
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

                busInfoList.add(busInfo)
            }

            busInfoList
        }
    }

    fun addBus(busInfo: BusInfo) {
        val sql = "INSERT INTO bus_info VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"

        connection.prepareStatement(sql).use {
            it.setString(1, busInfo.routeId)
            it.setInt(2, busInfo.routeLen)
            it.setString(3, busInfo.routeNo)
            it.setInt(4, busInfo.originBusStopId)
            it.setInt(5, busInfo.destBusStopId)
            it.setString(6, busInfo.busStartTime)
            it.setString(7, busInfo.busFinishTime)
            it.setInt(8, busInfo.maxAllocationGap)
            it.setInt(9, busInfo.minAllocationGap)
            it.setInt(10, busInfo.routeType)
            it.setInt(11, busInfo.turnBusStopId)

            it.executeUpdate()
        }
    }

    fun deleteBus(id: Int) {
        val sql = "DELETE FROM bus_info WHERE route_id = ?;"

        connection.prepareStatement(sql).use {
            it.setInt(1, id)
            it.executeUpdate()
        }
    }

    fun editBus(busInfo: BusInfo) {
        val sql = "UPDATE bus_info SET route_len = ?, route_no = ?, origin_bus_stop_id = ?, dest_bus_stop_id = ?, bus_start_time = ?, bus_finish_time = ?, max_allocation_gap = ?, min_allocation_gap = ?, route_type = ?, turn_bus_stop_id = ? WHERE route_id = ?;"

        connection.prepareStatement(sql).use {
            it.setInt(1, busInfo.routeLen)
            it.setString(2, busInfo.routeNo)
            it.setInt(3, busInfo.originBusStopId)
            it.setInt(4, busInfo.destBusStopId)
            it.setString(5, busInfo.busStartTime)
            it.setString(6, busInfo.busFinishTime)
            it.setInt(7, busInfo.maxAllocationGap)
            it.setInt(8, busInfo.minAllocationGap)
            it.setInt(9, busInfo.routeType)
            it.setInt(10, busInfo.turnBusStopId)
            it.setString(11, busInfo.routeId)
            it.executeUpdate()
        }
    }

    fun searchStation(keyword: String): List<BusStopStationInfo> {
        val sql = "SELECT * FROM bus_stop_station_info WHERE name LIKE ? LIMIT 10"

        val list = mutableListOf<BusStopStationInfo>()

        connection.prepareStatement(sql).use {
            it.setString(1, "%${keyword}%")

            val rs = it.executeQuery()
            while(rs.next()) {
                list.add(BusStopStationInfo(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("pos_x"),
                    rs.getDouble("pos_y"),
                    rs.getInt("short_id"),
                    rs.getString("admin_name"),
                ))
            }
        }

        return list
    }
}