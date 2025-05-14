package org.unzer.project.ui.conversation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.toMutableStateList
import dwc.composeapp.generated.resources.Res
import dwc.composeapp.generated.resources.ali
import dwc.composeapp.generated.resources.someone_else
import org.jetbrains.compose.resources.DrawableResource

class ConversationUiState(
    val channelName: String,
    val channelMembers: Int,
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
    val timestamp: String,
    val image: Int? = null,
    val authorImage: DrawableResource = if (author == "me") Res.drawable.ali else Res.drawable.someone_else,
)
