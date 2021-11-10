package com.d10ng.latlnglib.bean

import java.io.Serializable

/**
 * 经纬度
 *
 * @Author: D10NG
 * @Time: 2021/6/18 5:56 下午
 */
data class DLatLng(

    /**
     * 纬度
     */
    var latitude: Double = 0.0,

    /**
     * 经度
     */
    var longitude: Double = 0.0,
): Serializable
