// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.search

import com.google.cloud.functions.HttpFunction
import com.google.cloud.functions.HttpRequest
import com.google.cloud.functions.HttpResponse
import dev.sanson.lightroom.core.search.loadAssets
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.search.model.SearchRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.net.HttpURLConnection

@Suppress("unused")
class SearchLightroom : HttpFunction {
    private val json = Json.Default
    private val scope = CoroutineScope(Dispatchers.IO) + SupervisorJob()

    @OptIn(ExperimentalSerializationApi::class)
    override fun service(
        request: HttpRequest,
        response: HttpResponse,
    ) {
        val searchRequest =
            try {
                json.decodeFromStream<SearchRequest>(request.inputStream)
            } catch (e: Exception) {
                response.badInput()
                return
            }

        val lightroom =
            Lightroom(
                credentialStore =
                    InMemoryCredentialStore(
                        credential = searchRequest.credential,
                    ),
                coroutineScope = scope,
            )

        runBlocking {
            val assets = lightroom.loadAssets(searchConfig = searchRequest.searchConfig)

            // TODO: Return a new type here which omits irrelevant information (Artwork proxy perhaps)
            response.writer.write(json.encodeToString(assets))
        }
    }

    private fun HttpResponse.badInput() {
        setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST, "Malformed search request")
    }
}
