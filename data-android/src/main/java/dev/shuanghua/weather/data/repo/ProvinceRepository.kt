package dev.shuanghua.weather.data.repo

import dev.shuanghua.weather.data.db.dao.ProvinceDao
import dev.shuanghua.weather.data.db.entity.Province
import dev.shuanghua.weather.data.model.ProvinceReturn
import dev.shuanghua.weather.data.network.ShenZhenWeatherApi
import kotlinx.coroutines.flow.Flow

class ProvinceRepository(
    private val provinceDao: ProvinceDao,
    private val service: ShenZhenWeatherApi
) {

    suspend fun updateProvince() {
        val remoteProvince: ProvinceReturn = service.getProvinces().body()?.data ?: return
        provinceDao.insertProvince(remoteProvince.list)
    }

    fun observerProvinces(): Flow<List<Province>> = provinceDao.observerProvinces()

    companion object {
        @Volatile
        private var INSTANCE: ProvinceRepository? = null

        fun getInstance(
            provinceDao: ProvinceDao,
            service: ShenZhenWeatherApi
        ): ProvinceRepository {
            return INSTANCE
                ?: synchronized(this) {
                    INSTANCE
                        ?: ProvinceRepository(
                            provinceDao,
                            service
                        ).also { INSTANCE = it }
                }
        }
    }
}

