package dev.sanson.lightroom.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanson.lightroom.arch.Async
import dev.sanson.lightroom.arch.Store
import dev.sanson.lightroom.arch.Uninitialized
import dev.sanson.lightroom.arch.asAsyncFlow
import dev.sanson.lightroom.arch.collectInto
import dev.sanson.lightroom.sdk.Lightroom
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val isSignedIn: Async<Boolean> = Uninitialized,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val lightroom: Lightroom,
) : ViewModel() {

    val store = Store(SettingsState())

    init {
        viewModelScope.launch {
            lightroom.isSignedIn
                .asAsyncFlow()
                .collectInto(store) {
                    copy(isSignedIn = it)
                }
        }
    }
}
