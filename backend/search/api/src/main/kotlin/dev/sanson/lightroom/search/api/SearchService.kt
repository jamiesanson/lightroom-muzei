// Copyright (C) 2024, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.search.api

import retrofit2.http.Body
import retrofit2.http.POST

/**
 * TODO: This feels like too many "search" related modules.
 * Combine this in to one JVM, one backend, one Android. JVM includes requests & responses.
 * Version the API to ensure we can't inadvertantly break it.
 * Android module can switch between on-device and API-driven in case of breakage.
 */
interface SearchService {
    @POST
    suspend fun search(
        @Body request: SearchRequest,
    ): SearchResponse
}
