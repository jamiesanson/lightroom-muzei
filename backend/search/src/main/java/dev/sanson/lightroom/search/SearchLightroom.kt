// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.search

import com.google.cloud.functions.HttpFunction
import com.google.cloud.functions.HttpRequest
import com.google.cloud.functions.HttpResponse

class SearchLightroom : HttpFunction {
    override fun service(
        request: HttpRequest,
        response: HttpResponse,
    ) {
        response.writer.write("NO")
    }
}
