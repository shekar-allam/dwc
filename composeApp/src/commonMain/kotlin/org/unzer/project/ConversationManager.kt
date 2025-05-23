package org.unzer.project

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Immutable
data class Message(
    val author: String,
    val content: String = "",
    val imageBase64: String? = null
)

class ConversationUiState(
    initialMessages: List<Message>
) {
    private val _messages: SnapshotStateList<Message> =
        mutableStateListOf(*initialMessages.toTypedArray())
    val messages: List<Message> = _messages

    fun addMessage(msg: Message) {
        _messages.add(0, msg)
    }
}

object ConversationManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val uiState = ConversationUiState(
        initialMessages = listOf(
            Message("Assistant", "Hello! How can I help you today?")
        )
    )

    val status = mutableStateOf("Awaiting input...")
    val userQuery = mutableStateOf("")
    val uploadedFile = mutableStateOf<FileData?>(null)
    val isDocumentSearchMode = mutableStateOf(false)
    val isFileUploading = mutableStateOf(false)

    fun sendQuery() {
        if (userQuery.value.isNotBlank()) {
            scope.launch {
                // Add user's message to UI
                uiState.addMessage(Message("You", userQuery.value))
                status.value = "Sending query..."

                val result = sendQueryToServer(userQuery.value, isDocumentSearchMode.value)
                userQuery.value = ""

                // Process assistant's response
                when (result) {
                    is QueryResult.Text -> {
                        uiState.addMessage(Message("Assistant", result.text))
                    }

                    is QueryResult.Base64Image -> {
                        uiState.addMessage(
                            Message(
                                author = "Assistant",
                                content = "",
                                imageBase64 = result.base64Data
                            )
                        )
                    }

                    is QueryResult.Error -> {
                        uiState.addMessage(Message("Assistant", result.message))
                    }
                }

                status.value = "Response received"
            }
        } else {
            status.value = "Please enter a question."
        }
    }


    fun uploadFile() {
        isFileUploading.value = true
        scope.launch {
            status.value = "Picking file..."
            val file = pickPdfFile()
            if (file != null) {
                uploadedFile.value = file
                status.value = "Uploading file..."
                val uploadSuccess = uploadFileToServer(file)
                if (uploadSuccess) {
                    isDocumentSearchMode.value = true
                    status.value = "File uploaded. Chat mode activated."
                    uiState.addMessage(Message("You", "Uploaded file: ${file.name}"))
                } else {
                    uploadedFile.value = null
                    status.value = "File upload failed."
                }
                isFileUploading.value = false
            } else {
                status.value = "File upload cancelled or failed."
                isFileUploading.value = false
            }
        }
    }

    fun clearContext() {
        scope.launch {
            status.value = "Clearing context..."
            val cleared = clearChatContext()
            if (cleared) {
                isDocumentSearchMode.value = false
                uploadedFile.value = null
                status.value = "Context cleared. Switched to generic mode."
                uiState.addMessage(
                    Message(
                        "Assistant",
                        "Context cleared. Ready for generic queries."
                    )
                )
            } else {
                status.value = "Failed to clear context."
            }
        }
    }

    private suspend fun sendQueryToServer(
        query: String,
        isDocumentSearchMode: Boolean
    ): QueryResult {
        val endpoint = "$BASE_URL/chat"
        return try {
            val response = httpClient.post(endpoint) {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(QueryPayload.serializer(), QueryPayload(query)))
            }

            val body = response.bodyAsText()
            val result = Json.decodeFromString(QueryResponse.serializer(), body).response

            if (result.startsWith("data:image")) {
                QueryResult.Base64Image(result.substringAfter(":::"))
            } else {
                QueryResult.Text(result)
            }

        } catch (e: Exception) {
            QueryResult.Error("Error: ${e.message}")
        }
    }

    private suspend fun uploadFileToServer(fileData: FileData): Boolean {
        return try {
            val mimeType = getMimeTypeFromFileName(fileData.name)

            val response = httpClient.submitFormWithBinaryData(
                url = "$BASE_URL/upload",
                formData = formData {
                    append(
                        key = "file",
                        value = fileData.bytes,
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"${fileData.name}\"")
                            append(HttpHeaders.ContentType, mimeType)
                        }
                    )
                }
            )
            response.status.isSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun getMimeTypeFromFileName(fileName: String): String {
        return when (fileName.substringAfterLast('.', "").lowercase()) {
            "pdf" -> "application/pdf"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "csv" -> "text/csv"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "txt" -> "text/plain"
            else -> "application/octet-stream"
        }
    }

    private suspend fun clearChatContext(): Boolean {
        return try {
            val response = httpClient.post("$BASE_URL/clearContext") {
                contentType(ContentType.Application.Json)
                setBody("")
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }

    //private const val BASE_URL = "http://10.68.160.105:8081/api"
    private const val BASE_URL = "http://10.68.160.189:9090/api"
}

@Serializable
data class QueryPayload(val message: String)

@Serializable
data class QueryResponse(val response: String)

expect suspend fun pickPdfFile(): FileData?

data class FileData(
    val name: String,
    val bytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FileData

        if (name != other.name) return false
        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}