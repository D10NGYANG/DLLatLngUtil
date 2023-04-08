package com.d10ng.latlnglib.bean

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
data class DistanceAndBearing(

    /**
     * 距离
     */
    var distance: Double = 0.0,

    /**
     * 最开始算的方位角
     */
    var initialBearing: Double = 0.0,

    /**
     * 最终方位角
     */
    var finalBearing: Double = 0.0,
)
