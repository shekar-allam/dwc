package org.unzer.project

import io.ktor.client.*
import io.ktor.client.engine.cio.*

actual val httpClient = HttpClient(CIO)
