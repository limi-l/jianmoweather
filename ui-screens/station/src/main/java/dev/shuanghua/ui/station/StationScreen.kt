package dev.shuanghua.ui.station

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.shuanghua.weather.data.android.model.Station


@Composable
fun StationScreen(
    onBackClick: () -> Unit,
    navigateToWeatherScreen: () -> Unit,
    viewModel: StationViewModel = hiltViewModel(),
) {
    val uiState: StationsUiState by viewModel.uiState.collectAsStateWithLifecycle()

    StationScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        navigateToWeatherScreen = { obtId ->
            viewModel.saveSelectedStation(obtId)
            navigateToWeatherScreen()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationScreen(
    uiState: StationsUiState,
    onBackClick: () -> Unit,
    navigateToWeatherScreen: (String) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            StationTopBar(
                scrollBehavior = scrollBehavior,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize()
        ) {
            when (uiState) {
                is StationsUiState.NoData -> {}
                is StationsUiState.HasData -> {
                    items(
                        items = uiState.stationList,
                        key = { station -> station.stationName }
                    ) { station ->
                        StationItem(
                            station = station,
                            navigateToWeatherScreen = navigateToWeatherScreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StationItem(
    station: Station,
    navigateToWeatherScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    navigateToWeatherScreen(station.stationId)
                }
            )
            .padding(8.dp)
    ) {
        Text(
            text = station.stationName,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 20.sp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onBackClick: () -> Unit,
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = { Text(text = "站点") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "返回"
                )
            }
        }
    )
}