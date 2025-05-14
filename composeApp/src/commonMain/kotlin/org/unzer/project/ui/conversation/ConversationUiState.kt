package org.unzer.project.ui.conversation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.toMutableStateList

class ConversationUiState(
    initialMessages: List<Message>
) {
    private val _messages: MutableList<Message> = initialMessages.toMutableStateList()
    val messages: List<Message> = _messages

    fun addMessage(msg: Message) {
        _messages.add(0, msg)
    }
}

@Immutable
data class Message(
    val author: String,
    val content: String,
    val image: Int? = null,
)
