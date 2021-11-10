package com.d10ng.latlnglib

import com.d10ng.latlnglib.bean.DLatLng
import com.d10ng.latlnglib.constant.CoordinateSystemType
import kotlin.math.pow
import kotlin.math.roundToInt

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
            when(to) {
                CoordinateSystemType.GCJ02 -> {
                    val dd = CoordinateTransform.transformWGS84ToGCJ02(longitude, latitude)
                    return DLatLng(dd[1],dd[0])
                }
                CoordinateSystemType.BD09 -> {
                    val dd = CoordinateTransform.transformWGS84ToBD09(longitude, latitude)
                    return DLatLng(dd[1],dd[0])
                }
            }
        }
        CoordinateSystemType.GCJ02 -> {
            when(to) {
                CoordinateSystemType.WGS84 -> {
                    val dd = CoordinateTransform.transformGCJ02ToWGS84(longitude, latitude)
                    return DLatLng(dd[1],dd[0])
                }
                CoordinateSystemType.BD09 -> {
                    val dd = CoordinateTransform.transformGCJ02ToBD09(longitude, latitude)
                    return DLatLng(dd[1],dd[0])
                }
            }
        }
        CoordinateSystemType.BD09 -> {
            when(to) {
                CoordinateSystemType.WGS84 -> {
                    val dd = CoordinateTransform.transformBD09ToWGS84(longitude, latitude)
                    return DLatLng(dd[1],dd[0])
                }
                CoordinateSystemType.GCJ02 -> {
                    val dd = CoordinateTransform.transformBD09ToGCJ02(longitude, latitude)
                    return DLatLng(dd[1],dd[0])
                }
            }
        }
    }
    return this
}

/**
 * 判断经度数据是否为东经
 * @receiver [Double]
 * @return [Boolean] true: 东经；false: 西经
 */
fun Double.isEastLongitude(): Boolean {
    return this in 0.0..180.0
}

/**
 * 判断纬度数据是否为北纬
 * @receiver [Double]
 * @return [Boolean] true: 北纬；false: 南纬
 */
fun Double.isNorthLatitude(): Boolean {
    return this in 0.0 .. 90.0
}

/**
 * 将经度转换成不带前缀的数据
 * - 当数据大于180度或者小于0度表示西经
 * @receiver [Double]
 * @return [Double] 返回去除"-"或小于180的数值；eg：输入181.1，返回1.1; 输入-1.1返回1.1;
 */
fun Double.toLongitudeNoPre(): Double {
    // 收到经度数据"113.0312511"，如果这个数小于等于180代表东经，大于180度表示西经
    var longitudeData = this
    if (longitudeData > 180) {
        longitudeData = 360 - longitudeData
    } else if (longitudeData < 0) {
        longitudeData = 0 - longitudeData
    }
    return longitudeData
}

/**
 * 将纬度转换成不带前缀的数据
 * - 当数据大于90度或者小于0度表示南纬
 * @receiver [Double]
 * @return [Double] 返回去除"-"或小于90的数值；eg：输入91.1，返回1.1; 输入-1.1返回1.1;
 */
fun Double.toLatitudeNoPre(): Double {
    // 收到纬度数据"23.1531888",如果这个数小于等于90代表北纬，大于90代表南纬
    var latitudeData = this
    if (latitudeData > 90) {
        latitudeData = 180 - latitudeData
    } else if (latitudeData < 0) {
        latitudeData = 0 - latitudeData
    }
    return latitudeData
}

/**
 * 将经度添加东经标记转换成完整数据
 * - eg: 输入经度=110.1，isEast=false，isPositive=false，输出经度=-110.1
 * - eg: 输入经度=110.1，isEast=false，isPositive=true，输出经度=249.9
 * @receiver Double
 * @param isEast [Boolean] 是否为东经
 * @param isPositive [Boolean] 输出数据是否需要为正值
 * @return [Double] 输出经度
 */
