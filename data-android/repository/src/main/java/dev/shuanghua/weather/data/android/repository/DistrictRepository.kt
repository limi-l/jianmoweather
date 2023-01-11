package dev.shuanghua.weather.data.android.repository

import dev.shuanghua.weather.data.android.database.dao.DistrictDao
import dev.shuanghua.weather.data.android.database.dao.StationDao
import dev.shuanghua.weather.data.android.database.entity.DistrictEntity
import dev.shuanghua.weather.data.android.database.entity.StationEntity
import dev.shuanghua.weather.data.android.database.entity.asExternalModel
import dev.shuanghua.weather.data.android.model.District
import dev.shuanghua.weather.data.android.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class DistrictRepository @Inject constructor(
    private val network: NetworkDataSource,
    private val districtDao: DistrictDao,
    private val stationDao: StationDao
) {
    /**
     * 因为站点的数据不经常变动，建议在首次安装APP时调用，同时提供手动刷新操作
     */
    suspend fun updateStationList(param: String) {
        val districts = network.getDistrictWithStationList(param)
        if (districts.isNullOrEmpty()) throw Exception("服务器数据为空！")
        val districtList = ArrayList<DistrictEntity>()
        val stationList = ArrayList<StationEntity>()

        districts.forEach { district ->
            districtList.add(DistrictEntity(district.name))
            district.list.forEach {
                stationList.add(
                    StationEntity(
                        districtName = district.name,
                        stationId = it.stationId,
                        stationName = it.stationName
                    )
                )
            }
        }
        Timber.d("--->>$districtList")
        Timber.d("--->>$stationList")
        districtDao.insertDistricts(districtList)
        stationDao.insertStations(stationList)
    }

    fun observerDistricts(): Flow<List<District>> =
        districtDao.observerDistricts().map { districtList ->
            districtList.map(DistrictEntity::asExternalModel)

        }
}