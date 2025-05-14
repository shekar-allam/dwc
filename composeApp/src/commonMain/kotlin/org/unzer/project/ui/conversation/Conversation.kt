//package org.unzer.project.ui.conversation
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.material3.Button
//import androidx.compose.material3.Divider
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
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
//import kotlinx.coroutines.launch
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.Json
//import org.unzer.project.FileData
//import org.unzer.project.httpClient
//import org.unzer.project.pickPdfFile
//
//@Composable
//fun ConversationContent(modifier: Modifier = Modifier) {
//    val coroutineScope = rememberCoroutineScope()
//    var status by remember { mutableStateOf("Awaiting input...") }
//    var userQuery by remember { mutableStateOf("") }
//    val messages =
//        remember { mutableStateListOf("Assistant: Hello! How can I help you today?") }
//    var uploadedFile by remember { mutableStateOf<FileData?>(null) }
//    var chatMode by remember { mutableStateOf(false) }
//    val listState = rememberLazyListState()
//
//    Box(modifier = modifier.fillMaxSize().padding(16.dp)) {
//        Column(modifier = Modifier.fillMaxSize()) {
//            LazyColumn(
//                state = listState,
//                modifier = Modifier.weight(1f).fillMaxWidth().padding(bottom = 8.dp),
//                reverseLayout = true
//            ) {
//                items(messages.size) { index ->
//                    val message = messages[messages.lastIndex - index]
//                    Text(
//                        text = message,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 4.dp),
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//            }
//
//            Divider(modifier = Modifier.padding(vertical = 8.dp))
//
//            Column(modifier = Modifier.fillMaxWidth()) {
//                OutlinedTextField(
//                    value = userQuery,
//                    onValueChange = { userQuery = it },
//                    label = { Text("Type your question...") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(Modifier.height(8.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Button(onClick = {
//                        coroutineScope.launch {
//                            status = "Picking file..."
//                            val file = pickPdfFile()
//                            if (file != null) {
//                                uploadedFile = file
//                                status = "Uploading file..."
//                                val uploadSuccess = uploadFileToServer(file)
//                                if (uploadSuccess) {
//                                    chatMode = true
//                                    status = "File uploaded. Chat mode activated."
//                                    messages.add("You uploaded: ${file.name}")
//                                } else {
//                                    uploadedFile = null
//                                    status = "File upload failed."
//                                }
//                            } else {
//                                status = "File upload cancelled or failed."
//                            }
//                        }
//                    }) {
//                        Text("Attach PDF")
//                    }
//
//                    Button(onClick = {
//                        coroutineScope.launch {
//                            if (userQuery.isNotBlank()) {
//                                messages.add("You: $userQuery")
//                                status = "Sending query..."
//                                val result = sendQueryToServer(userQuery, chatMode)
//                                messages.add("Assistant: $result")
//                                userQuery = ""
//                                status = "Response received"
//                            } else {
//                                status = "Please enter a question."
//                            }
//                        }
//                    }) {
//                        Text("Send")
//                    }
//                }
//
//                Spacer(Modifier.height(8.dp))
//
//                Button(onClick = {
//                    coroutineScope.launch {
//                        status = "Clearing context..."
//                        val cleared = clearChatContext()
//                        if (cleared) {
//                            chatMode = false
//                            uploadedFile = null
//                            status = "Context cleared. Switched to generic mode."
//                            messages.add("Assistant: Context cleared. Ready for generic queries.")
//                        } else {
//                            status = "Failed to clear context."
//                        }
//                    }
//                }) {
//                    Text("Clear Context")
//                }
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(status, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
//        }
//
//        val showJump = listState.firstVisibleItemIndex > 2
//
//        JumpToBottom(enabled = showJump, onClicked = {
//            coroutineScope.launch {
//                listState.animateScrollToItem(0)
//            }
//        }, modifier = Modifier.align(Alignment.BottomCenter))
//    }
//}
//
//@Serializable
//data class QueryPayload(val query: String)
//
//suspend fun sendQueryToServer(query: String, useChatMode: Boolean): String {
//    val endpoint = if (useChatMode) {
//        "$BASE_URL/chat"
//    } else {
//        "$BASE_URL/genericChat"
//    }
//    return try {
//        val response = httpClient.post(endpoint) {
//            contentType(ContentType.Application.Json)
//            setBody(Json.encodeToString(QueryPayload.serializer(), QueryPayload(query)))
//        }
//        val body = response.bodyAsText()
//        Json.decodeFromString(QueryResponse.serializer(), body).response
//    } catch (e: Exception) {
//        "Error: ${e.message}"
//    }
//}
//
//suspend fun uploadFileToServer(fileData: FileData): Boolean {
//    return try {
//        val response = httpClient.submitFormWithBinaryData(
//            url = "$BASE_URL/upload",
//            formData = formData {
//                append(
//                    key = "file",
//                    value = fileData.bytes,
//                    headers = Headers.build {
//                        append(HttpHeaders.ContentDisposition, "filename=\"${fileData.name}\"")
//                        append(HttpHeaders.ContentType, "application/pdf")
//                    }
//                )
//            }
//        )
//        response.status.isSuccess()
//    } catch (e: Exception) {
//        false
//    }
//}
//
//suspend fun clearChatContext(): Boolean {
//    return try {
//        val response = httpClient.post("$BASE_URL/clearContext") {
//            contentType(ContentType.Application.Json)
//            setBody("")
//        }
//        response.status.isSuccess()
//    } catch (e: Exception) {
//        false
//    }
//}
//
//const val BASE_URL = "http://10.0.2.2:8081/api"