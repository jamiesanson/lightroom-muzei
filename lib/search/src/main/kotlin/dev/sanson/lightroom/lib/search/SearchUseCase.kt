// Copyright (C) 2024, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.lib.search

import dev.sanson.lightroom.sdk.model.Asset

interface SearchUseCase {
    suspend operator fun invoke(config: SearchConfig): Result<List<Asset>>
}
