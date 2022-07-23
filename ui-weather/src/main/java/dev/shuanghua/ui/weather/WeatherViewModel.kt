package dev.shuanghua.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shuanghua.weather.shared.UiMessageManager
import dev.shuanghua.weather.shared.extensions.ObservableLoadingCounter
import dev.shuanghua.weather.shared.extensions.collectStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shuanghua.weather.data.usecase.ObserverWeatherUseCase
import dev.shuanghua.weather.data.usecase.UpdateLocationCityWeather
import dev.shuanghua.ui.weather.WeatherUiState.Companion.HOME_SCREEN
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val updateWeather: UpdateLocationCityWeather, // network -> db
    observerWeather: ObserverWeatherUseCase,  // db(pojo) -> ViewModel
) : ViewModel() {

    private val observerLoading = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    // 协程库 combine 默认最多支持传入 5 个 Flow
    val uiStateFlow: StateFlow<WeatherUiState> = combine(
        observerWeather.flow,
        uiMessageManager.flow,
        observerLoading.flow
    ) { weather, message, loading ->
        if (weather != null) {
            WeatherUiState(
                temperature = weather.temperature,
                alarms = weather.alarms,
                oneHours = weather.oneHours,
                oneDays = weather.oneDays,
                others = weather.others,
                exponents = weather.exponents,
                message = message,
                loading = loading
            )
        } else {
            WeatherUiState.Empty
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WeatherUiState.Empty,
    )

    init {
        //refresh()  //每次进入自动刷新
        observerWeather(ObserverWeatherUseCase.Params(HOME_SCREEN))
    }

    /**
     * 后台自动更新定位，
     * 观察参数数据表，一旦出现变动则自动更新天气
     */
//	fun autoRefresh(screen: String = HOME_SCREEN) { //
//		viewModelScope.launch {
//			updateWeather(UpdateWeatherUseCase.Params(HOME_SCREEN))
//				.collectStatus(observerLoading, uiMessageManager)
//		}
//	}

    /**
     * requestParamsWithLocation(): 返回具体数据 Result<Params>
     * updateWeather(params): 不返回具体数据，只返回Flow<InvokeStatus>
     */
    fun refresh() {
        viewModelScope.launch {
            updateWeather(UpdateLocationCityWeather.Params(HOME_SCREEN))
                .collectStatus(observerLoading, uiMessageManager)
        }
    }

    fun clearMessage(id: Long) {
        viewModelScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }

    fun addToFavorite(){
        //TODO addToFavorite
    }
}