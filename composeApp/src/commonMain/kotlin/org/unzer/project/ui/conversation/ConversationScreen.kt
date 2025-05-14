package org.unzer.project.ui.conversation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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

    Box(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
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

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = ConversationManager.userQuery.value,
                    onValueChange = { ConversationManager.userQuery.value = it },
                    label = { Text("Type your question...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { ConversationManager.uploadFile() }) {
                        Text("Attach PDF")
                    }

                    Button(onClick = { ConversationManager.sendQuery() }) {
                        Text("Send")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = { ConversationManager.clearContext() }) {
                    Text("Clear Context")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ConversationManager.status.value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

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
    val alignment = if (message.author == "me") Alignment.TopEnd else Alignment.TopStart
    val avatarColor = if (message.author == "me") Color.Blue else Color.Gray

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar for others (left side)
            if (message.author != "me") {
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
                        color = if (message.author == "me") Color.LightGray else Color.White,
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

            // Avatar for "me" (right side)
            if (message.author == "me") {
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