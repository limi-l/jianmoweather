package jianmoweather.home.weather

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import jianmoweather.module.common_ui_compose.DescriptionPopup
import jianmoweather.module.common_ui_compose.rememberStateFlowWithLifecycle
import com.moshuanghua.jianmoweather.shared.extensions.ifNullToValue
import jianmoweather.data.db.entity.*
import jianmoweather.module.common_ui_compose.Screen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

object WeatherScreen {
    fun createRoute() = Screen.Weather.route
}

//        contentPadding = rememberInsetsPaddingValues(//获取 systemBar 高度
//            insets = LocalWindowInsets.current.systemBars,
//            applyBottom = false,
//            applyTop = true
//        ),
// val context = LocalContext.current

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalCoroutinesApi
@Composable
fun WeatherScreen() {
    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }
    WeatherScreen(
        scrollBehavior = scrollBehavior,
        openAirDetails = {},
        onMessageShown = {}
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class)
@Composable
internal fun WeatherScreen(
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: WeatherViewModel = hiltViewModel(),
    refresh: () -> Unit = { viewModel.refresh() },
    openAirDetails: () -> Unit,
    onMessageShown: (id: Long) -> Unit,
) {
    val state by rememberStateFlowWithLifecycle(
        stateFlow = viewModel.uiStateFlow
    )

    val loadingState by rememberStateFlowWithLifecycle(
        stateFlow = viewModel.refreshState
    )

    Scaffold(
        topBar = {
            WeatherScreenTopBar(
                temperature = state.temperature,
                scrollBehavior = scrollBehavior,
                openAirDetails = openAirDetails
            )
        },
    ) { paddingValues ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(
                isRefreshing = loadingState.isLoading
            ),
            onRefresh = refresh,
            refreshTriggerDistance = 60.dp,
            indicatorPadding = paddingValues,
            indicator = { _state, _trigger ->
                SwipeRefreshIndicator(
                    state = _state,
                    refreshTriggerDistance = _trigger,
                    scale = true
                )
            }

        ) {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 100.dp),
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                if (state.alarms.isNotEmpty()) {
                    item { AlarmImageList(state.alarms) }
                }
                if (state.temperature != null) item { TemperatureText(temperature = state.temperature!!) }
                if (state.oneDays.isNotEmpty()) item { OneDayList(oneDays = state.oneDays) }
                if (state.others.isNotEmpty()) {
                    item { OtherList(conditions = state.others) }
                }
                if (state.oneHours.isNotEmpty()) item { OneHourList(oneHours = state.oneHours) }
                if (state.exponents.isNotEmpty()) {
                    item { ExponentItems(exponents = state.exponents) }
                }
            }
        }
    }
}


