package kr.goldenmine.routefinder.controller

import jakarta.servlet.http.HttpServletRequest
import kr.goldenmine.routefinder.model.BusInfo
import kr.goldenmine.routefinder.model.BusStopStationInfo
import kr.goldenmine.routefinder.model.BusThroughInfo
import kr.goldenmine.routefinder.model.User
import kr.goldenmine.routefinder.request.*
import kr.goldenmine.routefinder.service.BusRouteService
import kr.goldenmine.routefinder.service.DijkstraAlgorithm
import kr.goldenmine.routefinder.service.LogService
import kr.goldenmine.routefinder.service.UserService
import org.apache.coyote.BadRequestException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.*

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
            logService.writeLog(user, start.name, start.shortId, end.name, end.shortId)
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

    @GetMapping("/route")
    fun getBusRoute(request: RequestBusRoute): RouteFindResponse {
        return RouteFindResponse(
            busRouteService.getBusRoutesByRouteNo(request.routeNo).map {
                DijkstraNodeDTO(it.first.posX, it.first.posY, it.first.name, it.first.shortId, request.routeNo)
            }
        )
    }

    @GetMapping("/station")
    fun getStation(request: RequestBusStation): ResponseEntity<BusStopStationInfo?> {
        val res = busRouteService.getStationByShortId(request.shortId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)

        return ResponseEntity.ok(res)
    }

    @GetMapping("/update")
    fun update(user: User) {
        if(!user.isAdmin) throw BadCredentialsException("you have no permission")
        dijkstraAlgorithm.initialize()
    }

    @GetMapping("/bus")
    fun getAllBus(): List<BusInfo> {
        return busRouteService.getAllBus()
    }

    @PutMapping("/bus")
    fun addBus(user: User, @RequestBody busInfo: BusInfo) {
        if(!user.isAdmin) throw BadCredentialsException("you have no permission")
        return busRouteService.addBus(busInfo)
    }

    @PatchMapping("/bus")
    fun editBus(user: User, @RequestBody busInfo: BusInfo) {
        if(!user.isAdmin) throw BadCredentialsException("you have no permission")
        return busRouteService.editBus(busInfo)
    }

    @DeleteMapping("/bus")
    fun deleteBus(user: User, @RequestBody busInfo: RequestDeleteById) {
        if(!user.isAdmin) throw BadCredentialsException("you have no permission")
        return busRouteService.deleteBus(busInfo.id)
    }

    @GetMapping("/busthrough")
    fun getAllBusThrough(): List<BusThroughInfo> {
        return busRouteService.getAllBusThroughInfo()
    }

    @PutMapping("/busthrough")
    fun addBusThrough(user: User, @RequestBody busInfo: BusThroughInfo) {
        if(!user.isAdmin) throw BadCredentialsException("you have no permission")
        return busRouteService.addBusThrough(busInfo)
    }

    @PatchMapping("/busthrough")
    fun editBusThrough(user: User, @RequestBody busInfo: BusThroughInfo) {
        if(!user.isAdmin) throw BadCredentialsException("you have no permission")
        return busRouteService.editBusThrough(busInfo)
    }

    @DeleteMapping("/busthrough")
    fun deleteBusThrough(user: User, @RequestBody busInfo: RequestDeleteById) {
        if(!user.isAdmin) throw BadCredentialsException("you have no permission")
        return busRouteService.deleteBusThrough(busInfo.id)
    }

    @GetMapping("/busstation")
    fun getAllStations(): List<BusStopStationInfo> {
        return busRouteService.getAllStations()
    }

    @PutMapping("/busstation")
    fun addStation(user: User, @RequestBody busInfo: BusStopStationInfo) {
        if(!user.isAdmin) throw BadCredentialsException("you have no permission")
        return busRouteService.addStation(busInfo)
    }

    @PatchMapping("/busstation")
    fun editStation(user: User, @RequestBody busInfo: BusStopStationInfo) {
        if(!user.isAdmin) throw BadCredentialsException("you have no permission")
        return busRouteService.editStation(busInfo)
    }

    @DeleteMapping("/busstation")
    fun deleteStation(user: User, @RequestBody busInfo: RequestDeleteById) {
        if(!user.isAdmin) throw BadCredentialsException("you have no permission")
        return busRouteService.deleteStation(busInfo.id)
    }
}