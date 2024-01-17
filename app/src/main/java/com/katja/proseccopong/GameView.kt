package com.katja.proseccopong

interface GameView {

    fun ballDown(): Boolean

    fun gameEnd()

    fun playPlatformSound()
    fun playGlassSound()

    // Method to handle glass breakage event
    fun handleGlassBreakage() {
    }

}

