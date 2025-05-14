package org.unzer.project

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import io.ktor.client.HttpClient
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
import org.jetbrains.compose.resources.DrawableResource

@Immutable
data class Message(
    val author: String,
    val content: String,
    val image: DrawableResource? = null
)

class ConversationUiState(
    initialMessages: List<Message>
) {
    private val _messages: SnapshotStateList<Message> = mutableStateListOf(*initialMessages.toTypedArray())
    val messages: List<Message> = _messages

    fun addMessage(msg: Message) {
        _messages.add(0, msg)
    }
}

object ConversationManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val httpClient = HttpClient()

    val uiState = ConversationUiState(
        initialMessages = listOf(
            Message("Assistant", "Hello! How can I help you today?")
        )
    )

    val status = mutableStateOf("Awaiting input...")
    val userQuery = mutableStateOf("")
    val uploadedFile = mutableStateOf<FileData?>(null)
    val chatMode = mutableStateOf(false)

    fun sendQuery() {
        if (userQuery.value.isNotBlank()) {
            scope.launch {
                uiState.addMessage(Message("You", userQuery.value))
                status.value = "Sending query..."
                val result = sendQueryToServer(userQuery.value, chatMode.value)
                uiState.addMessage(Message("Assistant", result))
                userQuery.value = ""
                status.value = "Response received"
            }
        } else {
            status.value = "Please enter a question."
        }
    }

    fun uploadFile() {
        scope.launch {
            status.value = "Picking file..."
            val file = pickPdfFile()
            if (file != null) {
                uploadedFile.value = file
                status.value = "Uploading file..."
                val uploadSuccess = uploadFileToServer(file)
                if (uploadSuccess) {
                    chatMode.value = true
                    status.value = "File uploaded. Chat mode activated."
                    uiState.addMessage(Message("You", "Uploaded file: ${file.name}"))
                } else {
                    uploadedFile.value = null
                    status.value = "File upload failed."
                }
            } else {
                status.value = "File upload cancelled or failed."
            }
        }
    }

    fun clearContext() {
        scope.launch {
            status.value = "Clearing context..."
            val cleared = clearChatContext()
            if (cleared) {
                chatMode.value = false
                uploadedFile.value = null
                status.value = "Context cleared. Switched to generic mode."
                uiState.addMessage(Message("Assistant", "Context cleared. Ready for generic queries."))
            } else {
                status.value = "Failed to clear context."
            }
        }
    }

    private suspend fun sendQueryToServer(query: String, useChatMode: Boolean): String {
        val endpoint = if (useChatMode) "$BASE_URL/chat" else "$BASE_URL/genericChat"
        return try {
            val response = httpClient.post(endpoint) {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(QueryPayload.serializer(), QueryPayload(query)))
            }
            val body = response.bodyAsText()
            Json.decodeFromString(QueryResponse.serializer(), body).response
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    private suspend fun uploadFileToServer(fileData: FileData): Boolean {
        return try {
            val response = httpClient.submitFormWithBinaryData(
                url = "$BASE_URL/upload",
                formData = formData {
                    append(
                        key = "file",
                        value = fileData.bytes,
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"${fileData.name}\"")
                            append(HttpHeaders.ContentType, "application/pdf")
                        }
                    )
                }
            )
            response.status.isSuccess()
        } catch (e: Exception) {
            false
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

    private const val BASE_URL = "http://10.0.2.2:8081/api"
}

@Serializable
data class QueryPayload(val query: String)

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