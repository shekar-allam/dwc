//package org.unzer.project
//
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.graphics.painter.Painter
//import coil.compose.rememberAsyncImagePainter
//import org.jetbrains.compose.resources.painterResource
//
//actual object ImageLoader {
//    @Composable
//    actual fun loadGifOrFallback(resourcePath: String): Painter {
//        return try {
//            rememberAsyncImagePainter(model = "file:///android_asset/$resourcePath")
//        } catch (e: Exception) {
//            painterResource("logo.png") // Fallback to static image
//        }
//    }
//}