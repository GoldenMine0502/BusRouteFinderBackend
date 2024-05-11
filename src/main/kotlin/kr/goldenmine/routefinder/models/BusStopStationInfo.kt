package kr.goldenmine.routefinder.models

import com.google.gson.annotations.SerializedName
import jakarta.persistence.*
import java.sql.ResultSet

//@Data
class BusStopStationInfo(
    /*
	id VARCHAR(20) NOT NULL PRIMARY KEY,
    name VARCHAR(40) NOT NULL,
	pos_x FLOAT,
    pos_y FLOAT,
    short_id INT(11),
    admin_name VARCHAR(20)
     */
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "name")
    val name: String,

    @Column(name = "pos_x")
    val posX: Double,

    @Column(name = "pos_y")
    val posY: Double,

    @Column(name = "short_id")
    val shortId: Int,

    @Column(name = "admin_name")
    val adminName: String,
) {

}

fun getBusStopStationInfoByResultSet(res: ResultSet): BusStopStationInfo {
    val obj = BusStopStationInfo(
        res.getInt("id"),
        res.getString("name"),
        res.getDouble("pos_x"),
        res.getDouble("pos_y"),
        res.getInt("short_id"),
        res.getString("admin_name"),
    )

    return obj
}