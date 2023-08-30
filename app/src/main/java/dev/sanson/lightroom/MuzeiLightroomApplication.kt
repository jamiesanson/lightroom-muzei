package dev.sanson.lightroom

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * TODO(s) in no particular order:
 * * Tablet support
 * * Screen transitions
 * * Testing presenters
 * * Modularise + revisit build-logic
 * * Adobe review
 * * Unsplash review
 * * Move secrets out of source
 * * UI testing
 * * Finish off filtering
 * * CI - testing
 * * Crash analytics; Leak detection
 * * Deployment - Automated beta/internal test
 * * Deployment - Automated prod deployment
 * * App icon
 * * Blog - Circuit without Anvil (can I make this better Circuit-side? Perhaps circuit-codegen-hilt?)
 * *
 */
@HiltAndroidApp
class MuzeiLightroomApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
