package org.unzer.project.ui.conversation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.unzer.project.ConversationManager
import org.unzer.project.Message
import org.unzer.project.ui.component.JumpToBottom

@Composable
fun ConversationScreen(modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isSendButtonEnabled =
        ConversationManager.userQuery.value.isNotBlank() // Enable Send button if text field is not empty

    Box(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Message list
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth().padding(bottom = 8.dp),
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

            // Status text
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ConversationManager.status.value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        // Jump to Bottom button
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
    }
}

@Composable
fun MessageItem(message: Message) {
    val alignment = if (message.author == "You") Alignment.TopEnd else Alignment.TopStart
    val avatarColor = if (message.author == "You") Color.Blue else Color.Gray

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar for Assistant (left side)
            if (message.author != "You") {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message.author.first().toString().uppercase(),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Message bubble
            Column(
                modifier = Modifier
                    .background(
                        color = if (message.author == "You") Color.LightGray else Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(text = message.author, style = MaterialTheme.typography.titleMedium)
                Text(text = message.content, style = MaterialTheme.typography.bodyMedium)
                if (message.image != null) {
                    Image(
                        painter = painterResource(message.image),
                        contentDescription = "Attached image",
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                    )
                }
            }

            // Avatar for You (right side)
            if (message.author == "You") {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message.author.first().toString().uppercase(),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium
                    )
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
            value = userQuery,
            onValueChange = onQueryChange,
            label = { Text("Type your question...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = {
                Icon(
                    imageVector = if (isDocumentSearchMode) Icons.Default.Description else Icons.Default.Search,
                    contentDescription = if (isDocumentSearchMode) "Document Search Mode" else "Generic Search Mode",
                    tint = if (isDocumentSearchMode) Color.Blue else Color.Gray
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.AttachFile,
                    contentDescription = "Attach PDF",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onUploadFile() }
                )
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Clear Context button
            OutlinedButton (
                modifier = Modifier.weight(0.5f),
                onClick = onClearContext,
                enabled = isDocumentSearchMode, // Only enabled in Document Search mode
                border = ButtonDefaults.outlinedButtonBorder(true),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Clear Context")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                modifier = Modifier.weight(0.5f),
                onClick = onSendClick,
                enabled = isSendButtonEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFd81b60),
                    contentColor = Color.White,
                    disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor.copy(alpha = 0.5f),
                    disabledContentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Send")
            }
        }
    }
}