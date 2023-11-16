package dev.sanson.lightroom

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * TODO(s):
 * ---------------
 *   Open source
 * ---------------
 * * Testing presenters
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
 * * Short onboarding explaining how it works
 * * Loading improvements - shimmer & placeholders
 * * Add notes around:
 *      * Not having to re-apply settings when you make changes on the Lightroom end
 *      * Artwork invalidation, keeping your wallpapers up to date
 * * Muzei artwork invalidation after 24 hours
 * * Form-factor & window size support
 * * Toolbar improvement - collapsing?
 * * UI testing
 * * Snapshot testing
 * * Baseline profiles
 * * Benchmarks
 * * Leak analysis in debug/qa/prod
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