fun Double.toFullLongitude(isEast: Boolean, isPositive: Boolean = true): Double {
   if (!this.isEastLongitude()) {
       // 当前输入的数据，是一个西经完整数据
       if (isPositive) {
           // 输出要求正值
           return if (this > 0) {
               // 输入数据是正值
               this
           } else {
               // 输入数据是负值
               360 + this
           }
       } else {
           // 输出要求负值
           return if (this > 0) {
               // 输入数据是正值
               this - 360
           } else {
               // 输入数据是负值
               this
           }
       }
   } else if (isEast) {
       // 当前输入的数据，是一个东经完整数据
       return this
   } else {
       // 当前输入的数据，是一个西经非完整数据
       return if (isPositive) {
           // 输出要求正值
           360 - this
       } else {
           // 输出要求负值
           - this
       }
   }
}

/**
 * 将纬度添加北纬标记转换成完整数据
 * - eg: 输入纬度=10.1，isNorth=false，isPositive=false，输出纬度=-10.1
 * - eg: 输入纬度=10.1，isNorth=false，isPositive=true，输出纬度=79.9
 * @receiver Double
 * @param isNorth [Boolean] 是否为北纬
 * @param isPositive [Boolean] 输出数据是否需要为正值
 * @return [Double] 输出纬度
 */
fun Double.toFullLatitude(isNorth: Boolean, isPositive: Boolean = true): Double {
    if (!this.isNorthLatitude()) {
        // 当前输入的数据，是一个南纬完整数据
        if (isPositive) {
            // 输出要求正值
            return if (this > 0) {
                // 输入数据是正值
                this
            } else {
                // 输入数据是负值
                90 + this
            }
        } else {
            // 输出要求负值
            return if (this > 0) {
                // 输入数据是正值
                this - 90
            } else {
                // 输入数据是负值
                this
            }
        }
    } else if (isNorth) {
        // 当前输入的数据，是一个北纬完整数据
        return this
    } else {
        // 当前输入的数据，是一个南纬非完整数据
        return if (isPositive) {
            // 输出要求正值
            90 - this
        } else {
            // 输出要求负值
            - this
        }
    }
}

/**
 * 将经度转换成显示数据
 * @receiver Double
 * @return String
 */
@Deprecated(
    message = "这个方法的名字不够准确，请使用另一个方法，更加准确，也更加通用",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("toLongitudeString(\"Fd°m′S.ss″\")")
)
fun Double.getShowLongitude(): String = toLongitudeString("Fd°m′S.ss″")

/**
 * 将纬度转换成显示数据
 * @receiver Double
 * @return String
 */
@Deprecated(
    message = "这个方法的名字不够准确，请使用另一个方法，更加准确，也更加通用",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("toLatitudeString(\"Fd°m′S.ss″\")")
)
fun Double.getShowLatitude(): String = toLatitudeString("Fd°m′S.ss″")

/**
 * 将经纬度转换为度分秒格式
 * @receiver [Double] 116.418847
 * @return [String] 116°25'7.85"
 */
@Deprecated(
    message = "这个方法的名字不够准确，请使用另一个方法，更加准确，也更加通用",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("this.toLatLngString(true, \"d°m′S.ss″\")")
)
fun Double.longlatitude2dfm(): String = this.toLatLngString(true, "d°m′S.ss″")

/**
 * 度分秒转经纬度
 * @receiver String 116°25'7.85"
 * @return Double 116.418847
 */
@Deprecated(
    message = "这个方法的名字不够准确，请使用另一个方法，更加准确，也更加通用",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("this.toLatOrLng(\"d°m′S.ss″\")"),
)
fun String.dfm2longlatitude(): Double = this.toLatOrLng("d°m′S.ss″")

/**
 * 将经纬度转经纬度字符串
 * - d: 度
 * - m: 分
 * - S: 秒的整数部分
 * - s: 秒的小数部分
 * - F: 英文方向，E、W、N、S
 * - CH: 中文方向，东经、西经、北纬、南纬
 * @receiver [Double] eg: 103.5863933
 * @param isLongitude [Boolean] 是否为经度
 * @param pattern [String] eg: "CH Fd°m′S.ss″"
 * @return [String] eg: 东经 E103°35′11.02″
 */
