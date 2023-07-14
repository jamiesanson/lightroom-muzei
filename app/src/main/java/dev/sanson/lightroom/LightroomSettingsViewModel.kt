package dev.sanson.lightroom

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanson.lightroom.arch.Async
import dev.sanson.lightroom.arch.Store
import dev.sanson.lightroom.arch.Uninitialized
import dev.sanson.lightroom.arch.asAsyncFlow
import dev.sanson.lightroom.arch.collectInto
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.backend.AccountService
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val isSignedIn: Async<Boolean> = Uninitialized,
)

@HiltViewModel
class LightroomSettingsViewModel @Inject constructor(
    val lightroom: Lightroom,
    val accountService: AccountService,
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

    fun signIn(context: Context) = lightroom.signIn(context)

    fun onCompleteSignIn(intent: Intent) = lightroom.handleSignInResponse(intent)
}