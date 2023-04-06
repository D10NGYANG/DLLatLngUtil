@file:OptIn(ExperimentalJsExport::class)
@file:JsExport
package com.d10ng.latlnglib

import com.d10ng.latlnglib.bean.DLatLng
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.math.*

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
 * @param point1 DLatLng
 * @param point2 DLatLng
 * @return Double
 */
fun getAngleOn2Points(point1: DLatLng, point2: DLatLng): Double {
    val lat1 = toRadians(point1.latitude)
    val lat2 = toRadians(point2.latitude)
    val lng1 = toRadians(point1.longitude)
    val lng2 = toRadians(point2.longitude)
    val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(lng2 - lng1)
    val y = sin(lng2 - lng1) * cos(lat2)
    val bearing = atan2(y, x)
    return (bearing * 180 / PI + 360) % 360
}


/**
 * 从两个坐标间根据百分比获取中间坐标
 * @param point1 DLatLng
 * @param point2 DLatLng
 * @param present Float 百分比 0.0～1.0
 * @return DLatLng
 */
fun getPointOn2Points(point1: DLatLng, point2: DLatLng, present: Float): DLatLng {
    return DLatLng(
        point1.latitude + (point2.latitude - point1.latitude) * present,
        point1.longitude + (point2.longitude - point1.longitude) * present
    )
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
                    p0= getPointOn2Points(p0, point, present.toFloat())
                    result.add(p0)
                    hasHandleDistance += lessDistance
                    noHandleDistance -= lessDistance
                }
            }
            totalDistance = newDistance
        }
    }
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