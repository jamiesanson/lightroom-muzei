// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.unsplash

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.sanson.unsplash.api.UnsplashService
import kotlinx.parcelize.Parcelize

interface Unsplash {
    suspend fun getRandomImage(): RandomImage?
}

@Composable
fun rememberRandomImage(unsplash: Unsplash = rememberUnsplash()): RandomImage? {
    var image by rememberSaveable { mutableStateOf<RandomImage?>(null) }

    LaunchedEffect(true) {
        if (image == null) {
            image = unsplash.getRandomImage()
        }
    }

    return image
}

@Parcelize
data class RandomImage(
    val url: String,
    val attribution: Attribution,
) : Parcelable {
    @Parcelize
    data class Attribution(
        val name: String,
        val username: String,
    ) : Parcelable
}

internal class RandomImageViewModel : ViewModel(), Unsplash {
    private val unsplashService by lazy { UnsplashService() }

    private var cachedPhoto: RandomImage? = null

    override suspend fun getRandomImage(): RandomImage? {
        if (cachedPhoto == null) {
            cachedPhoto =
                runCatching { unsplashService.getRandomPhoto() }
                    .map {
                        RandomImage(
                            url = it.urls.regular,
                            attribution =
                                RandomImage.Attribution(
                                    name = it.user.name,
                                    username = it.user.username,
                                ),
                        )
                    }
                    .getOrNull()
        }

        return cachedPhoto
    }
}

@Composable
private fun rememberUnsplash(): Unsplash {
    return if (LocalView.current.isInEditMode) {
        // Avoid ViewModel fetching in previews
        object : Unsplash {
            override suspend fun getRandomImage(): RandomImage? = null
        }
    } else {
        viewModel<RandomImageViewModel>(LocalContext.current as ViewModelStoreOwner)
    }
}
