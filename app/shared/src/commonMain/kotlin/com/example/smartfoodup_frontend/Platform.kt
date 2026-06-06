package com.example.smartfoodup_frontend

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform