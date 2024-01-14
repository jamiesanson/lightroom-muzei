// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.search

import com.google.cloud.functions.HttpFunction
import com.google.cloud.functions.HttpRequest
import com.google.cloud.functions.HttpResponse
import dev.sanson.lightroom.sdk.Lightroom
import dev.sanson.lightroom.search.model.SearchRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

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
                // TODO: Return an error response
                return
            }

        val credentialStore =
            InMemoryCredentialStore(
                credential = searchRequest.credential,
            )

        val lightroom = Lightroom(credentialStore, scope)

        response.writer.write("NO")
    }
}
