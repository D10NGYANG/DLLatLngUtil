package com.d10ng.latlnglib

import com.d10ng.latlnglib.bean.DLatLng
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class TestCalculate {

    private val array = arrayOf(
        DLatLng(31.695557, 119.801582),
        DLatLng(31.695551, 119.803146),
        DLatLng(31.695505, 119.804802),
        DLatLng(31.695389, 119.80687),
        DLatLng(31.695164, 119.809357),
        DLatLng(31.694862, 119.811691),
        DLatLng(31.694714, 119.812836),
        DLatLng(31.694466, 119.814781),
        DLatLng(31.69426, 119.816429),
        DLatLng(31.693727, 119.820694),
        DLatLng(31.693655, 119.821228),
        DLatLng(31.693396, 119.823349),
        DLatLng(31.693136, 119.825394),
        DLatLng(31.692776, 119.828079),
        DLatLng(31.692181, 119.832993),
        DLatLng(31.69212, 119.833473),
        DLatLng(31.691328, 119.83979),
        DLatLng(31.690964, 119.842529),
        DLatLng(31.690878, 119.843193),
        DLatLng(31.690777, 119.8442),
        DLatLng(31.690569, 119.845833),
        DLatLng(31.690521, 119.846222),
        DLatLng(31.690147, 119.849258),
        DLatLng(31.690086, 119.849747),
        DLatLng(31.689503, 119.854424),
        DLatLng(31.689449, 119.854828),
        DLatLng(31.689032, 119.857964),
        DLatLng(31.688921, 119.858856),
        DLatLng(31.688625, 119.861557),
        DLatLng(31.688572, 119.861946),
        DLatLng(31.688313, 119.864021),
        DLatLng(31.688019, 119.865517),
        DLatLng(31.687788, 119.866447),
        DLatLng(31.68754, 119.867256),
        DLatLng(31.687105, 119.868484),
        DLatLng(31.686522, 119.869797),
        DLatLng(31.686014, 119.870781),
        DLatLng(31.685534, 119.87159),
        DLatLng(31.684927, 119.872459),
        DLatLng(31.684626, 119.872894),
        DLatLng(31.684296, 119.873322),
        DLatLng(31.683853, 119.873848),
        DLatLng(31.683176, 119.874588),
        DLatLng(31.682295, 119.875443),
        DLatLng(31.680983, 119.87645),
        DLatLng(31.68083, 119.87645),
        DLatLng(31.680748, 119.87635),
        DLatLng(31.680672, 119.875511),
        DLatLng(31.680576, 119.87487),
        DLatLng(31.680256, 119.873962),
        DLatLng(31.679773, 119.873169),
        DLatLng(31.679199, 119.872559),
        DLatLng(31.678858, 119.872269),
        DLatLng(31.678705, 119.87233),
        DLatLng(31.677755, 119.873138),
        DLatLng(31.677727, 119.87326),
        DLatLng(31.678112, 119.874954),
        DLatLng(31.67808, 119.875275),
        DLatLng(31.677694, 119.87558),
        DLatLng(31.675388, 119.876984),
        DLatLng(31.675068, 119.877022),
        DLatLng(31.674646, 119.875954),
        DLatLng(31.674368, 119.874725),
        DLatLng(31.674334, 119.871819),
        DLatLng(31.674282, 119.871544),
        DLatLng(31.67366, 119.871574),
        DLatLng(31.673231, 119.871712),
        DLatLng(31.672728, 119.872093),
        DLatLng(31.672403, 119.872528),
        DLatLng(31.672344, 119.872482),
        DLatLng(31.672316, 119.872437),
        DLatLng(31.672686, 119.872002),
        DLatLng(31.673096, 119.871689),
        DLatLng(31.673578, 119.87149),
        DLatLng(31.674301, 119.871422),
        DLatLng(31.674343, 119.871376),
        DLatLng(31.674671, 119.868324),
        DLatLng(31.67474, 119.868202),
        DLatLng(31.676901, 119.868347),
        DLatLng(31.677696, 119.868271),
        DLatLng(31.678326, 119.86808),
        DLatLng(31.679003, 119.867767),
        DLatLng(31.679762, 119.86734),
        DLatLng(31.681446, 119.866447),
        DLatLng(31.681461, 119.866295),
        DLatLng(31.681471, 119.866005),
        DLatLng(31.681532, 119.865829),
        DLatLng(31.681852, 119.865501),
        DLatLng(31.681967, 119.865173),
        DLatLng(31.682037, 119.864738),
        DLatLng(31.682148, 119.863853),
        DLatLng(31.682459, 119.861443),
        DLatLng(31.682631, 119.860176),
        DLatLng(31.682795, 119.858871),
        DLatLng(31.682981, 119.858345),
        DLatLng(31.683365, 119.85788),
        DLatLng(31.683708, 119.857567),
        DLatLng(31.68401, 119.85701),
        DLatLng(31.68416, 119.856567),
        DLatLng(31.684214, 119.855927),
        DLatLng(31.684114, 119.855309),
        DLatLng(31.684011, 119.854942),
        DLatLng(31.683805, 119.854095),
        DLatLng(31.683701, 119.853226),
        DLatLng(31.683681, 119.852249),
        DLatLng(31.683786, 119.85125),
        DLatLng(31.683882, 119.850197),
        DLatLng(31.684202, 119.84761),
        DLatLng(31.684454, 119.845642),
        DLatLng(31.684649, 119.844147),
        DLatLng(31.684748, 119.843498),
        DLatLng(31.684826, 119.843452),
        DLatLng(31.684954, 119.84343),
        DLatLng(31.685577, 119.843414),
        DLatLng(31.685894, 119.843307),
        DLatLng(31.686234, 119.843124),
        DLatLng(31.686808, 119.842697),
        DLatLng(31.687094, 119.842239),
        DLatLng(31.687401, 119.842041),
        DLatLng(31.687622, 119.842033),
        DLatLng(31.687874, 119.842072),
        DLatLng(31.687984, 119.841896),
        DLatLng(31.687983, 119.841652),
        DLatLng(31.688204, 119.840111),
        DLatLng(31.688496, 119.839676),
        DLatLng(31.688885, 119.839554),
        DLatLng(31.688929, 119.839066),
        DLatLng(31.688818, 119.837868),
        DLatLng(31.688662, 119.837128),
        DLatLng(31.688414, 119.836304),
        DLatLng(31.688261, 119.835945),
        DLatLng(31.687773, 119.834976),
        DLatLng(31.687422, 119.83403),
        DLatLng(31.687126, 119.832954),
        DLatLng(31.68697, 119.831711),
        DLatLng(31.687008, 119.83017),
        DLatLng(31.687181, 119.829147),
        DLatLng(31.687584, 119.827606),
        DLatLng(31.688328, 119.824776),
        DLatLng(31.68894, 119.822525),
        DLatLng(31.689344, 119.820908),
        DLatLng(31.689644, 119.819489),
        DLatLng(31.689667, 119.818619),
        DLatLng(31.689508, 119.817924),
        DLatLng(31.689087, 119.816719),
        DLatLng(31.688955, 119.816002),
        DLatLng(31.688946, 119.814575),
        DLatLng(31.689135, 119.813538),
        DLatLng(31.689543, 119.812546),
        DLatLng(31.689749, 119.81208),
        DLatLng(31.689808, 119.811386),
        DLatLng(31.689711, 119.810394),
        DLatLng(31.689617, 119.809853),
        DLatLng(31.68936, 119.808922),
        DLatLng(31.68911, 119.807922),
        DLatLng(31.688871, 119.806946),
        DLatLng(31.688631, 119.806168),
        DLatLng(31.688162, 119.805069),
        DLatLng(31.687551, 119.804008),
        DLatLng(31.686855, 119.803108),
        DLatLng(31.686115, 119.802383),
        DLatLng(31.685247, 119.80172),
        DLatLng(31.684469, 119.80127),
        DLatLng(31.683582, 119.800903),
        DLatLng(31.681662, 119.800453),
        DLatLng(31.680269, 119.800102),
        DLatLng(31.679163, 119.79985),
        DLatLng(31.678406, 119.799675),
        DLatLng(31.677479, 119.799339),
        DLatLng(31.677221, 119.799202),
        DLatLng(31.677313, 119.798538),
        DLatLng(31.677513, 119.797478),
        DLatLng(31.677776, 119.797447),
        DLatLng(31.678391, 119.797462),
        DLatLng(31.678871, 119.797394),
        DLatLng(31.679581, 119.797218),
        DLatLng(31.680002, 119.797081),
        DLatLng(31.680508, 119.797073),
        DLatLng(31.680933, 119.797195),
        DLatLng(31.681303, 119.797348),
        DLatLng(31.681662, 119.797623),
        DLatLng(31.681948, 119.797935),
        DLatLng(31.682287, 119.798386),
        DLatLng(31.682648, 119.798805),
        DLatLng(31.683073, 119.799141),
        DLatLng(31.683725, 119.7994),
        DLatLng(31.684454, 119.799461),
        DLatLng(31.685244, 119.799194),
        DLatLng(31.686214, 119.798676),
        DLatLng(31.687273, 119.798088),
        DLatLng(31.687805, 119.79789),
        DLatLng(31.688354, 119.797813),
        DLatLng(31.689064, 119.797844),
        DLatLng(31.689987, 119.798134),
        DLatLng(31.69099, 119.798553),
        DLatLng(31.691544, 119.798714),
        DLatLng(31.695417, 119.798798),
        DLatLng(31.695536, 119.801598)
    )


    @Test
    fun testPointsOnDistance() {
        val total = getTotalDistance(array)
        println(total)
        val list = getPointsOnDistance(array, 10.0)
        println(list.size)
    }


    @Test
    fun testPointByPresent() {
        val start = array[0]
        val end = array[6]
        printlnPoint(start)
        val angle = getAngleOn2Points(start, end)
        val distance = getDistanceOn2Points(start, end)
        for (i in 1 .. 9) {
            ///println(i)
            val distance3 = distance * i / 10.0f
            val point = getPointByBasePoint(start, distance3, angle)
            printlnPoint(point)
            val angle1 = getAngleOn2Points(start, point)
            println(angle - angle1)
            //assertTrue(abs(angle - angle1) < 0.01)
            val distance1 = getDistanceOn2Points(start, point)
            println(distance1 - distance3)
            //assertTrue(abs(distance1 - distance3) < 0.01)
        }
        printlnPoint(end)
    }

    fun printlnPoint(point: DLatLng) {
        println("points.push(new T.LngLat(${point.longitude}, ${point.latitude}));")
    }
}