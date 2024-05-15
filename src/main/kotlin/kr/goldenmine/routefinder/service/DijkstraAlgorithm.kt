package kr.goldenmine.routefinder.service

import kr.goldenmine.routefinder.model.BusStopStationInfo
import kr.goldenmine.routefinder.model.BusThroughInfo
import kr.goldenmine.routefinder.utils.Point
import kr.goldenmine.routefinder.utils.convertTM127toWGS84
import kr.goldenmine.routefinder.utils.distance
import kr.goldenmine.routefinder.utils.distanceTM127
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.ArrayList

@Service
class DijkstraAlgorithm(
    private final val busRouteService: BusRouteService,
) {

    private val log: Logger = LoggerFactory.getLogger(DijkstraAlgorithm::class.java)

    private final var stations = listOf<BusStopStationInfo>()
    private final var throughs = listOf<BusThroughInfo>()

    // station 1에서 station 2로 갈 때 드는 거리 목록
//    private final var adjointMatrix: Array<DoubleArray> = Array(stations.size) { DoubleArray(stations.size) { -1.0 } }
    private final var nodes = ArrayList<ArrayList<Node>>()

    final var stationsMap = HashMap<Int, BusStopStationInfo>()
    final var stationIdToIndex = HashMap<Int, Int>()

    init {
        initialize()
    }

    final fun initialize() {
        stations = busRouteService.getAllStations()
        throughs = busRouteService.getAllBusThroughInfo()

        nodes = ArrayList()

        stationsMap = HashMap<Int, BusStopStationInfo>()
        stationIdToIndex = HashMap<Int, Int>()

        for(idx in stations.indices) {
            val station = stations[idx]

            stationsMap[idx] = station
            stationIdToIndex[station.id] = idx

            val wgs84 = convertTM127toWGS84(Point(station.posX, station.posY))

            station.posXWGS84 = wgs84.x
            station.posYWGS84 = wgs84.y

            nodes.add(ArrayList())
        }

        val throughsWithStation = busRouteService.getAllThroughsWithStation()

        // 버스 경로 추가
        for(idx in 0 until throughs.size - 1) {
            val start = throughsWithStation[idx]
            val end = throughsWithStation[idx + 1]

            val startId = stationIdToIndex[start.first.id]!!
            val endId = stationIdToIndex[end.first.id]!!

            if(start.second.routeId == end.second.routeId) {
                val routeId = start.second.routeId

                val distance = distanceTM127(
                    Point(start.first.posX, start.first.posY),
                    Point(end.first.posX, end.first.posY)
                )

                nodes[startId].add(Node(endId, distance, routeId))
                // 역방향은 불가능
//                nodes[endId].add(Node(startId, distance, routeId))
            }
        }

        log.info("도보 계산중")
        // 도보 경로 추가
        for(startId in stations.indices) {
            val stationStart = stations[startId]

            for(endId in stations.indices) {
                val stationFinish = stations[endId]

                // 단방향 그래프이므로 한쪽만 계산해도 됨
                if(startId == endId || startId < endId) continue

                val distance = distance(
                    Point(stationStart.posXWGS84!!, stationStart.posYWGS84!!),
                    Point(stationFinish.posXWGS84!!, stationFinish.posYWGS84!!),
                ) * 4 * 1.75
//                val distance = distanceTM127(
//                    Point(stationStart.posX, stationStart.posY),
//                    Point(stationFinish.posX, stationFinish.posY)
//                ) * 3

                // 4km 이하만 도보 허용, 시속 5km에 평균 버스 시속이 20km 정도 되므로 거리 4배 적용
                if(distance <= 4000) {
                    nodes[startId].add(Node(endId, distance, "도보"))
                    nodes[endId].add(Node(startId, distance, "도보"))
                }
            }
        }
        log.info("도보 계산 완료")
    }

    fun executeDijkstraAlgorithm(startIndex: Int, endIndex: Int): List<PreviousNode> {
        val dist = DoubleArray(stations.size)
        val previous = arrayOfNulls<PreviousNode>(stations.size)

        for (i in dist.indices) {
            dist[i] = Double.MAX_VALUE
            previous[i] = PreviousNode(i, null)
        }

        val queue = PriorityQueue<Node>(Comparator.comparingDouble { o -> o.cost })
        queue.offer(Node(startIndex, 0.0, null))
        dist[startIndex] = 0.0

        while (!queue.isEmpty()) {
            val current = queue.poll()

            if (dist[current.index] < current.cost)
                continue

            val nexts = nodes[current.index]

            for (i in nexts.indices) {
                val next = nexts[i]

                // 버스 환승시 2.5km을 더 간것으로 평가한다. -> 다음 노드까지 경로 + 2.5km 추가
                var otherBusCost = 2500
                if(current.busId == null || next.busId == "도보") otherBusCost = 0
                if(current.busId == next.busId) otherBusCost = 0

                val nextCost = current.cost + next.cost + otherBusCost
                if (dist[next.index] > nextCost) {
                    dist[next.index] = nextCost
                    previous[next.index] = PreviousNode(current.index, next.busId)
                    queue.add(Node(next.index, dist[next.index], next.busId))
                }
            }
        }

        return getTraceBack(previous, startIndex, endIndex)
    }

    fun getTraceBack(previousIndices: Array<PreviousNode?>, startIndex: Int, endIndex: Int): List<PreviousNode> {
        val previousNodes = ArrayList<PreviousNode>()

        var current = PreviousNode(endIndex, null)

        var lastBusId: String? = null

        while (true) {
            val previous = previousIndices[current.index]
            if(previous != null) {
                lastBusId = previous.busId
            }
            log.info("$previous, $current, $startIndex, $endIndex")

            // 시작노드까지 싹싹 긁어서 add
            previousNodes.add(current)

            // 시작 노드에 도달했다는 뜻이므로 break
            if (previous == null || previous.index == startIndex || previous.index == current.index) {
                break
            }

//            if(previous == current) sleep(1000L)

            current = previous
        }

        previousNodes.add(PreviousNode(startIndex, lastBusId))

        return previousNodes.reversed()
    }
}

class Node(
    val index: Int,
    val cost: Double,
    val busId: String?,
) {
    override fun toString(): String {
        return "Node{" +
                "idx=" + index +
                ", cost=" + cost +
                '}'
    }
}

class PreviousNode(
    val index: Int,
    val busId: String?,
) {
    override fun toString(): String {
        return "PreviousNode(index=$index, busId=$busId)"
    }
}