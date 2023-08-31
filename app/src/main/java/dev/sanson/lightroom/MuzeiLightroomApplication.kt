package dev.sanson.lightroom

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * TODO(s):
 * ---------
 *   Build
 * ---------
 * * Finish off filtering
 * * Testing presenters
 * * Modularise + revisit build-logic
 * * Form-factor support
 * * Screen transitions & UI polish
 *
 * ---------------
 *   Open source
 * ---------------
 * * UI testing
 * * Snapshot testing
 * * CI - run suite on commits
 * * Move secrets out of source
 * * Documentation & licensing
 *
 * --------------------
 *   Production-ready
 * --------------------
 * * Crash analytics; Leak detection
 * * Deployment - Automated beta/internal test
 * * Deployment - Automated prod deployment
 * * App icon
 *
 * --------------------
 *   Release prep
 * --------------------
 * * Adobe review
 * * Unsplash review
 * * Play Store account
 *
 * --------------------
 *   Extra-curricular
 * --------------------
 * * Blog - Circuit without Anvil (can I make this better Circuit-side? Perhaps circuit-codegen-hilt?)
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
