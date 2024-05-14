package kr.goldenmine.routefinder.controller

import kr.goldenmine.routefinder.model.User
import kr.goldenmine.routefinder.request.RequestDeleteLog
import kr.goldenmine.routefinder.request.RequestSearch
import kr.goldenmine.routefinder.request.ResponseMyLog
import kr.goldenmine.routefinder.request.ResponseSearch
import kr.goldenmine.routefinder.service.BusRouteService
import kr.goldenmine.routefinder.service.LogService
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/search")
class SearchController(
    val logService: LogService,
    val busRouteService: BusRouteService
) {
    @GetMapping("/mylog")
    fun getMyLog(user: User): ResponseMyLog {
        val list = logService.getLog(user)

        return ResponseMyLog(list)
    }

    @GetMapping("/alllog")
    fun getAllLog(user: User): ResponseMyLog {
        if(!user.isAdmin) throw BadCredentialsException("you have no permission")
        val list = logService.getLogAdmin()

        return ResponseMyLog(list)
    }

    @DeleteMapping("/log")
    fun deleteLog(user: User, @RequestBody request: RequestDeleteLog) {
        if(!user.isAdmin) throw BadCredentialsException("you have no permission")
        logService.deleteLog(request.id)
    }

    @GetMapping("/search")
    fun searchStation(request: RequestSearch): ResponseSearch {
        val list = busRouteService.searchStation(request.keyword)

        return ResponseSearch(list)
    }
}