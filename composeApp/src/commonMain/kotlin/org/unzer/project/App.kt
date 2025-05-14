package org.unzer.project

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.unzer.project.ui.ChatScreen
import org.unzer.project.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            AsyncImage(
//                                painter = Res.getUri("drawable/logo.jpg"),
//                                contentDescription = "Logo",
//                                modifier = Modifier.size(32.dp).padding(end = 8.dp)
//                            )
//                            Text("ewwef")
//                        }
                        Text("ewwef")
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { paddingValues ->
            ChatScreen(modifier = Modifier.padding(paddingValues))
        }
    }
}
