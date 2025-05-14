package org.unzer.project.ui.conversation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.unzer.project.ui.component.JumpToBottom

@Composable
fun ConversationContent(
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth().padding(bottom = 8.dp),
                reverseLayout = true
            ) {
                items(ConversationManager.messages.size) { index ->
                    val message = ConversationManager.messages[ConversationManager.messages.lastIndex - index]
                    Text(
                        text = message,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
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

                Spacer(Modifier.height(8.dp))

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

                Spacer(Modifier.height(8.dp))

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