fun Double.toLatLngString(isLongitude: Boolean, pattern: String = "CHFd°m′S.ss″"): String {
    val value = if (isLongitude) toLongitudeNoPre() else toLatitudeNoPre()
    var string = pattern
    var reg = "CH".toRegex().findAll(string).toList()
    var faxiang = if (isLongitude) {
        if (isEastLongitude()) "东经" else "西经"
    } else {
        if (isNorthLatitude()) "北纬" else "南纬"
    }
    for (item in reg) {

        string = string.replaceRange(item.range, faxiang)
    }
    reg = "F".toRegex().findAll(string).toList()
    faxiang = if (isLongitude) {
        if (isEastLongitude()) "E" else "W"
    } else {
        if (isNorthLatitude()) "N" else "S"
    }
    for (item in reg) {
        string = string.replaceRange(item.range, faxiang)
    }
    reg = "d+".toRegex().findAll(string).toList()
    val dValue = value.toInt()
    for (item in reg) {
        string = if (item.value.length == 1) {
            string.replaceRange(item.range, dValue.toString())
        } else {
            string.replaceRange(item.range, dValue.toString().up2Length(item.value.length))
        }
    }
    reg = "m+".toRegex().findAll(string).toList()
    val m = (value - dValue) * 60.0
    val mValue = m.toInt()
    for (item in reg) {
        string = if (item.value.length == 1) {
            string.replaceRange(item.range, mValue.toString())
        } else {
            string.replaceRange(item.range, mValue.toString().up2Length(item.value.length))
        }
    }
    reg = "S+".toRegex().findAll(string).toList()
    val S = (m - mValue) * 60.0
    val SValue = S.toInt()
    for (item in reg) {
        string = if (item.value.length == 1) {
            string.replaceRange(item.range, SValue.toString())
        } else {
            string.replaceRange(item.range, SValue.toString().up2Length(item.value.length))
        }
    }
    reg = "s+".toRegex().findAll(string).toList()
    for (item in reg) {
        val sValue = ((S - SValue) * (10.0.pow(item.value.length))).roundToInt()
        string = string.replaceRange(item.range, sValue.toString().up2Length(item.value.length))
    }
    return string
}

/**
 * 将经度转经度字符串
 * - d: 度
 * - m: 分
 * - S: 秒的整数部分
 * - s: 秒的小数部分
 * - F: 英文方向，E、W
 * - CH: 中文方向，东经、西经
 * @receiver [Double] eg: 103.5863933
 * @param pattern [String] eg: "CH Fd°m′S.ss″"
 * @return [String] eg: 东经 E103°35′11.02″
 */
fun Double.toLongitudeString(pattern: String = "CHFd°m′S.ss″"): String = toLatLngString(true, pattern)

/**
 * 将纬度转纬度字符串
 * - d: 度
 * - m: 分
 * - S: 秒的整数部分
 * - s: 秒的小数部分
 * - F: 英文方向，N、S
 * - CH: 中文方向，北纬、南纬
 * @receiver [Double] eg: 29.73784595
 * @param pattern [String] eg: "CH Fd°m′S.ss″"
 * @return [String] eg: 北纬 N29°44′16.25″
 */
fun Double.toLatitudeString(pattern: String = "CHFd°m′S.ss″"): String = toLatLngString(false, pattern)

/**
 * 将经纬度字符串转经纬度
 * - d: 度
 * - m: 分
 * - S: 秒的整数部分
 * - s: 秒的小数部分
 * - F: 英文方向，E、W、N、S
 * - CH: 中文方向，东经、西经、北纬、南纬
 * @receiver [String] eg: 东经 E103°35′11.02″
 * @param pattern [String] eg: "CH Fd°m′S.ss″"
 * @return [Double] eg: 103.5863933
 */
