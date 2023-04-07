@file:OptIn(ExperimentalJsExport::class)
@file:JsExport
package com.d10ng.latlnglib

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.math.floor

// 颜色字符串正则表达式
private val colorRegex = "^#([0-9a-fA-F]{6}|[0-9a-fA-F]{3})$".toRegex()

/**
 * 从两个颜色间根据比例获得中间颜色
 * @param color1 String 16进制颜色值，如#000000、#000
 * @param color2 String 16进制颜色值，如#000000、#000
 * @param present Float 比例，0-1
 * @return String 16进制颜色值，如#000000、#000
 */
fun getPresentColorWithRange(color1: String, color2: String, present: Float): String {
    val startColorNumbers = getRgbValueArrayFromHexColorString(color1)
    val endColorNumbers = getRgbValueArrayFromHexColorString(color2)
    val redRange = endColorNumbers[0] - startColorNumbers[0]
    val greenRange = endColorNumbers[1] - startColorNumbers[1]
    val blueRange = endColorNumbers[2] - startColorNumbers[2]
    val red = floor(redRange * present) + startColorNumbers[0]
    val green = floor(greenRange * present) + startColorNumbers[1]
    val blue = floor(blueRange * present) + startColorNumbers[2]
    return getHexColorStringFromRgbValueArray(arrayOf(red.toInt(), green.toInt(), blue.toInt()))
}

/**
 * 从16进制颜色值中提取红绿蓝的值
 * @param str String 16进制颜色值，如#000000、#000
 * @return Array<Int> [red, green, blue]，如果不是16进制颜色值则返回[0, 0, 0]，每个颜色值范围为0-255
 */
fun getRgbValueArrayFromHexColorString(str: String): Array<Int> {
    if (!colorRegex.matches(str)) return arrayOf(0, 0, 0)
    var hexColor = str.lowercase()
    if (str.length == 4) {
        var t = "#"
        val chars = hexColor.toCharArray()
        for (i in 1 until chars.size) {
            t += chars[i].toString() + chars[i].toString()
        }
        hexColor = t
    }
    val arr = mutableListOf<Int>()
    for (i in 1 until hexColor.length step 2) {
        val s = hexColor.slice(i until i + 2)
        arr.add(s.toUInt(16).toInt())
    }
    return arr.toTypedArray()
}

/**
 * 从红绿蓝的值获取16进制颜色值
 * @param rgb Array<Int> [red, green, blue]，每个颜色值范围为0-255
 * @return String 16进制颜色值，如#000000
 */
fun getHexColorStringFromRgbValueArray(rgb: Array<Int>): String {
    if (rgb.size != 3) return "#000000"
    val getHex = { num: Int ->
        num.toString(16).up2Length(2)
    }
    return "#${getHex(rgb[0])}${getHex(rgb[1])}${getHex(rgb[2])}"
}
