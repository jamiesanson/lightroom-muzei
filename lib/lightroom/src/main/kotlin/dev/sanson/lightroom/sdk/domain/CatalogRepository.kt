package dev.sanson.lightroom.sdk.domain

import dev.sanson.lightroom.sdk.backend.CatalogService
import dev.sanson.lightroom.sdk.model.Catalog
import dev.sanson.lightroom.sdk.model.CatalogId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class CatalogRepository(
    private val catalogService: CatalogService,
) {
    private var cachedCatalog: Catalog? = null

    suspend fun getCatalog(): Catalog =
        withContext(Dispatchers.IO) {
            cachedCatalog?.let { return@withContext it }

            val catalog = catalogService.getCatalog()

            return@withContext Catalog(
                id = CatalogId(catalog.id),
                name = catalog.payload?.name ?: "Lightroom",
            ).also { cachedCatalog = it }
        }
}
