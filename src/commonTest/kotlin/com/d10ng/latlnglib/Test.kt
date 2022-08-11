package com.d10ng.latlnglib

import com.d10ng.latlnglib.bean.DLatLng
import com.d10ng.latlnglib.constant.CoordinateSystemType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Test {

    @Test
    fun test() {
        val pattern = "CH Fd°m′S.ss″ "
        val string = 103.5863933.toLongitudeString(pattern)
        println(string)
        println(string.toLatOrLng(pattern))
        println(29.73784595.toLatitudeString(pattern))
        println(113.03131.latLng2ddmmpmmmm())

        val str1 = "E118°22′26.3″"
        println(str1.toLatOrLng("Fd°m′S.s″"))

        // 经纬度的坐标系转换
        val gcj02 = DLatLng(29.7378, 103.5863).convert(CoordinateSystemType.WGS84, CoordinateSystemType.GCJ02)
        println(gcj02)
        // DLatLng(latitude=29.734910753953383, longitude=103.58825934357566)
        assertTrue {
            gcj02.latitude == 29.734910753953383 && gcj02.longitude == 103.58825934357566
        }
    }

    @Test
    fun testCoordinateTransformConvert() {
        for (i in 0 .. 100) {
            val lat = (0 .. 89).random() + (10000 .. 99999).random() * 0.00001
            val lng = (0 .. 179).random() + (10000 .. 99999).random() * 0.00001
            var gcj02Point = DLatLng(lat, lng)
            //println(gcj02Point)
            var wgs84Point = gcj02Point.convert(CoordinateSystemType.GCJ02, CoordinateSystemType.WGS84)
            //println(wgs84Point)
            gcj02Point = wgs84Point.convert(CoordinateSystemType.WGS84, CoordinateSystemType.GCJ02)
            //println(gcj02Point)

            println("latDiff=${lat-gcj02Point.latitude},lngDiff=${lng-gcj02Point.longitude}")
        }
    }

    @Test
    fun testKeepDecimal() {
        // [734],35.97248,35.97247,35.97248
        // [45],8.13872,8.13871,8.13872
        // [318]77.35067,77.35066,77.35067
        for (i in 0 .. 1000) {
            val num = (0 .. 89).random() + (1000000 .. 9999999).random() * 0.0000001
            val keep = num.keepDecimal(5)
            var numStr = num.toString()
            val numStrS = numStr.split(".").toMutableList()
            numStrS[1] = numStrS[1].up2Length(5, isInStart = false, isForced = true)
            numStr = "${numStrS[0]}.${numStrS[1]}"
            println("[${i}]${num},${keep},${numStr}")
            assertEquals(keep, numStr.toDouble())
        }
    }
}