package com.teamfis.app.shared

interface Platform {
    val name: String
}

expect fun currentPlatform(): Platform
