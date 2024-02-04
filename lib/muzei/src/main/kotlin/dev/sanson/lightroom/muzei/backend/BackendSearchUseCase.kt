// Copyright (C) 2024, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.muzei.backend

import dev.sanson.lightroom.lib.search.SearchConfig
import dev.sanson.lightroom.lib.search.SearchUseCase
import dev.sanson.lightroom.sdk.model.Asset
import dev.sanson.lightroom.search.api.SearchRequest
import dev.sanson.lightroom.search.api.SearchService

internal class BackendSearchUseCase(
    private val searchService: SearchService,
) : SearchUseCase {
    override suspend fun invoke(config: SearchConfig): Result<List<Asset>> {
        return runCatching { searchService.search(SearchRequest(config)).assets }
    }
}
