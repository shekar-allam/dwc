package org.unzer.project.ui.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import org.unzer.project.ConversationManager
import org.unzer.project.Message
import org.unzer.project.ui.component.JumpToBottom

@Composable
fun ConversationScreen(modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isSendButtonEnabled = ConversationManager.userQuery.value.isNotBlank()
    val isFileUploading = ConversationManager.isFileUploading.value

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Message list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                reverseLayout = true
            ) {
                items(ConversationManager.uiState.messages) { message ->
                    MessageItem(message)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Divider
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Bottom bar
            BottomBar(
                userQuery = ConversationManager.userQuery.value,
                onQueryChange = { ConversationManager.userQuery.value = it },
                onSendClick = { ConversationManager.sendQuery() },
                onUploadFile = { ConversationManager.uploadFile() },
                onClearContext = { ConversationManager.clearContext() },
                isSendButtonEnabled = isSendButtonEnabled,
                isDocumentSearchMode = ConversationManager.isDocumentSearchMode.value
            )

            // Status
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ConversationManager.status.value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        // Jump to Bottom
        val showJump = listState.firstVisibleItemIndex > 2
        JumpToBottom(
            enabled = showJump,
            onClicked = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // ⏳ Blocking Overlay Loader
        if (isFileUploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .zIndex(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}


private val ChatBubbleShape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)

@Composable
fun MessageItem(message: Message) {
    val alignment = if (message.author == "You") Alignment.TopEnd else Alignment.TopStart
    val backgroundBubbleColor = if (message.author == "You") {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.background
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Surface(
                    color = backgroundBubbleColor,
                    shape = ChatBubbleShape
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        // Text content
                        if (message.content.isNotBlank()) {
                            Text(
                                text = message.content,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        // Base64 image rendering with Coil 3
                        message.imageBase64?.let { base64 ->
                            PlatformBase64Image(
                                base64 = base64,
                                description = "Attached image",
                                modifier = Modifier
                                    .fillMaxWidth()

                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BottomBar(
    userQuery: String,
    onQueryChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onUploadFile: () -> Unit,
    onClearContext: () -> Unit,
    isSendButtonEnabled: Boolean,
    isDocumentSearchMode: Boolean // New state to track the mode
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            textStyle = MaterialTheme.typography.titleLarge,
            value = userQuery,
            onValueChange = onQueryChange,
            label = { Text("Type your question...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = {
                Icon(
                    imageVector = if (isDocumentSearchMode) Icons.Default.Description else Icons.Default.Search,
                    contentDescription = if (isDocumentSearchMode) "Document Search Mode" else "Generic Search Mode",
                    tint = if (isDocumentSearchMode) MaterialTheme.colorScheme.primary else Color.DarkGray
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.AttachFile,
                    contentDescription = "Attach PDF",
                    tint = Color.DarkGray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onUploadFile() }
                )
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSendClick,
            enabled = isSendButtonEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFd81b60),
                contentColor = Color.White,
                disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor.copy(
                    alpha = 0.5f
                ),
                disabledContentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Send")
        }
    }
}

@Composable
expect fun PlatformBase64Image(
    base64: String,
    modifier: Modifier = Modifier,
    description: String = ""
)