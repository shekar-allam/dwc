package org.unzer.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {

    private lateinit var filePickerLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MyActivityProvider.setActivity(this)

        filePickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            MyActivityProvider.onFilePicked(uri)
        }

        // Launch picker with supported MIME types
        MyActivityProvider.setFilePickerLauncher {
            val supportedMimeTypes = arrayOf(
                "application/pdf",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
                "text/csv",
                "application/msword",                                               // .doc
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
                "text/plain"
            )
            filePickerLauncher.launch(supportedMimeTypes)
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


