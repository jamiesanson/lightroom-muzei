package dev.sanson.lightroom.ui.album

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanson.lightroom.data.Filter
import javax.inject.Inject

@HiltViewModel
class FilterStoreViewModel @Inject constructor(
    val filterStore: DataStore<Filter?>,
) : ViewModel()

@Composable
fun rememberFilterStore(viewModel: FilterStoreViewModel = hiltViewModel()): DataStore<Filter?> {
    return viewModel.filterStore
}
