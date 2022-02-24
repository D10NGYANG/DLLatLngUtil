# DLLatLngUtil
经纬度数据处理工具[![](https://jitpack.io/v/D10NGYANG/DLLatLngUtil.svg)](https://jitpack.io/#D10NGYANG/DLLatLngUtil)

## 使用
1 Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
2 Add the dependency
```gradle
dependencies {
    // 经纬度工具
    implementation 'com.github.D10NGYANG:DLLatLngUtil:1.3'
}
```
3 混淆
```properties
-keep class com.d10ng.latlnglib.** {*;}
-dontwarn com.d10ng.latlnglib.**
```
## 实例
```kotlin
/**
 * 经纬度的坐标系转换
 *
 * @receiver [DLatLng]
 * @param from [CoordinateSystemType] 输入坐标系
 * @param to [CoordinateSystemType] 输出坐标系
 * @return [DLatLng]
 */
fun DLatLng.convert(from: CoordinateSystemType, to: CoordinateSystemType): DLatLng

/**
 * 判断经度数据是否为东经
 * @receiver [Double]
 * @return [Boolean] true: 东经；false: 西经
 */
fun Double.isEastLongitude(): Boolean

/**
 * 判断纬度数据是否为北纬
 * @receiver [Double]
 * @return [Boolean] true: 北纬；false: 南纬
 */
fun Double.isNorthLatitude(): Boolean

/**
 * 将经度转换成不带前缀的数据
 * - 当数据大于180度或者小于0度表示西经
 * @receiver [Double]
 * @return [Double] 返回去除"-"或小于180的数值；eg：输入181.1，返回1.1; 输入-1.1返回1.1;
 */
fun Double.toLongitudeNoPre(): Double

/**
 * 将纬度转换成不带前缀的数据
 * - 当数据大于90度或者小于0度表示南纬
 * @receiver [Double]
 * @return [Double] 返回去除"-"或小于90的数值；eg：输入91.1，返回1.1; 输入-1.1返回1.1;
 */
fun Double.toLatitudeNoPre(): Double

/**
 * 将经度添加东经标记转换成完整数据
 * - eg: 输入经度=110.1，isEast=false，isPositive=false，输出经度=-110.1
 * - eg: 输入经度=110.1，isEast=false，isPositive=true，输出经度=249.9
 * @receiver Double
 * @param isEast [Boolean] 是否为东经
 * @param isPositive [Boolean] 输出数据是否需要为正值
 * @return [Double] 输出经度
 */
fun Double.toFullLongitude(isEast: Boolean, isPositive: Boolean = true): Double

/**
 * 将纬度添加北纬标记转换成完整数据
 * - eg: 输入纬度=10.1，isNorth=false，isPositive=false，输出纬度=-10.1
 * - eg: 输入纬度=10.1，isNorth=false，isPositive=true，输出纬度=79.9
 * @receiver Double
 * @param isNorth [Boolean] 是否为北纬
 * @param isPositive [Boolean] 输出数据是否需要为正值
 * @return [Double] 输出纬度
 */
fun Double.toFullLatitude(isNorth: Boolean, isPositive: Boolean = true): Double

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
fun Double.toLatLngString(isLongitude: Boolean, pattern: String = "CHFd°m′S.ss″"): String

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
fun Double.toLongitudeString(pattern: String = "CHFd°m′S.ss″"): String

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
fun Double.toLatitudeString(pattern: String = "CHFd°m′S.ss″"): String

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
fun String.toLatOrLng(pattern: String): Double

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
fun String.toLongitude(pattern: String): Double

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
fun String.toLatitude(pattern: String): Double

/**
 * 将ddmm.mmmm格式的经纬度转换成 真正的经纬度
 * - 北斗2.0协议里专用转换
 * @receiver [Double] eg: 11301.8789
 * @return [Double] eg: 113.03131
 */
fun Double.ddmmpmmmm2LatOrLng(): Double

/**
 * 将真正的经纬度转换成 ddmm.mmmm格式的经纬度
 * - 北斗2.0协议里专用转换
 * @receiver [Double] eg: 113.03131
 * @return [Double] eg: 11301.8789
 */
fun Double.latLng2ddmmpmmmm(): Double
```