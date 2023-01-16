package dev.shuanghua.weather

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import dev.shuanghua.ui.city.CityScreenDestination
import dev.shuanghua.ui.city.cityScreenGraph
import dev.shuanghua.ui.district.DistrictDestination
import dev.shuanghua.ui.district.districtScreenGraph
import dev.shuanghua.ui.favorite.FavoriteDestination
import dev.shuanghua.ui.favorite.favoriteScreenGraph
import dev.shuanghua.ui.more.moreScreenGraph
import dev.shuanghua.ui.province.ProvinceDestination
import dev.shuanghua.ui.province.provinceScreenGraph
import dev.shuanghua.ui.screen.favorites.weather.FavoritesWeatherDestination
import dev.shuanghua.ui.screen.favorites.weather.favoriteWeatherScreenGraph
import dev.shuanghua.ui.setting.SettingDestination
import dev.shuanghua.ui.setting.settingsScreenGraph
import dev.shuanghua.ui.station.StationDestination
import dev.shuanghua.ui.station.stationScreenGraph
import dev.shuanghua.ui.weather.WeatherDestination
import dev.shuanghua.ui.weather.weatherScreenGraph
import dev.shuanghua.ui.web.WebDestination
import dev.shuanghua.ui.web.webScreenGraph

/**
 * 传值和导航都在此处处理
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {

        favoriteScreenGraph(
            openProvinceScreen = {
                navController.navigate(ProvinceDestination.route)
            },

            openFavoriteWeatherScreen = {
                navController.navigate(FavoritesWeatherDestination.route)
            },


            nestedGraphs = {
                provinceScreenGraph(
                    onBackClick = { navController.popBackStack() },
                    openCityScreen = { provinceId, provinceName ->
                        navController.navigate("${CityScreenDestination.route}/$provinceId/$provinceName")
                    }
                )
                cityScreenGraph(
                    onBackClick = { navController.popBackStack() },
                    openFavoriteScreen = {
                        navController.popBackStack(  // cityId 传到 ViewModel, FavoriteScreen 在从 ViewModel 中获取
                            route = FavoriteDestination.destination, // favorite
                            inclusive = false // 如果为 true: 则目标 TestScreen.Favorite.createRoute(root) 也清除出栈
                        )
                    }
                )

                favoriteWeatherScreenGraph(
                    onBackClick = { navController.popBackStack() },
                    openAirDetails = {}
                )
            }
        )

        weatherScreenGraph(
            openAirDetails = {},// 未实现
            openDistrictScreen = { cityId, obtId ->
                navController.navigate("${DistrictDestination.route}/$cityId/$obtId")
            },

            nestedGraphs = {
                districtScreenGraph(
                    onBackClick = { navController.popBackStack() },
                    openStationScreen = { districtName ->
                        navController.navigate("${StationDestination.route}/$districtName")
                    }
                )
                stationScreenGraph(
                    onBackClick = { navController.popBackStack() },
                    openWeatherScreen = {
                        //弹出式返回受限于导航API，不能直接传值，推荐使用数据库或者datastore
                        //isLocation本身存数据库比较好，方便下次重新进入首页是判断是否为定位页面
                        navController.popBackStack(
                            route = WeatherDestination.destination,
                            inclusive = false // 如果为 true: 则目标 TestScreen.Favorite.createRoute(root) 也清除出栈
                        )
                    }
                )


            } // 区县页面 -> 街道站点页面
        )

        moreScreenGraph(
            openWebScreen = { url ->
                navController.navigate("${WebDestination.route}/${Uri.encode(url)}")
            },
            openSettingsScreen = {
                navController.navigate(SettingDestination.route)
            },

            nestedGraphs = {
                webScreenGraph(
                    onBackClick = { navController.popBackStack() }
                )
                settingsScreenGraph(
                    onBackClick = { navController.popBackStack() }
                )
            }
        )
    }
}
