package dev.shuanghua.weather.data.android.domain.usecase

import dev.shuanghua.weather.data.android.model.FavoriteStation
import dev.shuanghua.weather.data.android.model.params.WeatherParams
import dev.shuanghua.weather.data.android.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


/**
 * 返回三种结果：
 * 1：成功     网络请求成功
 * 2：错误     网络请求失败
 * 3：空集合   数据库为空时
 */
class GetFavoriteStationWeatherUseCase @Inject constructor(
	private val favoriteRepository: FavoritesRepository,
) {
	suspend operator fun invoke(): Flow<List<FavoriteStation>> = favoriteRepository
		.observerFavoriteStationParams()
		.map { getStationNetworkWeather(it) }


	private suspend fun getStationNetworkWeather(
		paramsList: List<WeatherParams>
	): List<FavoriteStation> =
		if (paramsList.isEmpty()) {
			emptyList()
		} else {
			favoriteRepository.getFavoritesStationsList(paramsList)
		}
}