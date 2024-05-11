package kr.goldenmine.routefinder.request

class DijkstraNodeDTO(
    val posX: Double,
    val posY: Double,
    val stationName: String,
    val busName: String?,
) {
}