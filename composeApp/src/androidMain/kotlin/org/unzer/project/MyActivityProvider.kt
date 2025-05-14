package org.unzer.project

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import kotlinx.coroutines.channels.Channel

object MyActivityProvider {
    internal var activity: ComponentActivity? = null
    private val filePickChannel = Channel<FileData?>(Channel.RENDEZVOUS)
    private var launchFilePicker: (() -> Unit)? = null

    fun setActivity(componentActivity: ComponentActivity) {
        activity = componentActivity
    }

    fun clearActivity() {
        activity = null
        launchFilePicker = null
    }

    fun setFilePickerLauncher(launcher: () -> Unit) {
        launchFilePicker = launcher
    }

    fun onFilePicked(uri: Uri?) {
        val activity = activity ?: return
        if (uri == null) {
            filePickChannel.trySend(null)
            return
        }

        try {
            val inputStream = activity.contentResolver.openInputStream(uri)
            val name = activity.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex != -1) {
                    cursor.getString(nameIndex)
                } else "file.pdf"
            } ?: "file.pdf"

            val bytes = inputStream?.readBytes()
            filePickChannel.trySend(if (bytes != null) FileData(name, bytes) else null)
        } catch (e: Exception) {
            filePickChannel.trySend(null)
        }
    }

    suspend fun pickFile(): FileData? {
        launchFilePicker?.invoke() ?: return null
        return filePickChannel.receive()
    }
}




