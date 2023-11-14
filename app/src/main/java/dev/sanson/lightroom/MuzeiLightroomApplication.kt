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
 * * Finish screen uplift
 *
 * --------------------
 *   Production-ready
 * --------------------
 * * Crash analytics; Leak detection
 * * App icon & store assets
 *
 * ---------------
 *   Open source
 * ---------------
 * * Testing presenters
 * * CI - run suite on commits
 * * Documentation & licensing
 * * Move remaining list items to issues on project
 *
 * --------------------
 *   Release prep
 * --------------------
 * * Adobe review
 * * Unsplash review
 * * Play Store account
 * * Testing
 *
 * --------------------
 *   Extra-curricular/Fast-follow
 * --------------------
 * * Loading improvements - shimmer & placeholders
 * * Form-factor support
 * * UI testing
 * * Snapshot testing
 * * Deployment - Automated beta/internal test
 * * Deployment - Automated prod deployment
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
