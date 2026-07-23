package com.smartfoodup.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform