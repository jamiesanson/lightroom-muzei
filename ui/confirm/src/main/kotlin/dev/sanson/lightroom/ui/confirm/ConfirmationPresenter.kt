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
import dev.sanson.lightroom.common.config.ConfigRepository
import dev.sanson.lightroom.muzei.LightroomAlbumProvider
import dev.sanson.lightroom.muzei.loadAssets
import dev.sanson.lightroom.muzei.toArtwork
import dev.sanson.lightroom.screens.ConfirmationScreen
import dev.sanson.lightroom.screens.FinishActivityScreen
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.model.Asset
import dev.sanson.lightroom.sdk.model.Rendition
import kotlinx.coroutines.flow.firstOrNull
import nz.sanson.lightroom.coil.LocalLightroomImageLoader

class ConfirmationPresenter
    @AssistedInject
    constructor(
        @Assisted private val navigator: Navigator,
        private val configRepository: ConfigRepository,
        private val lightroom: Lightroom,
    ) : Presenter<ConfirmState> {
        @Composable
        override fun present(): ConfirmState {
            val imageLoader = LocalLightroomImageLoader.current

            val context = LocalContext.current
            val providerClient = remember { getProviderClient<LightroomAlbumProvider>(context) }
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
                    ConfirmState.LoadingArtwork

                else ->
                    when (val firstImage = firstArtwork) {
                        null ->
                            ConfirmState.LoadingFirstImage(art)

                        else ->
                            ConfirmState.Loaded(
                                artwork = art,
                                firstWallpaper = firstImage,
                                firstArtworkCaptureDate = requireNotNull(art.first().captureDate),
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

        @CircuitInject(ConfirmationScreen::class, SingletonComponent::class)
        @AssistedFactory
        interface Factory {
            fun create(navigator: Navigator): ConfirmationPresenter
        }
    }
