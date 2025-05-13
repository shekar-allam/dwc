package org.unzer.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform