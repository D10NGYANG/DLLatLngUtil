@file:OptIn(ExperimentalJsExport::class)
@file:JsExport
package com.d10ng.latlnglib

import com.d10ng.latlnglib.bean.DLatLng
import com.d10ng.latlnglib.bean.DistanceAndBearing
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.math.*
import kotlin.random.Random

// 地球长半径，单位为米
private const val EARTH_RADIUS_LONG = 6378137.0
// 地球短半径，单位为米
private const val EARTH_RADIUS_SHORT = 6356752.314245
// 地球扁率，1/298.257223563
private const val EARTH_FLATTENING = 0.003352810664747481

/**
 * 获取两点间的距离和方位角
 * - 使用Vincenty算法，参考：http://www.movable-type.co.uk/scripts/latlong-vincenty.html
 * @param point1 DLatLng
 * @param point2 DLatLng
 * @return DistanceAndBearing
 */
fun getDistanceAndBearing(point1: DLatLng, point2: DLatLng): DistanceAndBearing {
    val radLon1 = toRadians(point1.longitude)
    val radLat1 = toRadians(point1.latitude)
    val radLon2 = toRadians(point2.longitude)
    val radLat2 = toRadians(point2.latitude)
    val L = radLon2 - radLon1
    val tanU1 = (1 - EARTH_FLATTENING) * tan(radLat1)
    val cosU1 = 1 / sqrt((1 + tanU1 * tanU1))
    val sinU1 = tanU1 * cosU1
    val tanU2 = (1 - EARTH_FLATTENING) * tan(radLat2)
    val cosU2 = 1 / sqrt((1 + tanU2 * tanU2))
    val sinU2 = tanU2 * cosU2
    val antipodal = abs(L) > PI / 2 || abs(radLat2 - radLat1) > PI / 2
    var f9 = L
    var sinF9: Double
    var cosF9: Double
    var fq = if (antipodal) PI else 0.0
    var sinFq = 0.0
    var cosFq = if (antipodal) -1.0 else 1.0
    var sinSFqFa: Double
    var cos2Fqm = 1.0
    var cosSFqFa = 1.0
    var iterations = 0
    var f91: Double
    do {
        sinF9 = sin(f9)
        cosF9 = cos(f9)
        sinSFqFa = (cosU2 * sinF9) * (cosU2 * sinF9) + (cosU1 * sinU2 - sinU1 * cosU2 * cosF9) * (cosU1 * sinU2 - sinU1 * cosU2 * cosF9)
        if (abs(sinSFqFa) < 1e-24) break
        sinFq = sqrt(sinSFqFa)
        cosFq = sinU1 * sinU2 + cosU1 * cosU2 * cosF9
        fq = atan2(sinFq, cosFq)
        val sinFa = cosU1 * cosU2 * sinF9 / sinFq
        cosSFqFa = 1 - sinFa * sinFa
        cos2Fqm = if (cosSFqFa != 0.0) cosFq - 2 * sinU1 * sinU2 / cosSFqFa else 0.0
        val C = (EARTH_FLATTENING / 16) * cosSFqFa * (4 + EARTH_FLATTENING * (4 - 3 * cosSFqFa))
        f91 = f9
        f9 = L + (1 - C) * EARTH_FLATTENING * sinFa * (fq + C * sinFq * (cos2Fqm + C * cosFq * (-1 + 2 * cos2Fqm * cos2Fqm)))
        val iterationCheck = if (antipodal) abs(f9) - PI else abs(f9)
        if (iterationCheck > PI) throw RuntimeException("λ > π")
    } while (abs(f9 - f91) > 1e-12 && ++iterations < 100)
    val uSq = cosSFqFa * (EARTH_RADIUS_LONG * EARTH_RADIUS_LONG - EARTH_RADIUS_SHORT * EARTH_RADIUS_SHORT) / (EARTH_RADIUS_SHORT * EARTH_RADIUS_SHORT)
    val A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)))
    val B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)))
    val deltaFq = B * sinFq * (cos2Fqm + B / 4 * (cosFq * (-1 + 2 * cos2Fqm * cos2Fqm) - B / 6 * cos2Fqm * (-3 + 4 * sinFq * sinFq) * (-3 + 4 * cos2Fqm * cos2Fqm)))
    val s = EARTH_RADIUS_SHORT * A * (fq - deltaFq)
    val alpha1 = if (abs(sinSFqFa) < 1e-12) 0.0 else atan2(cosU2 * sinF9, cosU1 * sinU2 - sinU1 * cosU2 * cosF9)
    val alpha2 = if (abs(sinSFqFa) < 1e-12) PI else atan2(cosU1 * sinF9, -sinU1 * cosU2 + cosU1 * sinU2 * cosF9)
    return DistanceAndBearing(s, toDegrees(alpha1), toDegrees(alpha2))
}

