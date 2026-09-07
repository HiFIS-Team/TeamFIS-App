package com.teamfis.app.shared

class Greeting {
    private val platform = currentPlatform()

    fun greet(): String = "Hello, ${platform.name}!"
}
