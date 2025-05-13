package org.unzer.project

import javax.swing.JFileChooser

actual suspend fun pickPdfFile(): FileData? {
    val chooser = JFileChooser().apply {
        fileFilter = javax.swing.filechooser.FileNameExtensionFilter("PDF Files", "pdf")
    }

    val result = chooser.showOpenDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) {
        val file = chooser.selectedFile
        FileData(file.name, file.readBytes())
    } else null
}
