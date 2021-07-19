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
    implementation 'com.github.D10NGYANG:DLLatLngUtil:1.0'
}
```
3 混淆
```properties
-keep class com.d10ng.latlnglib.** {*;}
-dontwarn com.d10ng.latlnglib.**
```
