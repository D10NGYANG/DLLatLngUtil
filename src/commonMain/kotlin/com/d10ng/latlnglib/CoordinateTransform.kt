package com.d10ng.latlnglib

import kotlin.math.*

/**
 * 提供了百度坐标（BD09）、国测局坐标（火星坐标，GCJ02）、和WGS84坐标系之间的转换
 *
 * @author Daniel
 * @since 2016/7/27 0027
 */
object CoordinateTransform {
    private const val x_PI = 3.14159265358979324 * 3000.0 / 180.0
    private const val PI = 3.1415926535897932384626
    private const val a = 6378245.0
    private const val ee = 0.00669342162296594323

    /**
     * 百度坐标（BD09）转 GCJ02
     *
     * @param lng 百度经度
     * @param lat 百度纬度
     * @return GCJ02 坐标：[经度，纬度]
     */
    fun transformBD09ToGCJ02(lng: Double, lat: Double): DoubleArray {
        val x = lng - 0.0065
        val y = lat - 0.006
        val z: Double = sqrt(x * x + y * y) - 0.00002 * sin(y * x_PI)
        val theta: Double = atan2(y, x) - 0.000003 * cos(x * x_PI)
        val gcj_lng: Double = z * cos(theta)
        val gcj_lat: Double = z * sin(theta)
        return doubleArrayOf(gcj_lng, gcj_lat)
    }

    /**
     * GCJ02 转百度坐标
     *
     * @param lng GCJ02 经度
     * @param lat GCJ02 纬度
     * @return 百度坐标：[经度，纬度]
     */
    fun transformGCJ02ToBD09(lng: Double, lat: Double): DoubleArray {
        val z: Double = sqrt(lng * lng + lat * lat) + 0.00002 * sin(lat * x_PI)
        val theta: Double = atan2(lat, lng) + 0.000003 * cos(lng * x_PI)
        val bd_lng: Double = z * cos(theta) + 0.0065
        val bd_lat: Double = z * sin(theta) + 0.006
        return doubleArrayOf(bd_lng, bd_lat)
    }

    /**
     * GCJ02 转 WGS84
     *
     * @param lng 经度
     * @param lat 纬度
     * @return WGS84坐标：[经度，纬度]
     */
    fun transformGCJ02ToWGS84(lng: Double, lat: Double): DoubleArray {
        return if (outOfChina(lng, lat)) {
            doubleArrayOf(lng, lat)
        } else {
            var dLat = transformLat(lng - 105.0, lat - 35.0)
            var dLng = transformLng(lng - 105.0, lat - 35.0)
            val radLat = lat / 180.0 * PI
            var magic: Double = sin(radLat)
            magic = 1 - ee * magic * magic
            val sqrtMagic: Double = sqrt(magic)
            dLat = dLat * 180.0 / (a * (1 - ee) / (magic * sqrtMagic) * PI)
            dLng = dLng * 180.0 / (a / sqrtMagic * cos(radLat) * PI)
            val mgLat = lat + dLat
            val mgLng = lng + dLng
            doubleArrayOf(lng * 2 - mgLng, lat * 2 - mgLat)
        }
    }

    /**
     * WGS84 坐标 转 GCJ02
     *
     * @param lng 经度
     * @param lat 纬度
     * @return GCJ02 坐标：[经度，纬度]
     */
    fun transformWGS84ToGCJ02(lng: Double, lat: Double): DoubleArray {
        return if (outOfChina(lng, lat)) {
            doubleArrayOf(lng, lat)
        } else {
            var dLat = transformLat(lng - 105.0, lat - 35.0)
            var dLng = transformLng(lng - 105.0, lat - 35.0)
            val redLat = lat / 180.0 * PI
            var magic: Double = sin(redLat)
            magic = 1 - ee * magic * magic
            val sqrtMagic: Double = sqrt(magic)
            dLat = dLat * 180.0 / (a * (1 - ee) / (magic * sqrtMagic) * PI)
            dLng = dLng * 180.0 / (a / sqrtMagic * cos(redLat) * PI)
            val mgLat = lat + dLat
            val mgLng = lng + dLng
            doubleArrayOf(mgLng, mgLat)
        }
    }

    /**
     * 百度坐标BD09 转 WGS84
     *
     * @param lng 经度
     * @param lat 纬度
     * @return WGS84 坐标：[经度，纬度]
     */
    fun transformBD09ToWGS84(lng: Double, lat: Double): DoubleArray {
        val lngLat = transformBD09ToGCJ02(lng, lat)
        return transformGCJ02ToWGS84(lngLat[0], lngLat[1])
    }

    /**
     * WGS84 转 百度坐标BD09
     *
     * @param lng 经度
     * @param lat 纬度
     * @return BD09 坐标：[经度，纬度]
     */
    fun transformWGS84ToBD09(lng: Double, lat: Double): DoubleArray {
        val lngLat = transformWGS84ToGCJ02(lng, lat)
        return transformGCJ02ToBD09(lngLat[0], lngLat[1])
    }

    private fun transformLat(lng: Double, lat: Double): Double {
        var ret: Double = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * sqrt(abs(lng))
        ret += (20.0 * sin(6.0 * lng * PI) + 20.0 * sin(2.0 * lng * PI)) * 2.0 / 3.0
        ret += (20.0 * sin(lat * PI) + 40.0 * sin(lat / 3.0 * PI)) * 2.0 / 3.0
        ret += (160.0 * sin(lat / 12.0 * PI) + 320 * sin(lat * PI / 30.0)) * 2.0 / 3.0
        return ret
    }

    private fun transformLng(lng: Double, lat: Double): Double {
        var ret: Double = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * sqrt(
            abs(lng)
        )
        ret += (20.0 * sin(6.0 * lng * PI) + 20.0 * sin(2.0 * lng * PI)) * 2.0 / 3.0
        ret += (20.0 * sin(lng * PI) + 40.0 * sin(lng / 3.0 * PI)) * 2.0 / 3.0
        ret += (150.0 * sin(lng / 12.0 * PI) + 300.0 * sin(lng / 30.0 * PI)) * 2.0 / 3.0
        return ret
    }

    /**
     * 判断坐标是否不在国内
     *
     * @param lng 经度
     * @param lat 纬度
     * @return 坐标是否在国内
     */
    fun outOfChina(lng: Double, lat: Double): Boolean {
        return lng < 72.004 || lng > 137.8347 || lat < 0.8293 || lat > 55.8271
    }
}