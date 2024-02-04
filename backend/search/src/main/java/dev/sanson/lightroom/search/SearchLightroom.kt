// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.search

import com.google.cloud.functions.HttpFunction
import com.google.cloud.functions.HttpRequest
import com.google.cloud.functions.HttpResponse
import dev.sanson.lightroom.lib.search.searchUseCase
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.sdk.backend.auth.Credential
import dev.sanson.lightroom.search.api.SearchRequest
import dev.sanson.lightroom.search.api.SearchResponse
import dev.sanson.lightroom.search.api.SearchService
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
import kotlin.jvm.optionals.getOrElse

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

        val accessToken = request.getFirstHeader("X-Lightroom-Access-Token")
            .getOrElse {
                response.notAuthorized()
                return
            }

        val credential = Credential(
            accessToken = accessToken,
            // Use a blank refresh token. This will result in a token refresh failure if the
            // incoming access token is invalid.
            refreshToken = ""
        )

        val lightroom =
            Lightroom(
                credentialStore =
                    InMemoryCredentialStore(
                        credential = credential,
                    ),
                coroutineScope = scope,
            )

        // Use an implementation of the SearchService API to ensure we're returning the correctly
        // typed response.
        val service = LightroomSearchService(lightroom)

        runBlocking {
            val searchResponse = service.search(searchRequest)
            response.writer.write(json.encodeToString(searchResponse))
        }
    }

    private fun HttpResponse.badInput() {
        setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST, "Malformed search request")
    }

    private fun HttpResponse.notAuthorized() {
        setStatusCode(HttpURLConnection.HTTP_UNAUTHORIZED, "Lightroom authorization failed")
    }

    private class LightroomSearchService(private val lightroom: Lightroom): SearchService {
        override suspend fun search(request: SearchRequest): SearchResponse {
            val search = lightroom.searchUseCase()
            val assets = search(request.searchConfig).getOrThrow()

            return SearchResponse(assets = assets)
        }
    }
}
