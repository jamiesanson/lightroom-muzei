package dev.sanson.lightroom

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanson.lightroom.backend.Lightroom
import dev.sanson.lightroom.backend.lightroom.AccountService
import javax.inject.Inject

// TODO: Use some other dependency abstraction to retrieve stuff out of hilt
@HiltViewModel
class LightroomSettingsViewModel @Inject constructor(
    val lightroom: Lightroom,
    val accountService: AccountService,
) : ViewModel()