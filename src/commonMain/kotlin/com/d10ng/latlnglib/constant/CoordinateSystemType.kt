package com.d10ng.latlnglib.constant

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * 坐标系类型
 *
 * @Author: D10NG
 * @Time: 2021/6/18 5:51 下午
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
enum class CoordinateSystemType {

    /**
     * 地球坐标系，国际上通用的坐标系。
     * - 设备一般包含GPS芯片或者北斗芯片获取的经纬度为WGS84地理坐标系；
     * - 谷歌地图采用的是WGS84地理坐标系（中国范围除外）；
     */
    WGS84,

    /**
     * 火星坐标系，是由中国国家测绘局制订的地理信息系统的坐标系统。
     * - 由WGS84坐标系经加密后的坐标系；
     * - 谷歌中国地图和搜狗中国地图采用的是GCJ02地理坐标系；
     */
    GCJ02,

    /**
     * 百度坐标系。
     * - GCJ02坐标系经加密后的坐标系；
     */
    BD09
}