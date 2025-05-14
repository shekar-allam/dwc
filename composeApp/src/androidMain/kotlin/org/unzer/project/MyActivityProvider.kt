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
            val contentResolver = activity.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            val name = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex != -1) {
                    cursor.getString(nameIndex)
                } else {
                    // fallback name with extension guess
                    "file.${getExtensionFromMime(contentResolver.getType(uri))}"
                }
            } ?: "file.${getExtensionFromMime(contentResolver.getType(uri))}"

            val bytes = inputStream?.readBytes()
            filePickChannel.trySend(if (bytes != null) FileData(name, bytes) else null)
        } catch (e: Exception) {
            e.printStackTrace()
            filePickChannel.trySend(null)
        }
    }

    suspend fun pickFile(): FileData? {
        launchFilePicker?.invoke() ?: return null
        return filePickChannel.receive()
    }

    private fun getExtensionFromMime(mimeType: String?): String {
        return when (mimeType) {
            "application/pdf" -> "pdf"
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> "xlsx"
            "text/csv" -> "csv"
            "application/msword" -> "doc"
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> "docx"
            "text/plain" -> "txt"
            else -> "bin"
        }
    }
}



