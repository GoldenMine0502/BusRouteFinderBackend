package kr.goldenmine.routefinder.service

import kr.goldenmine.routefinder.model.BusStopStationInfo
import kr.goldenmine.routefinder.utils.Point
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

    private final val stations = busRouteService.getAllStations()
    private final val throughs = busRouteService.getAllBusThroughInfo()

    // station 1에서 station 2로 갈 때 드는 거리 목록
//    private final var adjointMatrix: Array<DoubleArray> = Array(stations.size) { DoubleArray(stations.size) { -1.0 } }
    private final val nodes = ArrayList<ArrayList<Node>>()

    final val stationsMap = HashMap<Int, BusStopStationInfo>()
    final val stationIdToIndex = HashMap<Int, Int>()

    init {
//        for(idx1 in stations.indices) {
//            val stationStart = stations[idx1]
//
//            for(idx2 in stations.indices) {
//                val stationFinish = stations[idx2]
//
//                // 단방향 그래프이므로 한쪽만 계산해도 됨
//                if(idx1 == idx2 || idx1 < idx2) continue
//
//                val distance = distanceTM127(
//                    Point(stationStart.posX, stationStart.posY),
//                    Point(stationFinish.posX, stationFinish.posY)
//                )
//
//                adjointMatrix[idx1][idx2] = distance
//                adjointMatrix[idx2][idx1] = distance
//
//            }
//        }

        for(idx in stations.indices) {
            stationsMap[idx] = stations[idx]
            stationIdToIndex[stations[idx].id] = idx

            nodes.add(ArrayList())
        }

        val throughsWithStation = busRouteService.getAllThroughsWithStation()

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
                nodes[endId].add(Node(startId, distance, routeId))
            }
        }
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

                // 버스 환승시 1km을 더 간것으로 평가한다. -> 다음 노드까지 경로 + 1km 추가
                var otherBusCost = 1000
                if(current.busId == null) otherBusCost = 0
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
//            log.info("$previous, $current, $startIndex, $endIndex")

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