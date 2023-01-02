package dev.shuanghua.weather.data.model

data class WeatherResource(
    val screen: String,
    val cityId: String,
    val cityName: String,
    val temperature: String,
    val description: String,
    val airQuality: String,
    val lunarCalendar: String,
    val stationName: String,
    val stationId: String,
    val locationStationId: String,
    val sunUp: String,
    val sunDown: String,
    val alarmIcons: List<AlarmIcon>,
    val oneDays: List<OneDay>,
    val oneHours: List<OneHour>,
    val conditions: List<Condition>,
    val exponents: List<Exponent>,
)

val emptyWeatherResource = WeatherResource(
    screen = "",
    cityId = "",
    cityName = "",
    temperature = "",
    description = "",
    airQuality = "",
    lunarCalendar = "",
    stationName = "",
    stationId = "",
    locationStationId = "",
    sunUp = "",
    sunDown = "",
    alarmIcons = emptyList(),
    oneDays = emptyList(),
    oneHours = emptyList(),
    conditions = emptyList(),
    exponents = emptyList(),
)

val previewWeatherResource = WeatherResource(
    screen = "",
    cityId = "28060159493",
    cityName = "深圳(定位)",
    temperature = "26°C",
    description = "多云",
    airQuality = "26·优",
    lunarCalendar = "2022年3月12日 农历二月初十 距春分还有8天",
    stationName = "香蜜湖街道",
    stationId = "",
    locationStationId = "G3501",
    sunUp = "06:36",
    sunDown = "18:32",
    alarmIcons = emptyList(),
    oneDays = previewOnDay,
    oneHours = previewOneHour,
    conditions = previewCondition,
    exponents = previewExponent,
)