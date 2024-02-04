// Copyright (C) 2024, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.muzei.search

import dev.sanson.lightroom.lib.search.SearchConfig
import dev.sanson.lightroom.lib.search.SearchUseCase
import dev.sanson.lightroom.sdk.model.Asset
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private class CompositeSearchUseCase(
    private val useCases: List<SearchUseCase>,
) : SearchUseCase {
    override suspend fun invoke(config: SearchConfig): Result<List<Asset>> {
        return useCases.asFlow()
            .map { it(config) }
            .first { it.isSuccess }
    }
}

internal fun SearchUseCase(vararg searchUseCase: SearchUseCase): SearchUseCase {
    return CompositeSearchUseCase(searchUseCase.toList())
}