@Composable
internal fun AlarmImageList(alarms: List<Alarm>) {
    Row(
        modifier = Modifier
            .padding(top = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        for (alarm in alarms) {
            AlarmImageItem(alarm = alarm)
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
internal fun TemperatureText(temperature: Temperature) {
    Timber.d("数据1:------------------------")

    Surface(
        modifier = Modifier.padding(38.dp),
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(36.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = temperature.temperature,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 68.sp),
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(start = 26.dp, top = 42.dp)
            )

            Text(
                text = temperature.description,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

        }
    }
}

@Composable
fun OneDayList(
    modifier: Modifier = Modifier,
    oneDays: List<OneDay>
) { // 虽然 List 是不可变的, 但 list 中的每个 OneDay 里面的结构可能都不一样
    LazyRow(
        modifier = modifier.padding(top = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(
            items = oneDays,
            key = { it.id }
        ) {
            OneDayItem(oneDay = it)
        }
    }
}

@Composable
fun OneHourList(
    modifier: Modifier = Modifier,
    oneHours: List<OneHour>
) {
    Timber.d("数据3:oneHour------------------------")

    LazyRow(
        modifier = modifier.padding(top = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(
            items = oneHours,
            key = { it.id }
        ) {
            OneHourItem(oneHour = it)
        }
    }
}

@Composable
fun OtherList(
    modifier: Modifier = Modifier,
    conditions: List<Condition>
) {
    Spacer(modifier = Modifier.height(16.dp))
    LazyRow(modifier) {
        items(
            items = conditions,
            key = { it.name }
        ) {
            OtherItem(condition = it)
        }
    }
}

@Composable
fun AlarmImageItem(modifier: Modifier = Modifier, alarm: Alarm) {
    var oneDayDescriptionPopupShown by remember { mutableStateOf(false) }//state的更改会重新执行当前函数

    if (oneDayDescriptionPopupShown) {//
        DescriptionPopup(
            modifier = Modifier.clickable { oneDayDescriptionPopupShown = false },
            description = alarm.name,
            onDismiss = { oneDayDescriptionPopupShown = false })
    }
    AsyncImage(// coil 异步下载网络图片
        modifier = modifier
            .size(44.dp, 40.dp)
            .padding(start = 2.dp)
            .clip(shape = RoundedCornerShape(percent = 10))
            .clickable(onClick = {
                oneDayDescriptionPopupShown = true
            }),
        model = alarm.icon,
//        contentScale = ContentScale.Fit,
        contentDescription = null
    )
}

/**
 * 健康指数
 */
@Composable
fun ExponentItems(
    modifier: Modifier = Modifier,
    exponents: List<Exponent>
) {
    Surface(
        modifier = modifier.padding(16.dp),
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(40.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Text(
                text = "健康指数",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 16.dp)
            )

            val mod = Modifier
                .width(150.dp)
                .padding(top = 16.dp)

            FlowRow(
                mainAxisAlignment = MainAxisAlignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (exponents.size % 2 != 0) {
                    for (i in exponents.indices - 1) {
                        HealthExponentItem(
                            exponents[i].title,
                            exponents[i].levelDesc,
                            modifier = mod
                        )
                    }
                } else {
                    for (i in exponents.indices) {
                        HealthExponentItem(
                            exponents[i].title,
                            exponents[i].levelDesc,
                            modifier = mod
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OneDayItem(
    modifier: Modifier = Modifier,
    oneDay: OneDay
) {
    var oneDayDescriptionPopupShown by remember { mutableStateOf(false) }
    if (oneDayDescriptionPopupShown) {
        DescriptionPopup(description = oneDay.desc) {
            oneDayDescriptionPopupShown = false
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(16.dp)
            .clickable(
                onClick = { oneDayDescriptionPopupShown = true }
            ),
    ) {
        Text(text = oneDay.week, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Text(text = oneDay.date)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "${oneDay.minT}~${oneDay.maxT}", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun OneHourItem(modifier: Modifier = Modifier, oneHour: OneHour) {
    Column(
        modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = oneHour.t, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(16.dp))
        Text(text = oneHour.rain)
        Spacer(Modifier.height(16.dp))
        Text(text = oneHour.hour)
    }
}

@Composable
fun OtherItem(modifier: Modifier = Modifier, condition: Condition) {
    Surface(
        modifier = modifier.padding(16.dp),
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(36.dp)
    ) {
        Column(
            modifier = Modifier
                .width(148.dp)
                .height(90.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = condition.name)
            Spacer(Modifier.height(8.dp))
            Text(text = condition.value)
        }
    }
}

@Composable
fun HealthExponentItem(
    title: String = "舒适度",
    levelDesc: String = "",
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title)
        Text(text = levelDesc)
    }
}

/**
 * 标题
 */
@Composable
fun WeatherScreenTopBar(
    temperature: Temperature?,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    openAirDetails: () -> Unit
) {
    // 从上层到下层: status图标，Status背景色, TopBar ,  Content
    val foregroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent,
        actionIconContentColor = MaterialTheme.colorScheme.onSurface
    )
    val backgroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors()
    val backgroundColor = backgroundColors.containerColor(
        scrollFraction = scrollBehavior?.scrollFraction ?: 0f  //  离开顶部时设置为 surfaceColor, 否则使用默认
    ).value

    Surface(
        color = backgroundColor,
//        tonalElevation = 2.dp,   //tonalElevation使用ColorScheme.surface颜色, 避免和backgroundColor冲突所以注释掉
    ) {
        CenterAlignedTopAppBar(
            modifier = Modifier.statusBarsPadding(),
            scrollBehavior = scrollBehavior,
            colors = foregroundColors,
            navigationIcon = {
                Text(
                    text = temperature?.aqi.ifNullToValue(),
                    modifier = Modifier
                        .clickable(onClick = openAirDetails)
                        .clip(shape = CircleShape)
                        .padding(16.dp)
                )
            },
            title = {
                Text(
                    text = temperature?.cityName.ifNullToValue(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            actions = {
                Text(
                    text = temperature?.stationName.ifNullToValue(),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        )
    }
}
