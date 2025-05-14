//package org.unzer.project
//
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.graphics.painter.Painter
//import org.jetbrains.compose.resources.painterResource
//
//@Composable
//expect fun ImageLoader.Companion.loadGifOrFallback(fileName: String): Painter
//
//object ImageLoader {
//    @Composable
//    fun loadGifOrFallback(fileName: String): Painter {
//        return loadGifOrFallbackCommon(fileName)
//    }
//}
//
//
//@Composable
//internal expect fun loadGifOrFallbackCommon(fileName: String): Painter
//
