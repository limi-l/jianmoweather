package dev.shuanghua.ui.more

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shuanghua.datastore.DataStoreRepository
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {
}