/**
 * 计算两点之间的距离，单位为米
 * @param point1 DLatLng
 * @param point2 DLatLng
 * @return Double
 */
fun getDistanceOn2Points(point1: DLatLng, point2: DLatLng): Double {
    return getDistanceAndBearing(point1, point2).distance
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
    return getDistanceAndBearing(point1, point2).finalBearing
}

/**
 * 从两个坐标间根据百分比获取中间坐标
 * @param point1 DLatLng 起点
 * @param point2 DLatLng 终点
 * @param present Float 百分比 0.0～1.0
 * @return DLatLng
 */
fun getPointOn2Points(point1: DLatLng, point2: DLatLng, present: Float): DLatLng {
    val distanceAndBearing = getDistanceAndBearing(point1, point2)
    val newDistance = distanceAndBearing.distance * present
    return getPointByBasePoint(point1, newDistance, distanceAndBearing.finalBearing)
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
 * - 使用Vincenty direct算法，参考：http://www.movable-type.co.uk/scripts/latlong-vincenty.html
 * @param point DLatLng 基准点
 * @param distance Double 距离，单位为米
 * @param angle Double 角度，单位为弧度
 * @return DLatLng 新的坐标点
 */
fun getPointByBasePoint(point: DLatLng, distance: Double, angle: Double): DLatLng {
    if (distance == 0.0) return point.copy()
    val radLat = toRadians(point.latitude)
    val radLng = toRadians(point.longitude)
    val radAngle = toRadians(angle)
    val sinRadAngle = sin(radAngle)
    val cosRadAngle = cos(radAngle)
    val tanU1 = (1 - EARTH_FLATTENING) * tan(radLat)
    val cosU1 = 1 / sqrt((1 + tanU1 * tanU1))
    val sinU1 = tanU1 * cosU1
    val sigma1 = atan2(tanU1, cosRadAngle)
    val sinAlpha = cosU1 * sinRadAngle
    val cosSqAlpha = 1 - sinAlpha * sinAlpha
    val uSq = cosSqAlpha * (EARTH_RADIUS_LONG * EARTH_RADIUS_LONG - EARTH_RADIUS_SHORT * EARTH_RADIUS_SHORT) / (EARTH_RADIUS_SHORT * EARTH_RADIUS_SHORT)
    val A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)))
    val B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)))
    var sigma = distance / (EARTH_RADIUS_SHORT * A)
    var sinSigma: Double
    var cosSigma: Double
    var cos2SigmaM: Double
    var sigmaP: Double
    var iterations = 0.0
    do {
        cos2SigmaM = cos(2 * sigma1 + sigma)
        sinSigma = sin(sigma)
        cosSigma = cos(sigma)
        val deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)))
        sigmaP = sigma
        sigma = distance / (EARTH_RADIUS_SHORT * A) + deltaSigma
    } while (abs(sigma - sigmaP) > 1e-12 && ++iterations < 100)
    if (iterations >= 100) throw RuntimeException("Vincenty formula failed to converge")
    val x = sinU1 * sinSigma - cosU1 * cosSigma * cosRadAngle
    val lat2 = atan2(sinU1 * cosSigma + cosU1 * sinSigma * cosRadAngle, (1 - EARTH_FLATTENING) * sqrt(sinAlpha * sinAlpha + x * x))
    val lambda = atan2(sinSigma * sinRadAngle, cosU1 * cosSigma - sinU1 * sinSigma * cosRadAngle)
    val C = EARTH_FLATTENING / 16 * cosSqAlpha * (4 + EARTH_FLATTENING * (4 - 3 * cosSqAlpha))
    val L = lambda - (1 - C) * EARTH_FLATTENING * sinAlpha * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)))
    val lng2 = radLng + L
    return DLatLng(toDegrees(lat2), toDegrees(lng2))
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