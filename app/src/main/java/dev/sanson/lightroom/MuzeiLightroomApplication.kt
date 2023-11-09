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
 * * Screen transitions & UI polish
 * * Form-factor support
 *
 * ---------------
 *   Open source
 * ---------------
 * * UI testing
 * * Snapshot testing
 * * Testing presenters
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
 * * Blog - Circuit without Anvil
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
