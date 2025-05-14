package org.unzer.project

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import dwc.composeapp.generated.resources.Res
import dwc.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.unzer.project.ui.conversation.ConversationScreen
import org.unzer.project.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun App() {
    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        AsyncImage(
                            model = Res.getUri("drawable/logo.png"),
                            contentDescription = stringResource(Res.string.app_name),
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { paddingValues ->
            ConversationScreen(modifier = Modifier.padding(paddingValues))
        }
    }
}
