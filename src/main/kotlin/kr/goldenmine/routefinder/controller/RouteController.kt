package kr.goldenmine.routefinder.controller

import kr.goldenmine.routefinder.service.BusRouteService
import kr.goldenmine.routefinder.service.DijkstraAlgorithm
import kr.goldenmine.routefinder.request.DijkstraNodeDTO
import kr.goldenmine.routefinder.request.RouteFindRequest
import kr.goldenmine.routefinder.request.RouteFindResponse
import org.apache.coyote.BadRequestException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/route")
class RouteController(
    val busRouteService: BusRouteService,
    val dijkstraAlgorithm: DijkstraAlgorithm,
) {
    @GetMapping("/find")
    fun getBusRoute(routeFindRequest: RouteFindRequest): RouteFindResponse {
        val start = busRouteService.getStationByShortId(routeFindRequest.startShortId)
        val end = busRouteService.getStationByShortId(routeFindRequest.endShortId)

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
                    val busInfo = if (it.busId != null) busRouteService.getBusInfoById(it.busId) else null

                    DijkstraNodeDTO(station.posX, station.posY, station.name, busInfo?.routeNo)
                }.toList()
        )
    }
}