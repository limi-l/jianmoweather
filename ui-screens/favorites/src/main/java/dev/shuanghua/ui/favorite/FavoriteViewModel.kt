package dev.shuanghua.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shuanghua.weather.data.db.entity.FavoriteCityWeather
import dev.shuanghua.weather.data.repo.ParamsRepository
import dev.shuanghua.weather.data.usecase.*
import dev.shuanghua.weather.shared.UiMessage
import dev.shuanghua.weather.shared.UiMessageManager
import dev.shuanghua.weather.shared.extensions.ObservableLoadingCounter
import dev.shuanghua.weather.shared.extensions.collectStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 当使用 ObservableUseCase 的时候，记得先调用 invoke()
 * 然后一定要 collect 该 Flow, 才能观察到数据
 * 此页面请求地址中的 cityids不能为空，必须至少有一个城市id
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val paramsRepository: ParamsRepository,
    private val observerIds: ObserverFavoriteCityIdsUseCase,
    private val updateWeather: UpdateFavoriteCityWeatherUseCase,
    private val removeFavorite: RemoveFavoriteUseCase,
    observerWeather: ObserverFavoriteCityWeatherUseCase
) : ViewModel() {
    private val observerLoading = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    val uiState: StateFlow<FavoriteUiState> = combine(
        observerWeather.flow,
        observerLoading.flow,
        uiMessageManager.flow
    ) { weathers, loading, message ->
        FavoriteUiState(favorites = weathers, message = message, loading = loading)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FavoriteUiState.Empty
    )

    init {
        //数据库建立观察绑定
        observerIds("")
        observerWeather("")
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {// 开始写入数据
            observerIds.flow
                .filterNot { it.isEmpty() }
                .map { ids ->  // List -> ArrayList
                    val array = ArrayList<String>()
                    ids.forEach { array.add(it) }
                    array
                }
                .map { paramsRepository.getFavoriteParam(it) }//生成请求参数
                .map { updateWeather(it) }                      //获取网络数据
                .collect {
                    it.collectStatus(observerLoading, uiMessageManager)
                }
        }
    }


    fun deleteFavorite(cityId: String) {
        viewModelScope.launch {
            removeFavorite(RemoveFavoriteUseCase.Params(cityId))
        }
    }
}

//sealed interface FavoriteWeatherState {
//    data class Success(val data: List<FavoriteCityWeather>) : FavoriteWeatherState
//    object Error : FavoriteWeatherState
//    object Loading : FavoriteWeatherState
//}

data class FavoriteUiState(
    val favorites: List<FavoriteCityWeather> = emptyList(),
    val message: UiMessage? = null,
    val loading: Boolean = false
) {
    companion object {
        val Empty = FavoriteUiState()
        const val FAVORITE_SCREEN = "FavoriteScreen"
    }
}