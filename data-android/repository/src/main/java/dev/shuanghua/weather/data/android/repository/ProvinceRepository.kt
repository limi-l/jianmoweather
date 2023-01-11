package dev.shuanghua.weather.data.android.repository

import dev.shuanghua.weather.data.android.database.dao.ProvinceDao
import dev.shuanghua.weather.data.android.model.Province
import dev.shuanghua.weather.data.android.network.NetworkDataSource
import dev.shuanghua.weather.data.android.network.api.ShenZhenRetrofitApi
import dev.shuanghua.weather.data.android.network.model.ShenZhenProvince
import dev.shuanghua.weather.data.android.repository.convert.asEntity
import dev.shuanghua.weather.data.android.repository.convert.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProvinceRepository @Inject constructor(
    private val provinceDao: ProvinceDao,
    private val network: NetworkDataSource
) {

    suspend fun updateProvince() {
        val provinceList: List<ShenZhenProvince> = network.getProvinces() ?: return
        provinceDao.insertProvince(provinceList.map { it.asEntity() })
    }

    fun observerProvinces(): Flow<List<Province>> =
        provinceDao.observerProvinces().map { provinceEntityList ->
            provinceEntityList.map { it.asExternalModel() }
        }

//    companion object {
//        @Volatile
//        private var INSTANCE: ProvinceRepository? = null
//
//        fun getInstance(
//            provinceDao: ProvinceDao,
//            service: ShenZhenRetrofitApi
//        ): ProvinceRepository {
//            return INSTANCE
//                ?: synchronized(this) {
//                    INSTANCE
//                        ?: ProvinceRepository(
//                            provinceDao,
//                            service
//                        ).also { INSTANCE = it }
//                }
//        }
//    }
}
