package org.unzer.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            ChatAssistantScreen()
        }
    }
}


@Composable
fun ChatAssistantScreen() {
    val coroutineScope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Awaiting input...") }
    var userQuery by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf("Assistant: Hello! How can I help you today?") }
    var uploadedFile by remember { mutableStateOf<FileData?>(null) }
    var chatMode by remember { mutableStateOf(false) } // false = genericChat, true = chat

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // Chat history display
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(bottom = 8.dp),
            reverseLayout = true
        ) {
            items(messages.size) { index ->
                val message = messages[messages.lastIndex - index]
                Text(
                    text = message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    style = MaterialTheme.typography.body1
                )
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Query input and action buttons
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = userQuery,
                onValueChange = { userQuery = it },
                label = { Text("Type your question...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = {
                    coroutineScope.launch {
                        status = "Picking file..."
                        val file = pickPdfFile()
                        if (file != null) {
                            uploadedFile = file
                            status = "Uploading file..."
                            val uploadSuccess = uploadFileToServer(file)
                            if (uploadSuccess) {
                                chatMode = true
                                status = "File uploaded. Chat mode activated."
                                messages.add("You uploaded: ${file.name}")
                            } else {
                                uploadedFile = null
                                status = "File upload failed."
                            }
                        } else {
                            status = "File upload cancelled or failed."
                        }
                    }
                }) {
                    Text("Attach PDF")
                }

                Button(onClick = {
                    coroutineScope.launch {
                        if (userQuery.isNotBlank()) {
                            messages.add("You: $userQuery")
                            status = "Sending query..."
                            val result = sendQueryToServer(userQuery, chatMode)
                            messages.add("Assistant: $result")
                            userQuery = ""
                            status = "Response received"
                        } else {
                            status = "Please enter a question."
                        }
                    }
                }) {
                    Text("Send")
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(onClick = {
                coroutineScope.launch {
                    status = "Clearing context..."
                    val cleared = clearChatContext()
                    if (cleared) {
                        chatMode = false
                        uploadedFile = null
                        status = "Context cleared. Switched to generic mode."
                        messages.add("Assistant: Context cleared. Ready for generic queries.")
                    } else {
                        status = "Failed to clear context."
                    }
                }
            }) {
                Text("Clear Context")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(status, style = MaterialTheme.typography.caption, color = Color.Gray)
    }
}

@Serializable
data class QueryPayload(val query: String)

suspend fun sendQueryToServer(query: String, useChatMode: Boolean): String {
    val endpoint = if (useChatMode) {
        "http://10.68.160.105:8081/api/chat"
    } else {
        "http://10.68.160.105:8081/api/genericChat"
    }

    return try {
        val response = httpClient.post(endpoint) {
            contentType(io.ktor.http.ContentType.Application.Json)
            setBody(Json.encodeToString(QueryPayload(query)))
        }
        response.bodyAsText()
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}

suspend fun uploadFileToServer(fileData: FileData): Boolean {
    return try {
        val response = httpClient.submitFormWithBinaryData(
            url = "http://10.68.160.105:8081/api/upload",
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

suspend fun clearChatContext(): Boolean {
    return try {
        val response = httpClient.post("http://10.68.160.105:8081/api/clearContext") {
            contentType(io.ktor.http.ContentType.Application.Json)
            setBody("")
        }
        response.status.isSuccess()
    } catch (e: Exception) {
        false
    }
}

