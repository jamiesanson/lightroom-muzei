package dev.sanson.lightroom.ui.signin

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanson.lightroom.sdk.Lightroom
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val lightroom: Lightroom,
) : ViewModel() {
    fun signIn(context: Context) = lightroom.signIn(context)

    fun onCompleteSignIn(intent: Intent) = lightroom.handleSignInResponse(intent)
}
