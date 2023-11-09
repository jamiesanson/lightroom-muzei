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
 * * Modularise + revisit build-logic
 *      * Gradle - convention per library type
 *      * Features - per-screen + anything else there might be in there
 *      * :core:data to lower config repo
 *      * :core:ui for theme, components
 *      * :core:di for qualifiers
 *      * :lib:lightroom - existing
 *      * :lib:unsplash - move hyperlink text into library to avoid core deps
 *      * :lib:muzei <-- lightroom, everything for LoadArtwork.kt. Integration module
 * * Form-factor support
 * * Testing presenters
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
