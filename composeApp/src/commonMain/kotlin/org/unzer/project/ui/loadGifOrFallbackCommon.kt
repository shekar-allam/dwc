//package org.unzer.project
//
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.graphics.painter.Painter
//import dwc.composeapp.generated.resources.Res
//import org.jetbrains.compose.resources.painterResource
//
//@Composable
//internal expect fun loadGif(fileName: String): Painter
//
//// CommonMain
//@Composable
//internal fun loadGifOrFallbackCommon(fileName: String): Painter {
//    return try {
//        loadGif(fileName)
//    } catch (e: Exception) {
//        painterResource(Res.drawable.lo())
//    }
//}
//
