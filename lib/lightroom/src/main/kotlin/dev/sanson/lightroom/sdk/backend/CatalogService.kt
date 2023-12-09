// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.backend

import dev.sanson.lightroom.sdk.backend.model.Catalog
import dev.sanson.lightroom.sdk.backend.model.Resource
import retrofit2.http.GET

internal interface CatalogService {
    @GET("/v2/catalog")
    suspend fun getCatalog(): Resource<Catalog>
}
