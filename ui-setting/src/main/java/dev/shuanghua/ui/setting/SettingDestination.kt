package dev.shuanghua.ui.setting

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.shuanghua.core.navigation.AppNavigationDestination

object SettingDestination : AppNavigationDestination {
    override val route = "setting_route"
    override val destination = "setting_destination"
}

fun NavGraphBuilder.settingsGraph() {
    composable(route = SettingDestination.route) {//省份页面的地址
        SettingScreen(//接收页面的回调事件，因为回调逻辑涉及页面跳转需要 navController 对象，所以继续将事件传递到上游处理

        )
    }
}