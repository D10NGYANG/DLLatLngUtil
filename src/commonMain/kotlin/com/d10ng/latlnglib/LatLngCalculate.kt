@file:OptIn(ExperimentalJsExport::class)
@file:JsExport
package com.d10ng.latlnglib

import com.d10ng.latlnglib.bean.DLatLng
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.math.*
import kotlin.random.Random

/**
 * 计算两点之间的距离，单位为米
 * @param point1 DLatLng
 * @param point2 DLatLng
 * @return Double
 */
fun getDistanceOn2Points(point1: DLatLng, point2: DLatLng): Double {
    val earthR = 6371e3 // 地球半径，单位为米
    val lat1Rad = toRadians(point1.latitude)
    val lat2Rad = toRadians(point2.latitude)
    val deltaLat = toRadians(point2.latitude - point1.latitude)
    val deltaLon = toRadians(point2.longitude - point1.longitude)
    val a = sin(deltaLat / 2) * sin(deltaLat / 2) + cos(lat1Rad) * cos(lat2Rad) * sin(deltaLon / 2) * sin(deltaLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthR * c
}

/**
 * 将角度转换为弧度
 * @param degree Double
 * @return Double
 */
private fun toRadians(degree: Double): Double {
    return degree * PI / 180
}

/**
 * 将弧度转换为角度
 * @param radian Double
 * @return Double
 */
private fun toDegrees(radian: Double): Double {
    return radian * 180 / PI
}

/**
 * 判断点是否在圆内
 * @param point DLatLng 待判断坐标点
 * @param center DLatLng 圆圈中心点
 * @param radius Double 圆圈半径
 * @param offset Float 误差偏移
 * @return Boolean
 */
fun isPointInCircle(point: DLatLng, center: DLatLng, radius: Double, offset: Float = 0f): Boolean {
    return getDistanceOn2Points(point, center) + offset <= radius
}

/**
 * 计算两个坐标点之间的夹角，单位度
 * @param point1 DLatLng 起点
 * @param point2 DLatLng 终点
 * @return Double
 */
fun getAngleOn2Points(point1: DLatLng, point2: DLatLng): Double {
    val x = cos(point1.latitude) * sin(point2.latitude) - sin(point1.latitude) * cos(point2.latitude) * cos(point2.longitude - point1.longitude)
    val y = sin(point2.longitude - point1.longitude) * cos(point2.latitude)
    val bearing = toDegrees(atan2(y, x))
    return if (bearing < 0) bearing + 360 else bearing
}


/**
 * 从两个坐标间根据百分比获取中间坐标
 * @param point1 DLatLng 起点
 * @param point2 DLatLng 终点
 * @param present Float 百分比 0.0～1.0
 * @return DLatLng
 */
fun getPointOn2Points(point1: DLatLng, point2: DLatLng, present: Float): DLatLng {
    val distance = getDistanceOn2Points(point1, point2)
    val angle = getAngleOn2Points(point1, point2)
    val newDistance = distance * present
    return getPointByBasePoint(point1, newDistance, angle)
}

/**
 * 计算一段轨迹的总距离，单位为米
 * @param points Array<DLatLng>
 * @return Double
 */
fun getTotalDistance(points: Array<DLatLng>): Double {
    var total = 0.0
    for (i in 0 until points.size - 1) {
        total += getDistanceOn2Points(points[i], points[i + 1])
    }
    return total
}

/**
 * 从一段轨迹中获取间隔指定距离的坐标点重新组成的轨迹
 * @param points Array<DLatLng> 原始轨迹
 * @param distance Double 间隔距离，单位为米
 * @return Array<DLatLng> 新的轨迹
 */
fun getPointsOnDistance(points: Array<DLatLng>, distance: Double): Array<DLatLng> {
    val result = mutableListOf<DLatLng>()
    var totalDistance = 0.0
    points.forEachIndexed { index, point ->
        if (index == 0) {
            totalDistance = 0.0
        } else {
            val lastPoint = points[index - 1]
            val lastDistance = totalDistance
            val curDistance = getDistanceOn2Points(lastPoint, point)
            val newDistance = lastDistance + curDistance
            val totalCount = floor(newDistance / distance).toInt()
            if (totalCount > result.size) {
                val count = totalCount - result.size
                var hasHandleDistance = lastDistance
                var noHandleDistance = curDistance
                var p0 = lastPoint
                for (i in 0 until count) {
                    val lessDistance = distance - (hasHandleDistance % distance)
                    val present = lessDistance / noHandleDistance
                    p0 = getPointOn2Points(p0, point, present.toFloat())
                    result.add(p0)
                    hasHandleDistance += lessDistance
                    noHandleDistance -= lessDistance
                }
            }
            totalDistance = newDistance
        }
    }
    result.add(0, points[0])
    result.add(points[points.size - 1])
    return result.toTypedArray()
}

/**
 * 压缩轨迹
 * @param points Array<DLatLng> 原始轨迹
 * @return Array<DLatLng> 新的轨迹
 */
fun compressTrack(points: Array<DLatLng>): Array<DLatLng> {
    if (points.size <= 10) return points.copyOf()
    val result = mutableListOf<DLatLng>()
    var a = points[0]
    var b = points[1]
    for (i in points.indices) {
        if (i < 2) {
            result.add(points[i])
            continue
        }
        val c = points[i]
        val distance = getDistanceOn2Points(a, b) + getDistanceOn2Points(b, c)
        val x = if (distance <= 10) 90 else if (distance > 50) 5 else max(((1 - distance / 50) * 90).toInt(), 5)
        val j1 = getAngleOn2Points(a, b)
        val j2 = getAngleOn2Points(b, c)
        if (abs(j1 - j2) < x) {
            result.removeLast()
        } else {
            a = b
        }
        result.add(c)
        b = c
    }
    return result.toTypedArray()
}

/**
 * 根据距离与角度从一个坐标点获取另一个坐标点
 * @param point DLatLng 基准点
 * @param distance Double 距离，单位为米
 * @param angle Double 角度，单位为弧度
 * @return DLatLng 新的坐标点
 */
fun getPointByBasePoint(point: DLatLng, distance: Double, angle: Double): DLatLng {
    val radian = toRadians(angle)
    val earthR = 6371e3
    val newLat = point.latitude + distance * cos(radian) / (earthR * 2 * PI / 360)
    val newLng = point.longitude + distance * sin(radian) / (earthR * cos(newLat) * 2 * PI / 360)
    return DLatLng(newLat, newLng)
}

/**
 * 基于一个中心点的半径范围获取一个随机坐标点
 * @param point DLatLng 中心点
 * @param radius Double 半径
 * @return DLatLng 随机坐标点
 */
fun getRandomPoint(point: DLatLng, radius: Double): DLatLng {
    // 随机角度
    val randomAngle = Random.nextDouble() * PI * 2
    // 随机距离
    val randomDistance = Random.nextDouble() * radius
    return getPointByBasePoint(point, randomDistance, randomAngle)
}

/**
 * 获取一个位置点在一条轨迹上最靠近的几个线段，即这个位置点最有可能吸附到的几个线段，最少会有一个线段，最多会有三个线段
 * @param point DLatLng 位置点
 * @param line Array<DLatLng> 轨迹，至少两个点，最好是换算成相同间隔的轨迹（如间隔100米一个点），可以使用函数getPointsOnDistance进行换算
 * @param offset Float 误差范围，单位为米，即返回的最靠近的线段列表中第一个线段的距离和最后一个线段的距离之差不能超过这个值
 * @return Array<Int> 最靠近的线段的索引列表，索引从0开始，假设返回的是[0, 1, 2]，则表示最靠近的线段是line[0]～line[1]，次靠近的线段是line[1]～line[2]，然后线段是line[2]～line[3]
 */
fun getNearPartsOnLine(point: DLatLng, line: Array<DLatLng>, offset: Float = 10f): Array<Int> {
    val ls = mutableListOf<Pair<Int, Double>>()
    for (i in 0 until line.size - 1) {
        val distance = getDistanceOn2Points(point, line[i]) + getDistanceOn2Points(point, line[i + 1])
        if (ls.isEmpty()) ls.add(i to distance)
        else if (distance > ls[0].second + offset) continue
        else if (distance < ls[0].second - offset) {
            ls.clear()
            ls.add(i to distance)
        }
        else {
            for (j in 0 until ls.size) {
                if (distance < ls[j].second) {
                    ls.add(j, i to distance)
                    break
                }
            }
            if (ls.size > 3) ls.removeAt(3)
        }
    }
    return ls.map { it.first }.toTypedArray()
}

/**
 * 获取一个位置点在一条线段上的投影点
 * @param point DLatLng 位置点
 * @param part Array<DLatLng> 线段，必须是两个点
 * @return DLatLng 投影点
 */
fun getProjectionPointOnLinePart(point: DLatLng, part: Array<DLatLng>): DLatLng {
    val start = part[0]
    val end = part[1]
    val p2s = getDistanceOn2Points(point, start)
    val p2e = getDistanceOn2Points(point, end)
    val s2e = getDistanceOn2Points(start, end)
    return if ((p2s - p2e) > s2e)  end
    else if ((p2e - p2s) > s2e) start
    else getPointOn2Points(start, end, (p2s / (p2s + p2e)).toFloat())
}

/**
 * 获取一个位置点在一条轨迹上的投影点
 * @param point DLatLng 位置点
 * @param line Array<DLatLng> 轨迹，至少两个点，最好是换算成相同间隔的轨迹（如间隔100米一个点），可以使用函数getPointsOnDistance进行换算
 * @return DLatLng 投影点
 */
fun getProjectionPointOnLine(point: DLatLng, line: Array<DLatLng>): DLatLng {
    // 获取位置点在轨迹上最靠近的几个线段
    val nearParts = getNearPartsOnLine(point, line)
    // 获取位置点在最靠近的线段上的投影点
    return getProjectionPointOnLinePart(point, arrayOf(line[nearParts[0]], line[nearParts[0] + 1]))
}

/**
 * 获取一个位置点在一条轨迹上的投影线，从起点开始算
 * - 目前适用于马拉松比赛中的收尾车的轨迹拟合到活动线路来
 * @param point DLatLng 位置点
 * @param targetLine Array<DLatLng> 目标线路，最好是换算成相同间隔的轨迹（如间隔100米一个点），可以使用函数getPointsOnDistance进行换算
 * @return Array<DLatLng> 投影线
 */
fun getProjectionLineOnLineWithPoint(point: DLatLng, targetLine: Array<DLatLng>): Array<DLatLng> {
    // 获取位置点在轨迹上最靠近的几个线段
    val nearParts = getNearPartsOnLine(point, targetLine)
    val nearIndex = nearParts[0]
    val projectionPoint = getProjectionPointOnLinePart(point, arrayOf(targetLine[nearIndex], targetLine[nearIndex + 1]))
    return targetLine.copyOfRange(0, nearIndex + 1).plus(projectionPoint)
}

/**
 * 获取线路在另一条线路上的投影线，从起点开始算
 * - 目前适用于马拉松比赛中的收尾车的轨迹拟合到活动线路来
 * @param line Array<DLatLng> 线路
 * @param targetLine Array<DLatLng> 目标线路，起码有两个点，最好是换算成相同间隔的轨迹（如间隔100米一个点），可以使用函数getPointsOnDistance进行换算
 * @param offset Float 误差范围，单位为米，计算的最靠近的线段列表中第一个线段的距离和最后一个线段的距离之差不能超过这个值
 * @return Array<DLatLng> 投影线
 */
fun getProjectionLineOnLineWithLine(line: Array<DLatLng>, targetLine: Array<DLatLng>, offset: Float = 10f): Array<DLatLng> {
    // 如果一个点都没有的线路，直接返回目标线路的第一个点
    if (line.isEmpty()) return arrayOf(targetLine[0])
    // 先对轨迹进行压缩，清除一些不必要的点
    val compressLine = compressTrack(line)
    // 获取最新一个位置点在轨迹上最靠近的几个线段
    val newPoint = compressLine[compressLine.size - 1]
    val nearParts = getNearPartsOnLine(newPoint, targetLine, offset)
    // 如果轨迹就一个点，或者最近的线段就一个，那就拿最近的线段去计算投影点
    val nearIndex = if (compressLine.size == 1 || nearParts.size == 1) 0
    else {
        // 多个线段的话，就得拿上一个位置点和最新位置点来计算方向，和每个线段的方向进行比较，找到最相似的方向，就拿最新位置点去计算投影点
        val lastPoint = compressLine[compressLine.size - 2]
        val newAngle = getAngleOn2Points(lastPoint, newPoint)
        val nearAngles = nearParts.map { getAngleOn2Points(targetLine[it], targetLine[it + 1]) }
        val nearAngle = nearAngles.minBy { abs(it - newAngle) }
        nearAngles.indexOf(nearAngle)
    }
    val part = arrayOf(targetLine[nearParts[nearIndex]], targetLine[nearParts[nearIndex] + 1])
    val projectionPoint = getProjectionPointOnLinePart(newPoint, part)
    return targetLine.copyOfRange(0, nearParts[nearIndex] + 1).plus(projectionPoint)
}