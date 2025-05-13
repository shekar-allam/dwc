package org.unzer.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {

    private lateinit var filePickerLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MyActivityProvider.setActivity(this)
        filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            MyActivityProvider.onFilePicked(uri)
        }
        MyActivityProvider.setFilePickerLauncher {
            filePickerLauncher.launch("application/pdf") // Only PDFs
        }

        setContent {
            App()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MyActivityProvider.clearActivity()
    }
}

