package com.d10ng.latlnglib

import com.d10ng.latlnglib.bean.DLatLng
import com.d10ng.latlnglib.constant.CoordinateSystemType
import kotlin.test.Test
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
}