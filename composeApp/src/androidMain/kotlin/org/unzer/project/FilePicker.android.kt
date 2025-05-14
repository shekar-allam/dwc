package org.unzer.project

actual suspend fun pickPdfFile(): FileData? {
    return MyActivityProvider.pickFile()
}

