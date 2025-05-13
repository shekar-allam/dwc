package org.unzer.project

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*

actual val httpClient = HttpClient(OkHttp)
