package org.unzer.project

import kotlinx.browser.document
import kotlinx.coroutines.suspendCancellableCoroutine
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.w3c.dom.HTMLInputElement
import org.w3c.files.FileReader
import kotlin.coroutines.resume

actual suspend fun pickPdfFile(): FileData? = suspendCancellableCoroutine { cont ->
    val input = document.createElement("input") as HTMLInputElement
    input.type = "file"

    // Accept multiple MIME types
    input.accept = listOf(
        "application/pdf",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "text/csv",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "text/plain"
    ).joinToString(",")

    input.onchange = {
        val file = input.files?.item(0)
        if (file != null) {
            val reader = FileReader()

            reader.onloadend = {
                val result = reader.result
                if (result is ArrayBuffer) {
                    val int8Array = Int8Array(result)
                    val bytes = ByteArray(int8Array.length) { i -> int8Array[i] }
                    cont.resume(FileData(file.name, bytes))
                } else {
                    cont.resume(null) // Skip string results or unknowns
                }
            }

            reader.onerror = {
                cont.resume(null)
            }

            reader.readAsArrayBuffer(file)
        } else {
            cont.resume(null)
        }
    }

    input.click()
}

