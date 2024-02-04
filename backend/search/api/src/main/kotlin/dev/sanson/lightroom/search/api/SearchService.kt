// Copyright (C) 2024, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.search.api

import retrofit2.http.Body
import retrofit2.http.POST

interface SearchService {
    @POST
    suspend fun search(
        @Body request: SearchRequest,
    ): SearchResponse
}
