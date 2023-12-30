// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.android.auth

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dev.sanson.lightroom.sdk.Lightroom
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

internal class TokenRefreshWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result =
        coroutineScope {
            val lightroom = Lightroom(filesDir = applicationContext.filesDir, coroutineScope = this)

            runCatching {
                lightroom.authManager.refreshTokens()
            }.fold(
                onSuccess = { Result.success() },
                onFailure = { error ->
                    when (error) {
                        is HttpException -> Result.failure()
                        else -> Result.retry()
                    }
                },
            )
        }

    companion object {
        private const val TOKEN_REFRESH_WORK_NAME = "lightroom_token_refresh"

        fun enqueue(context: Context) {
            val refreshConstraints =
                Constraints(
                    requiredNetworkType = NetworkType.CONNECTED,
                    requiresBatteryNotLow = true,
                )

            // Refresh tokens every week, near the end of said week
            val requestBuilder =
                PeriodicWorkRequestBuilder<TokenRefreshWorker>(
                    repeatInterval = 7,
                    repeatIntervalTimeUnit = TimeUnit.DAYS,
                    flexTimeInterval = 1,
                    flexTimeIntervalUnit = TimeUnit.DAYS,
                )

            val request =
                requestBuilder
                    .setConstraints(refreshConstraints)
                    .setInitialDelay(duration = 1, TimeUnit.DAYS)
                    .setBackoffCriteria(BackoffPolicy.LINEAR, backoffDelay = 1, TimeUnit.HOURS)
                    .build()

            val workManager = WorkManager.getInstance(context)

            workManager
                .enqueueUniquePeriodicWork(TOKEN_REFRESH_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
        }
    }
}
