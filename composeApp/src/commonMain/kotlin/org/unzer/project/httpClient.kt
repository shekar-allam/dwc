package org.unzer.project

import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

suspend fun uploadFile(fileData: FileData): String {
    val response: HttpResponse = httpClient.submitFormWithBinaryData(
        url = "https://your-api.com/upload", // Replace with your real endpoint
        formData = formData {
            append(
                "file", fileData.bytes,
                Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=\"${fileData.name}\"")
                    append(HttpHeaders.ContentType, "application/pdf")
                }
            )
        }
    )
    return response.bodyAsText()
}

