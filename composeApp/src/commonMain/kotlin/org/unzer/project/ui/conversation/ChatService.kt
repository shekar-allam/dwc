//package org.unzer.project.ui.conversation
//import io.ktor.client.request.forms.formData
//import io.ktor.client.request.forms.submitFormWithBinaryData
//import io.ktor.client.request.post
//import io.ktor.client.request.setBody
//import io.ktor.client.statement.bodyAsText
//import io.ktor.http.ContentType
//import io.ktor.http.Headers
//import io.ktor.http.HttpHeaders
//import io.ktor.http.contentType
//import io.ktor.http.isSuccess
//import kotlinx.serialization.json.Json
//import org.unzer.project.FileData
//import org.unzer.project.httpClient
//
//object ChatService {
//    const val BASE_URL = "http://10.0.2.2:8081/api"
//
//        suspend fun sendQuery(query: String, useChatMode: Boolean): String {
//            val endpoint = if (useChatMode) "$BASE_URL/chat" else "$BASE_URL/genericChat"
//            return try {
//                val response = httpClient.post(endpoint) {
//                    contentType(ContentType.Application.Json)
//                    setBody(Json.encodeToString(QueryPayload.serializer(), QueryPayload(query)))
//                }
//                response.bodyAsText()
//            } catch (e: Exception) {
//                "Error: ${e.message}"
//            }
//        }
//
//        suspend fun uploadFile(fileData: FileData): Boolean {
//            return try {
//                val response = httpClient.submitFormWithBinaryData(
//                    url = "$BASE_URL/upload",
//                    formData = formData {
//                        append(
//                            key = "file",
//                            value = fileData.bytes,
//                            headers = Headers.build {
//                                append(HttpHeaders.ContentDisposition, "filename=\"${fileData.name}\"")
//                                append(HttpHeaders.ContentType, "application/pdf")
//                            }
//                        )
//                    }
//                )
//                response.status.isSuccess()
//            } catch (e: Exception) {
//                false
//            }
//        }
//
//        suspend fun clearContext(): Boolean {
//            return try {
//                val response = httpClient.post("$BASE_URL/clearContext") {
//                    contentType(ContentType.Application.Json)
//                    setBody("")
//                }
//                response.status.isSuccess()
//            } catch (e: Exception) {
//                false
//            }
//        }
//    }