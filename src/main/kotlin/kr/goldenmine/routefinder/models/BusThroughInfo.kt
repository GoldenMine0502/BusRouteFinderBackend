package kr.goldenmine.routefinder.models

import com.google.gson.annotations.SerializedName
import lombok.Data
import jakarta.persistence.*

//@Data
//@SequenceGenerator(name = "bus_through_info_sequence_generator", sequenceName = "bus_stop_sequence")
class BusThroughInfo(
    /*
    	id INT(11) NOT NULL PRIMARY KEY,
	route_id VARCHAR(20),
    bus_stop_station_id VARCHAR(20),
    bus_stop_sequence INT(11)
     */
    @Id
    @Column(name = "id")
    val id: Int,

    @Column(name = "route_id")
    val routeId: String,

    @Column(name = "bus_stop_station_id")
    val busStopStationId: Int,

    @Column(name = "bus_stop_sequence")
    val busStopSequence: Int,
    ) {

}