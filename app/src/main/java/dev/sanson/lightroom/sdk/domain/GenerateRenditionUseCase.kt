package dev.sanson.lightroom.sdk.domain

import dev.sanson.lightroom.sdk.backend.AssetsService
import dev.sanson.lightroom.sdk.model.AssetId
import dev.sanson.lightroom.sdk.model.Rendition
import javax.inject.Inject

class GenerateRenditionUseCase @Inject constructor(
    private val catalogRepository: CatalogRepository,
    private val assetsService: AssetsService,
) {

    suspend operator fun invoke(assetId: AssetId, rendition: Rendition) {
        val catalog = catalogRepository.getCatalog()

        assetsService.generateRendition(
            catalogId = catalog.id.id,
            assetId = assetId.id,
            renditions = rendition.code,
        )
    }
}
