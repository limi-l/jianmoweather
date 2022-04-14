package com.moshuanghua.jianmoweather.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.moshuanghua.jianmoweather.R
import com.moshuanghua.jianmoweather.ui.AppNavigation
import com.moshuanghua.jianmoweather.ui.rememberJianMoAppState
import jianmoweather.home.favorite.FavoriteScreen
import jianmoweather.home.more.MoreScreen
import jianmoweather.home.weather.WeatherScreen
import jianmoweather.module.common_ui_compose.Screen
import timber.log.Timber


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class
)
@Composable
fun MainScreen() {
    val navController = rememberAnimatedNavController()
    val appState = rememberJianMoAppState(navController)
    //viewModel.refresh()
    Scaffold(
        //topBar = {WeatherScreenTopBar(openAirDetails = {})},
        bottomBar = {
            if (appState.shouldShowBottomBar) {
                JianMoBottomBar(navController)
            }
        }
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            AppNavigation(
                navController,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

/**
 * 底部 tab 点击切换页面
 * Modifier.navigationBarsPadding() :
 * 如果父 Layout 不设置，子View设置了，则子View会让父Layout膨胀变大（父Layout高度增加），但父Layout依然占据systemBar空间
 * 如果父 Layout 设置了，子 View 不设置，则子view并不会去占据systemBar空间
 * 总结：子 View 永远不会改变 父Layout的空间位置，但可以更改父Layout的大小
 */
@Composable
fun JianMoBottomBar(navController: NavController) {
    Surface(tonalElevation = 2.dp) {//tonalElevation 改变 surfaceColor 的深浅
        val currentSelectedItem by navController.currentScreenAsState()
        MainScreenNavigation(
            selectedNavigation = currentSelectedItem,
            onNavigationSelected = { selected: Screen ->
                navController.navigate(route = selected.route) {
                    launchSingleTop = true
                    restoreState = true
                    navController.graph.startDestinationRoute?.let {
                        popUpTo(it) { saveState = true }
                    }
                }

            }
        )
    }
}

/**
 * selectedNavigation：要选中的item
 */
@Composable
internal fun MainScreenNavigation(
    selectedNavigation: Screen, //传入 当前正在选中的 item screen
    onNavigationSelected: (Screen) -> Unit, //传出 用户点击之后的新 item screen ,回传给 navController 来调用页面切换
    modifier: Modifier = Modifier
) {
    NavigationBar(
        // Material 3
        modifier = modifier.navigationBarsPadding(),//一般配合 Surface来膨胀使用。 Surface/Box + navigationBarsPadding + Transparent
        containerColor = Color.Transparent, //设置为透明以使用surface 颜色，看起来更统一
    ) {
        MainScreenNavigationItems.forEach { item: MainScreenNavigationItem ->
            NavigationBarItem(
                label = { Text(text = stringResource(item.labelResId)) },
                selected = selectedNavigation == item.screen,
                onClick = { onNavigationSelected(item.screen) },
                //interactionSource = rememberRipple(bounded = true),
                icon = {
                    MainScreenNavigationItemIcon(
                        item = item,
                        selected = selectedNavigation == item.screen
                    )
                }
            )
        }
    }
}

/**
 * 重写监听事件，让选中的页面具有 State 特性
 */
@Composable
private fun NavController.currentScreenAsState(): State<Screen> {
    val selectedItem = remember { mutableStateOf<Screen>(Screen.Weather) }
    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when {
                destination.hierarchy.any { it.route == Screen.Weather.route } -> {
                    selectedItem.value = Screen.Weather
                }
                destination.hierarchy.any { it.route == Screen.Favorite.route } -> {
                    selectedItem.value = Screen.Favorite
                }
                destination.hierarchy.any { it.route == Screen.More.route } -> {
                    selectedItem.value = Screen.More
                }
            }
        }
        addOnDestinationChangedListener(listener)
        onDispose { removeOnDestinationChangedListener(listener) }
    }
    return selectedItem
}

/**
 * 设置 bottombar 图标和文字
 * 用 class 来表示 navigationItem, 每个 navigationItem 有图标和文字以及对应的 screen
 */
private sealed class MainScreenNavigationItem(
    val screen: Screen,
    @StringRes val labelResId: Int,
    @StringRes val contentDescriptionResId: Int
) {
    class ResourceIcon( //普通图片
        screen: Screen,
        @StringRes labelResId: Int,
        @StringRes contentDescriptionResId: Int,
        @DrawableRes val iconResId: Int,
        @DrawableRes val selectedIconResId: Int? = null
    ) : MainScreenNavigationItem(screen, labelResId, contentDescriptionResId)

    class VectorIcon( //矢量图片
        screen: Screen,
        @StringRes labelResId: Int,
        @StringRes contentDescriptionResId: Int,
        val iconImageVector: ImageVector,
        val selectedImageVector: ImageVector? = null
    ) : MainScreenNavigationItem(screen, labelResId, contentDescriptionResId)
}

private val MainScreenNavigationItems = listOf(// 收集 NavigationItem Class, 并设置对应 screen 、图标和文字
    MainScreenNavigationItem.VectorIcon(
        screen = Screen.Favorite,
        labelResId = R.string.favorite,
        contentDescriptionResId = R.string.favorite,
        iconImageVector = Icons.Outlined.Favorite,
        selectedImageVector = Icons.Default.Favorite
    ),
    MainScreenNavigationItem.VectorIcon(
        screen = Screen.Weather,
        labelResId = R.string.weather,
        contentDescriptionResId = R.string.weather,
        iconImageVector = Icons.Outlined.Home,
        selectedImageVector = Icons.Default.Home
    ),
    MainScreenNavigationItem.VectorIcon(
        screen = Screen.More,
        labelResId = R.string.more,
        contentDescriptionResId = R.string.more,
        iconImageVector = Icons.Outlined.Menu,
        selectedImageVector = Icons.Default.Menu
    )
)

/**
 * 根据 NavigationItem 设置的图标类型来确定的载入显示图标
 */
@Composable
private fun MainScreenNavigationItemIcon(item: MainScreenNavigationItem, selected: Boolean) {
    val painter = when (item) {
        is MainScreenNavigationItem.ResourceIcon -> painterResource(item.iconResId)
        is MainScreenNavigationItem.VectorIcon -> rememberVectorPainter(item.iconImageVector)
    }

    val selectedPainter = when (item) {
        is MainScreenNavigationItem.ResourceIcon -> item.selectedIconResId?.let {
            painterResource(
                it
            )
        }
        is MainScreenNavigationItem.VectorIcon -> item.selectedImageVector?.let {
            rememberVectorPainter(it)
        }
    }

    if (selectedPainter != null) {
        Crossfade(targetState = selected) {
            Icon(
                painter = if (it) selectedPainter else painter,
                contentDescription = stringResource(item.contentDescriptionResId)
            )
        }
    } else {
        Icon(
            painter = painter,
            contentDescription = stringResource(item.contentDescriptionResId)
        )
    }
}

/**
 * 收缩后的Navigation ，只显示图标
 */
@Composable
internal fun MainScreenNavigationRail(
    selectedNavigation: Screen,
    onNavigationSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier,
    ) {
        NavigationRail(
            contentColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.systemBarsPadding()
        ) {
            MainScreenNavigationItems.forEach { item ->
                NavigationRailItem(
                    icon = {
                        MainScreenNavigationItemIcon(
                            item = item,
                            selected = selectedNavigation == item.screen
                        )
                    },
                    alwaysShowLabel = false,
                    label = { Text(text = stringResource(item.labelResId)) },
                    selected = selectedNavigation == item.screen,
                    onClick = { onNavigationSelected(item.screen) },
                )
            }
        }
    }
}