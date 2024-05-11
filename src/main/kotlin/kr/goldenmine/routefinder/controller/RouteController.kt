package kr.goldenmine.routefinder.controller

import jakarta.servlet.http.HttpServletRequest
import kr.goldenmine.routefinder.service.BusRouteService
import kr.goldenmine.routefinder.service.DijkstraAlgorithm
import kr.goldenmine.routefinder.request.DijkstraNodeDTO
import kr.goldenmine.routefinder.request.RouteFindRequest
import kr.goldenmine.routefinder.request.RouteFindResponse
import kr.goldenmine.routefinder.service.LogService
import kr.goldenmine.routefinder.service.UserService
import org.apache.coyote.BadRequestException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/route")
class RouteController(
    val busRouteService: BusRouteService,
    val dijkstraAlgorithm: DijkstraAlgorithm,
    val userService: UserService,
    val logService: LogService,
) {
    @GetMapping("/find")
    fun getBusRoute(request: HttpServletRequest, routeFindRequest: RouteFindRequest): RouteFindResponse {
        val authorizationHeader = request.getHeader("Authorization")

        val user = if(authorizationHeader != null) { userService.processHeader(authorizationHeader) } else { null }

        val start = busRouteService.getStationByShortId(routeFindRequest.startShortId)
        val end = busRouteService.getStationByShortId(routeFindRequest.endShortId)

        if (start == null || end == null) {
            throw BadRequestException("id is not matched")
        }

        // 검색 기록 로깅
        if(user != null) {
            logService.writeLog(user, start.name, end.name)
        }

        return RouteFindResponse(
            dijkstraAlgorithm.executeDijkstraAlgorithm(
                dijkstraAlgorithm.stationIdToIndex[start.id]!!,
                dijkstraAlgorithm.stationIdToIndex[end.id]!!
            )
                .map {
                    val station = dijkstraAlgorithm.stationsMap[it.index]!!
                    val busName = if(it.busId == "도보") "도보"
                    else if (it.busId != null) busRouteService.getBusInfoById(it.busId)?.routeNo
                    else null


                    DijkstraNodeDTO(station.posX, station.posY, station.name, station.shortId, busName)
                }.toList()
        )
    }
}