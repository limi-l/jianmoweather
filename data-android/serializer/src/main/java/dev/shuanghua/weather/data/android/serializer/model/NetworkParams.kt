package dev.shuanghua.weather.data.android.serializer.model

import dev.shuanghua.weather.data.android.model.params.CityListParams
import dev.shuanghua.weather.data.android.model.params.DistrictParams
import dev.shuanghua.weather.data.android.model.params.FavoriteCityParams
import dev.shuanghua.weather.data.android.model.params.SearchCityByKeywordsParams
import dev.shuanghua.weather.data.android.model.params.WeatherParams

/** 此模块 model 存放被序列化的模型  **/


fun WeatherParams.toMapParams(): MutableMap<String, Any> = mutableMapOf(
    "pcity" to cityName,
    "parea" to district,
    "lon" to lon,
    "lat" to lat,
    "obtid" to obtId,
    "uid" to "d6OIg9m36iZ4kri8sztq",
    "rainm" to "1"
)


fun DistrictParams.toMapParams(): MutableMap<String, Any> = mutableMapOf(
    "type" to type,
    "ver" to version,
    "rever" to rever,
    "net" to net,
    "pcity" to cityName,
    "parea" to district,
    "lon" to lon,
    "lat" to lat,
    "gif" to gif,
    "uid" to uid,
    "uname" to uname,
    "token" to token,
    "os" to os,
    "Param" to mutableMapOf(
        "cityid" to cityId,
        "obtid" to obtId,
        "lon" to lon,
        "lat" to lat
    )
)

fun FavoriteCityParams.toMapParams(): MutableMap<String, Any> = mutableMapOf(
    "type" to type,
    "ver" to version,
    "rever" to rever,
    "net" to net,

    "pcity" to cityName,
    "parea" to district,
    "lon" to lon,
    "lat" to lat,

    "gif" to gif,
    "uid" to uid,
    "uname" to uname,
    "token" to token,
    "os" to os,
    "Param" to mutableMapOf(
        "isauto" to isAuto,
        "cityids" to cityIds,
    )
)

//--------------------------------------------------------------------------------------------------

/**
 * 根据省份ID查询城市列表（省份列表只要一个完整的 URL即可， 无需其它参数）
 */
fun CityListParams.toMapParams(): MutableMap<String, Any> = mutableMapOf(
    "type" to type,
    "ver" to version,
    "rever" to rever,
    "net" to net,
    "pcity" to cityName,
    "parea" to district,
    "lon" to lon,
    "lat" to lat,
    "gif" to gif,
    "uid" to uid,
    "uname" to uname,
    "token" to token,
    "os" to os,
    "Param" to mutableMapOf(
        "provId" to provId,
        "cityids" to cityIds,
    )
)

fun SearchCityByKeywordsParams.toMapParams(): MutableMap<String, Any> = mutableMapOf(
    "type" to type,
    "ver" to version,
    "rever" to rever,
    "net" to net,
    "pcity" to cityName,
    "parea" to district,
    "lon" to lon,
    "lat" to lat,
    "gif" to gif,
    "uid" to uid,
    "uname" to uname,
    "token" to token,
    "os" to os,
    "Param" to mutableMapOf(
        "key" to keywords,
        "cityids" to cityIds,
    )
)


