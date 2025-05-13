package org.unzer.project

import io.ktor.client.*
import io.ktor.client.engine.js.*

actual val httpClient = HttpClient(Js)
