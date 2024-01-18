package com.katja.proseccopong

interface GameView {

    fun ballDown(): Boolean

    fun gameEnd()

    fun playPlatformSound()
    fun playGlassSound()

    // Metod för att hantera händelsen av glassplittring
    fun handleGlassBreakage() {
    }

}

