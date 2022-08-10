@file:OptIn(ExperimentalJsExport::class)
@file:JsExport
package com.d10ng.latlnglib

import com.d10ng.latlnglib.bean.DLatLng
import com.d10ng.latlnglib.constant.CoordinateSystemType
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * 经纬度的坐标系转换
 *
 * @receiver [DLatLng]
 * @param from [CoordinateSystemType] 输入坐标系
 * @param to [CoordinateSystemType] 输出坐标系
 * @return [DLatLng]
 */
fun DLatLng.convert(from: CoordinateSystemType, to: CoordinateSystemType): DLatLng {
    when(from) {
        CoordinateSystemType.WGS84 -> {
            return when(to) {
                CoordinateSystemType.GCJ02 -> {
                    val dd = CoordinateTransform.transformWGS84ToGCJ02(longitude, latitude)
                    DLatLng(dd[1],dd[0])
                }

                CoordinateSystemType.BD09 -> {
                    val dd = CoordinateTransform.transformWGS84ToBD09(longitude, latitude)
                    DLatLng(dd[1],dd[0])
                }

                else -> this
            }
        }
        CoordinateSystemType.GCJ02 -> {
            return when(to) {
                CoordinateSystemType.WGS84 -> {
                    val dd = CoordinateTransform.transformGCJ02ToWGS84(longitude, latitude)
                    DLatLng(dd[1],dd[0])
                }

                CoordinateSystemType.BD09 -> {
                    val dd = CoordinateTransform.transformGCJ02ToBD09(longitude, latitude)
                    DLatLng(dd[1],dd[0])
                }

                else -> this
            }
        }
        CoordinateSystemType.BD09 -> {
            return when(to) {
                CoordinateSystemType.WGS84 -> {
                    val dd = CoordinateTransform.transformBD09ToWGS84(longitude, latitude)
                    DLatLng(dd[1],dd[0])
                }

                CoordinateSystemType.GCJ02 -> {
                    val dd = CoordinateTransform.transformBD09ToGCJ02(longitude, latitude)
                    DLatLng(dd[1],dd[0])
                }

                else -> this
            }
        }
    }
}