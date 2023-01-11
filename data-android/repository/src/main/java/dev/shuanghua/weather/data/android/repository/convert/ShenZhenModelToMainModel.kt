package dev.shuanghua.weather.data.android.repository.convert

import dev.shuanghua.weather.data.android.model.AlarmIcon
import dev.shuanghua.weather.data.android.model.Condition
import dev.shuanghua.weather.data.android.model.Exponent
import dev.shuanghua.weather.data.android.model.OneDay
import dev.shuanghua.weather.data.android.model.OneHour
import dev.shuanghua.weather.data.android.model.Weather
import dev.shuanghua.weather.data.android.network.api.ShenZhenRetrofitApi
import dev.shuanghua.weather.data.android.network.model.ShenZhenWeather

/**
 * 直接转成 Ui 可用模型
 */
fun ShenZhenWeather.asWeather(): Weather {
    val desc = cleanTodayDescribe()
    val airQuality = cleanAirQuality()
    val lunarCalendar = cleanCalendar()
    val (sunUp, sunDown) = cleanSunTime()

    return Weather(
        cityId = cityid,
        cityName = cityName,
        temperature = t,
        stationName = stationName,
        stationId = obtidyb,
        locationStationId = autoObtid,
        description = desc,
        lunarCalendar = lunarCalendar,
        sunUp = sunUp,
        sunDown = sunDown,
        airQuality = airQuality,
        alarmIcons = asAlarmIconList(),
        oneDays = asOneDayList(),
        oneHours = asOneHourList(),
        conditions = mapToConditionList(),
        exponents = mapToExponentList()
    )
}

fun ShenZhenWeather.asAlarmIconList(): List<AlarmIcon> {
    var alarmsIconUrl = ""
    return alarmList.mapIndexed { index, alarm ->
        if (alarm.icon != "") {
            alarmsIconUrl = ShenZhenRetrofitApi.ICON_HOST + alarm.icon
        }
        AlarmIcon(
            id = index,
            cityId = cityid,
            iconUrl = alarmsIconUrl,
            name = alarm.name
        )
    }
}

fun ShenZhenWeather.asOneHourList(): List<OneHour> {
    return hourForeList.mapIndexed { index, oneHour ->
        OneHour(
            id = index,
            cityId = cityid,
            hour = oneHour.hour,
            t = oneHour.t,
            icon = oneHour.weatherpic,
            rain = oneHour.rain
        )
    }
}

fun ShenZhenWeather.asOneDayList(): List<OneDay> {
    return dayList.mapIndexed { index, oneDay ->
        OneDay(
            id = index,
            cityId = cityid,
            date = oneDay.date,
            week = oneDay.week,
            desc = oneDay.desc,
            t = "${oneDay.minT}~${oneDay.maxT}",
            minT = oneDay.minT,
            maxT = oneDay.maxT,
            iconName = oneDay.wtype
        )
    }
}

fun ShenZhenWeather.mapToConditionList(): List<Condition> {
    val conditions = ArrayList<Condition>()
    if (rh.isNotEmpty()) {
        val rhItem = Condition(
            id = 0,
            cityId = cityid,
            name = "湿度",
            value = rh
        )
        conditions.add(rhItem)
    }
    if (pa.isNotEmpty()) {
        val hPaItem = Condition(
            id = 1,
            cityId = cityid,
            name = "气压",
            value = pa
        )
        conditions.add(hPaItem)
    }
    if (wwCN.isNotEmpty()) {//东风
        val windItem = Condition(
            id = 2,
            cityId = cityid,
            name = wwCN,
            value = wf
        )
        conditions.add(windItem)
    }
    if (r24h.isNotEmpty()) {
        val r24hItem = Condition(
            id = 3,
            cityId = cityid,
            name = "24H降雨量",
            value = r24h
        )
        conditions.add(r24hItem)
    }
    if (r01h.isNotEmpty()) {
        val r01hItem = Condition(
            id = 4,
            cityId = cityid,
            name = "1H降雨量",
            value = r01h
        )
        conditions.add(r01hItem)
    }
    if (v.isNotEmpty()) {
        val visibilityItem = Condition(
            id = 5,
            cityId = cityid,
            name = "能见度",
            value = v
        )
        conditions.add(visibilityItem)
    }
    return conditions
}

fun ShenZhenWeather.mapToExponentList(): List<Exponent> {
    val healthExponents = ArrayList<Exponent>()
    jkzs?.apply {
        shushidu.apply {
            val healthExponent = Exponent(
                id = 0,
                cityId = cityid,
                title = title,
                level = level,
                levelDesc = level_desc,
                levelAdvice = level_advice
            )
            healthExponents.add(healthExponent)
        }
        gaowen.apply {
            val healthExponent = Exponent(
                id = 1,
                cityId = cityid,
                title = title,
                level = level,
                levelDesc = level_desc,
                levelAdvice = level_advice
            )
            healthExponents.add(healthExponent)
        }
        ziwaixian.apply {
            val healthExponent = Exponent(
                id = 2,
                cityId = cityid,
                title = title,
                level = level,
                levelDesc = level_desc,
                levelAdvice = level_advice
            )
            healthExponents.add(healthExponent)
        }
        co.apply {
            val healthExponent = Exponent(
                id = 3,
                cityId = cityid,
                title = title,
                level = level,
                levelDesc = level_desc,
                levelAdvice = level_advice
            )
            healthExponents.add(healthExponent)
        }
        meibian.apply {
            val healthExponent = Exponent(
                id = 4,
                cityId = cityid,
                title = title,
                level = level,
                levelDesc = level_desc,
                levelAdvice = level_advice
            )
            healthExponents.add(healthExponent)
        }
        chenlian.apply {
            val healthExponent = Exponent(
                id = 5,
                cityId = cityid,
                title = title,
                level = level,
                levelDesc = level_desc,
                levelAdvice = level_advice
            )
            healthExponents.add(healthExponent)
        }
        luyou.apply {
            val healthExponent = Exponent(
                id = 6,
                cityId = cityid,
                title = title,
                level = level,
                levelDesc = level_desc,
                levelAdvice = level_advice
            )
            healthExponents.add(healthExponent)
        }
        liugan.apply {
            val healthExponent = Exponent(
                id = 7,
                cityId = cityid,
                title = title,
                level = level,
                levelDesc = level_desc,
                levelAdvice = level_advice
            )
            healthExponents.add(healthExponent)
        }
        chuanyi.apply {
            val exponent = Exponent(
                id = 8,
                cityId = cityid,
                title = title,
                level = level,
                levelDesc = level_desc,
                levelAdvice = level_advice
            )
            healthExponents.add(exponent)
        }
    }
    return healthExponents
}