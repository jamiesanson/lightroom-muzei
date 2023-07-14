package dev.sanson.lightroom.sdk.backend

import dev.sanson.lightroom.sdk.backend.model.Catalog
import dev.sanson.lightroom.sdk.backend.model.Resource
import retrofit2.http.GET

interface CatalogService {

    @GET("/v2/catalog")
    fun getCatalog(): Resource<Catalog>
}
