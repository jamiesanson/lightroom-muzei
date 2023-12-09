// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.domain

import dev.sanson.lightroom.sdk.backend.AssetsService
import dev.sanson.lightroom.sdk.model.Asset
import javax.inject.Inject

internal class GetCatalogAssetsUseCase
    @Inject
    constructor(
        private val catalogRepository: CatalogRepository,
        private val assetsService: AssetsService,
    ) {
        /**
         * Paginate through all assets in a catalog, and map them to [Asset] models
         */
        suspend operator fun invoke(): List<Asset> {
            val catalogId = catalogRepository.getCatalog().id

            val catalogAssets =
                buildList {
                    // Begin with negative time to (hopefully) fetch all undated assets.
                    // We may miss some if there's more than [PAGE_SIZE] undated assets in the album
                    var capturedAfter: String? = "-0001-12-31T23:59:59"

                    while (capturedAfter != null) {
                        val page =
                            assetsService.retrieveAssets(
                                catalogId = catalogId.id,
                                limit = API_PAGE_SIZE,
                                capturedAfter = capturedAfter,
                            )

                        addAll(page.resources)

                        capturedAfter = page.links?.next?.capturedAfter
                    }
                }

            return catalogAssets.map { it.asset.toAsset(catalogId) }
        }
    }
