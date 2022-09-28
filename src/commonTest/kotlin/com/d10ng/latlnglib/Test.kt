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
    fun testLatitudeToDMS() {
        val map = mapOf(
            50.64363 to arrayOf(50,38,37.068f),
            64.20716 to arrayOf(64,12,25.776f),
            59.36704 to arrayOf(59,22,1.344f),
            4.69648 to arrayOf(4,41,47.328f),
            59.31789 to arrayOf(59,19,4.404f),
            88.83955 to arrayOf(88,50,22.38f),
            36.59161 to arrayOf(36,35,29.796f),
            45.76718 to arrayOf(45,46,1.848f),
            27.11171 to arrayOf(27,6,42.156f),
            44.47969 to arrayOf(44,28,46.884f),
            56.57414 to arrayOf(56,34,26.904f),
            176.51667 to arrayOf(176,31,0.012f),
            169.98483 to arrayOf(169,59,5.388f),
            38.31324 to arrayOf(38,18,47.664f),
            137.30027 to arrayOf(137,18,0.972f),
            4.83266 to arrayOf(4,49,57.576f),
            68.40851 to arrayOf(68,24,30.636f),
            101.10532 to arrayOf(101,6,19.152f),
            95.87154 to arrayOf(95,52,17.544f),
            72.71799 to arrayOf(72,43,4.764f),
            65.19124 to arrayOf(65,11,28.464f),
            28.934 to arrayOf(28,56,2.4f),
        )
        map.forEach { item ->
            val dms = item.key.toDMS(item.key > 90)
            assertEquals(dms.degrees, item.value[0])
            assertEquals(dms.minutes, item.value[1])
            assertEquals(dms.seconds, item.value[2])
            val latlng = dms.toLatLng()
            assertTrue(item.key in (latlng - 0.00001) .. (latlng + 0.00001))
        }
    }
}