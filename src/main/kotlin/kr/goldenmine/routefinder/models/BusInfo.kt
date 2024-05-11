package kr.goldenmine.routefinder.models

import com.google.gson.annotations.SerializedName
import jakarta.persistence.*

/*
	route_id VARCHAR(20) NOT NULL PRIMARY KEY,
    route_len INT(11),
    route_no VARCHAR(20),
	origin_bus_stop_id VARCHAR(20),
    dest_bus_stop_id VARCHAR(20),
    bus_start_time VARCHAR(10),
    bus_finish_time VARCHAR(10),
    max_allocation_gap INT(11),
    min_allocation_gap INT(11),
    route_type INT(11),
    turn_bus_stop_id VARCHAR(20)
 */
//@Data
class BusInfo(
    @Id
    @Column(name = "route_id")
    val routeId: String,

    @Column(name = "route_len")
    val routeLen: Int,

    @Column(name = "route_no")
    val routeNo: String,

    @Column(name = "origin_bus_stop_id")
    val originBusStopId: Int,

    @Column(name = "dest_bus_stop_id")
    val destBusStopId: Int,

    @Column(name = "bus_start_time")
    val busStartTime: String,

    @Column(name = "bus_finish_time")
    val busFinishTime: String,

    @Column(name = "max_allocation_gap")
    val maxAllocationGap: Int,

    @Column(name = "min_allocation_gap")
    val minAllocationGap: Int,

    @Column(name = "route_type")
    val routeType: Int,

    @Column(name = "turn_bus_stop_id")
    val turnBusStopId: Int,
) {
    override fun hashCode(): Int {
        return routeId.toInt()
    }

    override fun equals(other: Any?): Boolean {
        if(other is BusInfo) {
            return routeId == other.routeId
        }

        return false
    }
}