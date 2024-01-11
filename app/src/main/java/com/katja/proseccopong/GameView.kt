package com.katja.proseccopong

interface GameView {

    fun ballDown() : Boolean

    fun gameEnd()


    // Method to handle glass breakage event
    fun handleGlassBreakage() {
    }

    fun initializeMediaPLayer()
    fun playHitSoundEffect()
    fun playGlassSoundEffect()
}