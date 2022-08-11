package com.d10ng.latlnglib

import kotlin.math.pow

internal fun String.up2Length(
    length: Int,
    filler: Char = '0',
    isInStart: Boolean = true,
    isForced: Boolean = true
): String {
    val result = StringBuilder()
    if (!isInStart) result.append(this)
    if (this.length < length) {
        for (i in 0 until length - this.length) {
            result.append(filler)
        }
    }
    if (isInStart) result.append(this)
    return if (isForced) {
        if (isInStart) result.toString().substring(result.length - length)
        else result.toString().substring(0, length)
    } else result.toString()
}

/**
 * 保留指定数量的小数
 * @receiver Double
 * @param count Int
 * @return Double
 */
internal fun Double.keepDecimal(count: Int = 2): Double {
    val x = (10.0).pow(count)
    return (this * x).toLong().toDouble() / x
}