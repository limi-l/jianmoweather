package dev.shuanghua.weather.data.android.repository

import dev.shuanghua.weather.data.android.database.dao.FavoriteDao
import dev.shuanghua.weather.data.android.database.entity.FavoriteCityIdEntity
import dev.shuanghua.weather.data.android.database.entity.WeatherParamsEntity
import dev.shuanghua.weather.data.android.model.FavoriteCity
import dev.shuanghua.weather.data.android.model.FavoriteStation
import dev.shuanghua.weather.data.android.model.Weather
import dev.shuanghua.weather.data.android.model.params.WeatherParams
import dev.shuanghua.weather.data.android.network.NetworkDataSource
import dev.shuanghua.weather.data.android.network.model.ShenZhenFavoriteCityWeather
import dev.shuanghua.weather.data.android.repository.convert.asEntity
import dev.shuanghua.weather.data.android.repository.convert.asExternalModel
import dev.shuanghua.weather.data.android.repository.convert.asFavoriteStation
import dev.shuanghua.weather.shared.AppCoroutineDispatchers
import dev.shuanghua.weather.shared.asArrayList
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoritesRepository @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val networkDataSource: NetworkDataSource,
    private val dispatchers: AppCoroutineDispatchers
) {

    /**
     * concurrency request multiple stations weather
     * 并发请求多个站点天气数据
     */
    suspend fun getFavoritesStationsList(
        paramsList: List<String>
    ): List<FavoriteStation> = withContext(dispatchers.io) {
        val networkDeferred: List<Deferred<FavoriteStation>> = paramsList.map { it: String ->
            async { networkDataSource.getMainWeather(it).asFavoriteStation() }
        }

        networkDeferred.awaitAll()// 并发
    }


    suspend fun getFavoritesWeather(params: String): Weather =
        withContext(dispatchers.io) {
            networkDataSource.getMainWeather(params).asExternalModel()
        }


    fun observerFavoriteStationParams(): Flow<List<WeatherParams>> {
        return favoriteDao.observerFavoriteStationParam().map {
            it.map(WeatherParamsEntity::asExternalModel)
        }
    }

    suspend fun saveStationParam(
        weatherParams: WeatherParams,
        stationName: String
    ) = withContext(dispatchers.io) {
        val weatherParamsEntity = weatherParams.asEntity(stationName)
        favoriteDao.insertStationParam(weatherParamsEntity)
    }

    suspend fun deleteStationParam(stationName: String) =
        withContext(dispatchers.io) {
            favoriteDao.deleteStationWeatherParam(stationName)
        }

    suspend fun getStationParamsByName(
        stationName: String
    ): WeatherParams {
        return favoriteDao.getStationWeatherParams(stationName).asExternalModel()
    }

//-------------------------------------------------------------------------------------------//

    fun observerCityIds(): Flow<ArrayList<String>> =
        favoriteDao.observerCityId().map { idList ->
            idList.asArrayList()
        }

    suspend fun getFavoriteCityWeather(params: String): List<FavoriteCity> =
        networkDataSource.getFavoriteCityWeatherList(params)
            .map(ShenZhenFavoriteCityWeather::asExternalModel)

    suspend fun deleteCity(cityId: String) =
        withContext(dispatchers.io) {
            favoriteDao.deleteCity(cityId)
        }

    suspend fun saveFavoriteCity(cityId: String) =
        withContext(dispatchers.io) {
            val favoriteCityIdEntity = FavoriteCityIdEntity(id = cityId)
            favoriteDao.insertCityId(favoriteCityIdEntity)
        }
}