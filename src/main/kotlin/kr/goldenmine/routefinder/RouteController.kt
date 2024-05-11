package kr.goldenmine.routefinder

import kr.goldenmine.routefinder.models.BusStopStationInfo
import kr.goldenmine.routefinder.request.DijkstraNodeDTO
import kr.goldenmine.routefinder.request.RouteFindRequest
import kr.goldenmine.routefinder.request.RouteFindResponse
import org.apache.coyote.BadRequestException
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException.BadRequest

@RestController
@RequestMapping("/route")
class RouteController(
    val dbService: DBService,
    val dijkstraAlgorithm: DijkstraAlgorithm,
) {
    @GetMapping("/find")
    fun getBusRoute(routeFindRequest: RouteFindRequest): RouteFindResponse {
        val start = dbService.getStationByShortId(routeFindRequest.startShortId)
        val end = dbService.getStationByShortId(routeFindRequest.endShortId)

        if (start == null || end == null) {
            throw BadRequestException("id is not matched")
        }

        return RouteFindResponse(
            dijkstraAlgorithm.executeDijkstraAlgorithm(
                dijkstraAlgorithm.stationIdToIndex[start.id]!!,
                dijkstraAlgorithm.stationIdToIndex[end.id]!!
            )
                .map {
                    val station = dijkstraAlgorithm.stationsMap[it.index]!!
                    val busInfo = if (it.busId != null) dbService.getBusInfoById(it.busId) else null

                    DijkstraNodeDTO(station.posX, station.posY, station.name, busInfo?.routeNo)
                }.toList()
        )
    }
}