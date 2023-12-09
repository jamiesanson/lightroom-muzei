// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.ui.confirm

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.apps.muzei.api.provider.ProviderContract.getProviderClient
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.components.SingletonComponent
import dev.sanson.lightroom.common.config.Config
import dev.sanson.lightroom.common.config.ConfigRepository
import dev.sanson.lightroom.muzei.LightroomArtProvider
import dev.sanson.lightroom.muzei.loadAssets
import dev.sanson.lightroom.muzei.toArtwork
import dev.sanson.lightroom.screens.ConfirmationScreen
import dev.sanson.lightroom.screens.FinishActivityScreen
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.model.Asset
import dev.sanson.lightroom.sdk.model.Rendition
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import nz.sanson.lightroom.coil.LocalLightroomImageLoader

class ConfirmationPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    private val configRepository: ConfigRepository,
    private val lightroom: Lightroom,
) : Presenter<ConfirmState> {
    @Composable
    override fun present(): ConfirmState {
        val imageLoader = LocalLightroomImageLoader.current

        val context = LocalContext.current
        val providerClient = remember { getProviderClient<LightroomArtProvider>(context) }
        val artwork by produceState<List<Asset>?>(initialValue = null) {
            val config =
                requireNotNull(configRepository.config.firstOrNull()) {
                    "Config is required on confirmation"
                }

            value =
                lightroom
                    .loadAssets(config)
                    .also { assets ->
                        providerClient.setArtwork(assets.map { it.toArtwork() })
                    }
        }

        val stepNumber by produceState(initialValue = 3) {
            value = configRepository.config.first()?.source?.let {
                if (it is Config.Source.Album) 4 else 3
            } ?: 3
        }

        var firstArtwork by remember { mutableStateOf<Asset?>(null) }

        LaunchedEffect(artwork) {
            val art = artwork ?: return@LaunchedEffect

            val firstAsset = art.first()

            lightroom.generateRendition(
                asset = firstAsset.id,
                rendition = Rendition.Full,
            )

            with(imageLoader) {
                newRequest(
                    assetId = firstAsset.id,
                    catalogId = firstAsset.catalogId,
                    rendition = Rendition.Full,
                ).await()
            }

            firstArtwork = firstAsset
        }

        return when (val art = artwork) {
            null ->
                ConfirmState.LoadingArtwork(stepNumber)

            else ->
                when (val firstImage = firstArtwork) {
                    null ->
                        ConfirmState.LoadingFirstImage(stepNumber, art)

                    else ->
                        ConfirmState.Loaded(
                            stepNumber = stepNumber,
                            artwork = art,
                            firstWallpaper = firstImage,
                            firstWallpaperAge = firstImage.computeAge(),
                            eventSink = { event ->
                                when (event) {
                                    ConfirmEvent.OnFinish ->
                                        navigator.goTo(FinishActivityScreen(resultCode = Activity.RESULT_OK))
                                }
                            },
                        )
                }
        }
    }

    private fun Asset.computeAge(): String {
        val timePeriod =
            captureDate.periodUntil(Clock.System.now(), TimeZone.currentSystemDefault())

        return when {
            // "5 years"
            timePeriod.years > 1 -> "${timePeriod.years} years"

            // "1 year, 3 months" OR "1 year"
            timePeriod.years == 1 ->
                if (timePeriod.months > 0) {
                    "${timePeriod.years} year, ${timePeriod.months} months"
                } else {
                    "${timePeriod.years} year"
                }

            timePeriod.months > 1 -> "${timePeriod.months} months"

            else -> "${timePeriod.days} days"
        }
    }

    @CircuitInject(ConfirmationScreen::class, SingletonComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): ConfirmationPresenter
    }
}
