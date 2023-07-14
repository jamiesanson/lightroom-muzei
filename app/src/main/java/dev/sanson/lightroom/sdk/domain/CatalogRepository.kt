package dev.sanson.lightroom.sdk.domain

import dev.sanson.lightroom.sdk.backend.CatalogService
import dev.sanson.lightroom.sdk.model.Catalog
import dev.sanson.lightroom.sdk.model.CatalogId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CatalogRepository @Inject constructor(
    private val catalogService: CatalogService,
) {
    private var cachedCatalog: Catalog? = null

    suspend fun getCatalog(): Catalog = withContext(Dispatchers.IO) {
        cachedCatalog?.let { return@withContext it }

        val catalog = catalogService.getCatalog()

        return@withContext Catalog(
            id = CatalogId(catalog.id),
            name = catalog.payload.name,
        ).also { cachedCatalog = it }
    }
}