fun String.toLatOrLng(pattern: String): Double {
    val string = "$this*"
    var index = 0
    var tempPattern = "$pattern*"
    while (index < string.length - 1) {
        val list = "[^dmSs]+".toRegex().findAll(tempPattern).toList()
        for (item in list) {
            if (item.range.first < index) continue
            if (item.value.contains("CH") || item.value.contains("F")) {
                index = item.range.last
                continue
            }
            val pos = string.indexOf(item.value)
            if (pos < item.range.first) {
                println("ERROR, pos=$pos, range.first=${item.range.first}")
                return 0.0
            }
            if (pos == item.range.first) {
                index = item.range.last
                continue
            } else {
                val lastStr = tempPattern.substring(item.range.first -1, item.range.first)
                tempPattern = tempPattern.replaceRange(
                    item.range.first -1, item.range.first,
                    lastStr.up2Length(pos - item.range.first + 1, lastStr.toCharArray()[0]))
                index = pos + item.value.length
                break
            }
        }
    }
    //println(tempPattern)
    var reg = "CH".toRegex().findAll(tempPattern).toList()
    var faxiang = if (reg.isNotEmpty()) string.substring(reg[0].range) else null
    reg = "F".toRegex().findAll(tempPattern).toList()
    faxiang = if (reg.isNotEmpty()) string.substring(reg[0].range) else faxiang
    val isLongitude = when(faxiang) {
        "东经", "西经", "E", "W" -> true
        else -> false
    }
    reg = "[d]+".toRegex().findAll(tempPattern).toList()
    var dStr = ""
    for (item in reg) {
        dStr += string.substring(item.range)
    }
    reg = "[m]+".toRegex().findAll(tempPattern).toList()
    var mStr = ""
    for (item in reg) {
        mStr += string.substring(item.range)
    }
    reg = "[S]+".toRegex().findAll(tempPattern).toList()
    var SStr = ""
    for (item in reg) {
        SStr += string.substring(item.range)
    }
    if (SStr.isEmpty()) SStr = "0"
    reg = "[s]+".toRegex().findAll(tempPattern).toList()
    var sStr = ""
    for (item in reg) {
        sStr += string.substring(item.range)
    }
    if (sStr.isEmpty()) sStr = "0"
    val s = "$SStr.$sStr".toDouble() / 60.0
    val m = (mStr.toInt() + s) / 60.0
    val d = dStr.toInt() + m
    return if (isLongitude) {
        val isEast = when(faxiang) {
            "东经", "E" -> true
            else -> false
        }
        d.toFullLongitude(isEast, false)
    } else {
        val isNorth = when(faxiang) {
            "北纬", "N" -> true
            else -> false
        }
        d.toFullLatitude(isNorth, false)
    }
}

/**
 * 将经度字符串转经度
 * - d: 度
 * - m: 分
 * - S: 秒的整数部分
 * - s: 秒的小数部分
 * - F: 英文方向，E、W
 * - CH: 中文方向，东经、西经
 * @receiver [String] eg: 东经 E103°35′11.02″
 * @param pattern [String] eg: "CH Fd°m′S.ss″"
 * @return [Double] eg: 103.5863933
 */
fun String.toLongitude(pattern: String): Double = "E$this".toLatOrLng("F$pattern")

/**
 * 将纬度字符串转纬度
 * - d: 度
 * - m: 分
 * - S: 秒的整数部分
 * - s: 秒的小数部分
 * - F: 英文方向，N、S
 * - CH: 中文方向，北纬、南纬
 * @receiver [String] eg: 北纬 N29°44′16.25″
 * @param pattern [String] eg: "CH Fd°m′S.ss″"
 * @return [Double] eg: 29.73784595
 */
fun String.toLatitude(pattern: String): Double = "N$this".toLatOrLng("F$pattern")

/**
 * 将ddmm.mmmm格式的经纬度转换成 真正的经纬度
 * - 北斗2.0协议里专用转换
 * @receiver [Double] eg: 11301.8789
 * @return [Double] eg: 113.03131
 */
fun Double.ddmmpmmmm2LatOrLng(): Double {
    val temp = this / 100
    val zheng = temp.toInt()
    val end = (temp - zheng) * 100.0 / 60
    return zheng + end
}

fun main() {
    val pattern = "CH Fd°m′S.ss″ "
    val string = 103.5863933.toLongitudeString(pattern)
    println(string)
    println(string.toLatOrLng(pattern))
    println(29.73784595.toLatitudeString(pattern))

    // 经纬度的坐标系转换
    println(DLatLng(29.7378, 103.5863).convert(CoordinateSystemType.WGS84, CoordinateSystemType.GCJ02))
    // DLatLng(latitude=29.734910753953383, longitude=103.58825934357566)
}