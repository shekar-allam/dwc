package org.unzer.project.ui.conversation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.unzer.project.AndroidBase64Image

@Composable
actual fun PlatformBase64Image(
    base64: String,
    modifier: Modifier,
    description: String
) {
    AndroidBase64Image(base64, modifier, description)
}