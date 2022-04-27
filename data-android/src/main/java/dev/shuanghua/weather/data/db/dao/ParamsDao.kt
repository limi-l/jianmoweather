package dev.shuanghua.weather.data.db.dao

import androidx.room.*
import dev.shuanghua.weather.data.db.entity.OuterParam
import dev.shuanghua.weather.data.db.entity.WeatherParam
import dev.shuanghua.weather.data.db.pojo.LastRequestParams

@Dao
abstract class ParamsDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertLocationCityWeatherRequestParam(
        outerParam: OuterParam,
        innerParam: WeatherParam
    )

    @Transaction
    @Query("SELECT * FROM param_outer")
    abstract suspend fun getOuterParam(): OuterParam

    @Transaction
    @Query("SELECT * FROM param_weather")
    abstract suspend fun getMainWeatherParam(): WeatherParam

    suspend fun getLastLocationCityWeatherRequestParam(): LastRequestParams {
        val outerParam = getOuterParam()
        val innerParam = getMainWeatherParam()
        return LastRequestParams(lastOuterParam = outerParam, lastMainWeatherParam = innerParam)
    }
}