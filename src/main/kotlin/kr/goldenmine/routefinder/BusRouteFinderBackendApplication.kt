package kr.goldenmine.routefinder

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BusRouteFinderBackendApplication

fun main(args: Array<String>) {
    runApplication<BusRouteFinderBackendApplication>(*args)
}
