package org.unzer.project

sealed class QueryResult {
    data class Text(val text: String) : QueryResult()
    data class Base64Image(val base64Data: String) : QueryResult()
    data class Error(val message: String) : QueryResult()
}