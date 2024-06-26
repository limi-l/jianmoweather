package dev.shuanghua.ui.screen.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shuanghua.weather.data.android.domain.SaveStationToFavorite
import dev.shuanghua.weather.data.android.domain.UpdateWeatherUseCase
import dev.shuanghua.weather.data.android.model.SelectedStation
import dev.shuanghua.weather.data.android.model.Weather
import dev.shuanghua.weather.data.android.repository.DistrictStationRepository
import dev.shuanghua.weather.data.android.repository.WeatherRepository
import dev.shuanghua.weather.shared.ObservableLoadingCounter
import dev.shuanghua.weather.shared.UiMessage
import dev.shuanghua.weather.shared.UiMessageManager
import dev.shuanghua.weather.shared.collectStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(
	private val weatherRepository: WeatherRepository,
	private val stationRepository: DistrictStationRepository,
	private val updateWeatherUseCase: UpdateWeatherUseCase,
	private val addStationToFavoriteUseCase: SaveStationToFavorite,
) : ViewModel() {

	/*
	 保存服务返回的城市ID, 用于下拉刷新
	 */
	private var cityId: String = ""

	/*
	 用于添加到收藏
	 */
	private var stationName: String = ""

	/*
	 从站点页面选择的站点
	 */
	private var selectedStation: SelectedStation = SelectedStation("", "1")

	private val isLoading = ObservableLoadingCounter()
	private val messages = UiMessageManager()

	private val viewModelState = MutableStateFlow(
		WeatherViewModelState(isLoading = false)
	)

	val uiState: StateFlow<WeatherUiState> = viewModelState
		.map(WeatherViewModelState::toUiState)
		.stateIn(
			viewModelScope,
			SharingStarted.WhileSubscribed(5000),
			viewModelState.value.toUiState()
		)

	init {
		/*
		 观察数据库-天气数据
		 */
		viewModelScope.launch {
			observerWeather(
				weatherRepository.observerWeather(),
				isLoading.flow,
				messages.flow,
			) { newData ->
				viewModelState.update {
					it.copy(
						weather = newData.weather,
						isLoading = newData.isLoading,
						uiMessage = newData.uiMessage
					)
				}
			}
		}

		/*
		 观察数据库-站点数据
		 */
		viewModelScope.launch {
			stationRepository.observerSelectedStation().collect {
				if (it != null) selectedStation = it
				updateWeatherUseCase(
					UpdateWeatherUseCase.Params(cityId, selectedStation)
				).collectStatus(isLoading, messages)
			}
		}
	}


	fun refresh() {
		viewModelScope.launch {
			updateWeatherUseCase(
				UpdateWeatherUseCase.Params(cityId, selectedStation)
			).collectStatus(isLoading, messages)
		}
	}


	private suspend fun observerWeather(
		weatherFlow: Flow<Weather>,
		isLoadingFlows: Flow<Boolean>,
		errorMessageFlow: Flow<UiMessage?>,
		updateViewModelState: (WeatherViewModelState) -> Unit,
	) {
		combine(  // coroutines Zip.kt 最多允许 5 个 flow，超过需要自定义
			weatherFlow,
			isLoadingFlows,
			errorMessageFlow
		) { weather, loadingStatus, errorMessage ->
			stationName = weather.stationName
			cityId = weather.cityId
			WeatherViewModelState(
				weather = weather,
				isLoading = loadingStatus,
				uiMessage = errorMessage
			)
		}.collect { updateViewModelState(it) }
	}


	fun clearMessage(id: Long) = viewModelScope.launch {
		messages.clearMessage(id)
	}

	/**
	 * 添加当前城市到收藏页面
	 */
	fun addStationToFavorite() = viewModelScope.launch {
		//使用 executeSync 执行耗时任务时，记得在 doWork 中切到非 Ui 换线程
		try {
			addStationToFavoriteUseCase(cityId, stationName)
		} catch (e: Exception) {
			messages.emitMessage(UiMessage("不要重复收藏哦"))
		}
	}
}

sealed interface WeatherUiState {
	val isLoading: Boolean
	val uiMessage: UiMessage?

	data class NoData(
		override val isLoading: Boolean,
		override val uiMessage: UiMessage?,
	) : WeatherUiState

	data class HasData(
		val weather: Weather,
		override val isLoading: Boolean,
		override val uiMessage: UiMessage?,
	) : WeatherUiState
}

private data class WeatherViewModelState(
	val weather: Weather? = null,
	val isLoading: Boolean = false,
	val uiMessage: UiMessage? = null,
) {
	fun toUiState(): WeatherUiState = if (weather == null) {
		WeatherUiState.NoData(
			isLoading = isLoading,
			uiMessage = uiMessage
		)
	} else {
		WeatherUiState.HasData(
			weather = weather,
			isLoading = isLoading,
			uiMessage = uiMessage
		)
	}
}
