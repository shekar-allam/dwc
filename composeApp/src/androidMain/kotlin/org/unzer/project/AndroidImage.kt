package org.unzer.project

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import okio.ByteString.Companion.decodeBase64

@Composable
fun AndroidBase64Image(base64: String, modifier: Modifier = Modifier, description: String = "") {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(base64.decodeBase64()?.toByteArray())
            .listener(
                onError = { request, throwable ->
                    Log.e("Coil", "Base64 image failed", throwable.throwable)
                },
                onSuccess = { _, _ ->
                    Log.d("Coil", "Base64 image loaded successfully")
                }
            )
            .build(),
        contentDescription = description,
        contentScale = ContentScale.Inside,
        modifier = modifier
    )
}
